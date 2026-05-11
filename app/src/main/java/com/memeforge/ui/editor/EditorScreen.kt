package com.memeforge.ui.editor

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.memeforge.R
import com.memeforge.ui.components.MemeCanvas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    templateId: String,
    onBack: () -> Unit,
    viewModel: EditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var sourceBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(templateId) {
        viewModel.loadTemplate(templateId)
    }

    // Cargar el bitmap de la plantilla para renderizado
    LaunchedEffect(uiState.template?.imageUrl) {
        uiState.template?.imageUrl?.let { url ->
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false)
                .build()
            val result = withContext(Dispatchers.IO) { context.imageLoader.execute(request) }
            if (result is SuccessResult) {
                sourceBitmap = (result.drawable as? BitmapDrawable)?.bitmap
            }
        }
    }

    LaunchedEffect(uiState.savedUri) {
        if (uiState.savedUri != null) {
            Toast.makeText(context, context.getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show()
        }
    }

    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = viewModel::clearError,
            title = { Text(stringResource(R.string.error_saving)) },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = viewModel::clearError) { Text("OK") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.template_editor)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            uiState.previewBitmap?.let { bmp ->
                MemeCanvas(
                    bitmap = bmp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp)
                )
            } ?: AsyncImage(
                model = uiState.template?.imageUrl,
                contentDescription = uiState.template?.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(12.dp))

            uiState.template?.textZones?.forEach { zone ->
                val text = uiState.memeTexts[zone.id]?.content ?: zone.defaultText
                OutlinedTextField(
                    value = text,
                    onValueChange = { viewModel.updateText(zone.id, it) },
                    label = { Text(zone.label) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { sourceBitmap?.let { viewModel.generatePreview(it) } },
                    modifier = Modifier.weight(1f),
                    enabled = sourceBitmap != null
                ) {
                    Text(stringResource(R.string.btn_preview))
                }

                Button(
                    onClick = { sourceBitmap?.let { viewModel.save(it) } },
                    modifier = Modifier.weight(1f),
                    enabled = sourceBitmap != null && !uiState.isSaving
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text(stringResource(R.string.btn_save))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val shareUri = uiState.savedUri ?: run {
                        // Si no se guardó en galería, compartir desde bitmap en cache
                        val bmp = uiState.previewBitmap ?: sourceBitmap ?: return@Button
                        val file = File(context.cacheDir, "meme_share.jpg")
                        file.outputStream().use { bmp.compress(Bitmap.CompressFormat.JPEG, 95, it) }
                        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                    }
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/jpeg"
                        putExtra(Intent.EXTRA_STREAM, shareUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_meme)))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                enabled = uiState.previewBitmap != null || uiState.savedUri != null
            ) {
                Text(stringResource(R.string.btn_share))
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
