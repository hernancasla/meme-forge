package com.memeforge.ui.editor

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memeforge.data.model.MemeTemplate
import com.memeforge.data.model.MemeText
import com.memeforge.data.repository.TemplateRepository
import com.memeforge.util.AdManager
import com.memeforge.util.MemeRenderer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private val SAVE_COUNT_KEY = intPreferencesKey("save_count")

data class EditorUiState(
    val template: MemeTemplate? = null,
    val memeTexts: Map<String, MemeText> = emptyMap(),
    val previewBitmap: Bitmap? = null,
    val isSaving: Boolean = false,
    val showInterstitial: Boolean = false,
    val shareUri: Uri? = null,
    val premiumUnlocked: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val repository: TemplateRepository,
    private val dataStore: DataStore<Preferences>,
    val adManager: AdManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init {
        adManager.loadInterstitial(context)
        adManager.loadRewarded(context)
    }

    fun loadTemplate(templateId: String) {
        val template = repository.getTemplateById(templateId) ?: return
        val initialTexts = template.textZones.associate { zone ->
            zone.id to MemeText(
                zoneId = zone.id,
                content = zone.defaultText,
                xRatio = zone.defaultX,
                yRatio = zone.defaultY
            )
        }
        _uiState.value = _uiState.value.copy(template = template, memeTexts = initialTexts)
    }

    fun updateText(zoneId: String, content: String) {
        val current = _uiState.value
        val existing = current.memeTexts[zoneId]
        val updated = existing?.copy(content = content)
            ?: MemeText(zoneId = zoneId, content = content, xRatio = 0.5f, yRatio = 0.5f)
        _uiState.value = current.copy(memeTexts = current.memeTexts + (zoneId to updated))
    }

    fun generatePreview(sourceBitmap: Bitmap) {
        val current = _uiState.value
        val rendered = MemeRenderer.renderMeme(
            sourceBitmap,
            current.memeTexts.values.toList(),
            sourceBitmap.width,
            sourceBitmap.height
        )
        _uiState.value = current.copy(previewBitmap = rendered)
    }

    fun requestSave(sourceBitmap: Bitmap) {
        viewModelScope.launch {
            val saveCount = dataStore.data.first()[SAVE_COUNT_KEY] ?: 0
            // Mostrar interstitial en cada segunda creación
            if (saveCount > 0 && saveCount % 2 == 0) {
                _uiState.value = _uiState.value.copy(showInterstitial = true)
            } else {
                performSave(sourceBitmap)
            }
        }
    }

    fun onInterstitialHandled(sourceBitmap: Bitmap) {
        _uiState.value = _uiState.value.copy(showInterstitial = false)
        viewModelScope.launch { performSave(sourceBitmap) }
    }

    private suspend fun performSave(sourceBitmap: Bitmap) {
        _uiState.value = _uiState.value.copy(isSaving = true)
        try {
            val current = _uiState.value
            val rendered = MemeRenderer.renderMeme(
                sourceBitmap,
                current.memeTexts.values.toList(),
                sourceBitmap.width,
                sourceBitmap.height
            )
            val uri = saveBitmapToGallery(rendered)
            dataStore.edit { it[SAVE_COUNT_KEY] = (it[SAVE_COUNT_KEY] ?: 0) + 1 }
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                previewBitmap = rendered,
                shareUri = uri
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
        }
    }

    private fun saveBitmapToGallery(bitmap: Bitmap): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "meme_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MemeForge")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val update = ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }
                context.contentResolver.update(it, update, null, null)
            }
        }
        return uri
    }

    fun unlockPremium() {
        _uiState.value = _uiState.value.copy(premiumUnlocked = true)
    }

    fun clearShareUri() {
        _uiState.value = _uiState.value.copy(shareUri = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
