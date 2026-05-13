package com.memeforge.data.repository

import android.content.Context
import com.memeforge.R
import com.memeforge.data.model.MemeTemplate
import com.memeforge.data.model.TextZone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class TemplateRepository @Inject constructor(
    private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }

    private val remoteUrl =
        "https://cdn.jsdelivr.net/gh/hernancasla/meme-forge@main/templates/templates.json"

    private val cacheFile: File
        get() = File(context.filesDir, "templates_cache.json")

    /** In-memory snapshot of the most recent successful template list (bundled or remote). */
    private var liveTemplates: List<MemeTemplate> = emptyList()

    private val premiumTemplates = listOf(
        MemeTemplate(
            id = "premium_red",
            name = "Fondo Rojo",
            imageUrl = "https://placehold.co/800x600/FF0000/FFFFFF?text=Premium",
            category = "premium",
            textZones = listOf(TextZone("center", "Centro", 0.5f, 0.5f, "Texto aquí"))
        ),
        MemeTemplate(
            id = "premium_blue",
            name = "Fondo Azul",
            imageUrl = "https://placehold.co/800x600/0000FF/FFFFFF?text=Premium",
            category = "premium",
            textZones = listOf(TextZone("center", "Centro", 0.5f, 0.5f, "Texto aquí"))
        ),
        MemeTemplate(
            id = "premium_green",
            name = "Fondo Verde",
            imageUrl = "https://placehold.co/800x600/00AA00/FFFFFF?text=Premium",
            category = "premium",
            textZones = listOf(TextZone("center", "Centro", 0.5f, 0.5f, "Texto aquí"))
        )
    )

    // Carga sincrónica: siempre arranca con el bundled del APK (fuente de verdad offline)
    fun getTemplates(): List<MemeTemplate> {
        val bundled = loadBundled()
        liveTemplates = bundled
        return bundled
    }

    // Fetch remoto en background — si tiene éxito actualiza el cache y devuelve la lista nueva;
    // si el CDN falla usamos el bundled del APK (no el caché en disco, que puede ser más viejo).
    suspend fun refreshTemplates(): List<MemeTemplate> = withContext(Dispatchers.IO) {
        try {
            val connection = URL(remoteUrl).openConnection() as HttpURLConnection
            connection.connectTimeout = 5_000
            connection.readTimeout = 5_000
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            cacheFile.writeText(body)
            val remote: List<MemeTemplate> = json.decodeFromString(body)
            liveTemplates = remote
            remote
        } catch (e: Exception) {
            // Caché en disco puede ser más viejo que el bundled → siempre preferir bundled
            loadBundled().also { liveTemplates = it }
        }
    }

    // Busca en la lista "viva" (puede incluir templates del CDN no presentes en el bundled)
    fun getTemplateById(id: String): MemeTemplate? =
        (liveTemplates.ifEmpty { loadBundled() } + premiumTemplates).find { it.id == id }

    private fun loadCached(): List<MemeTemplate>? {
        if (!cacheFile.exists()) return null
        return try {
            json.decodeFromString(cacheFile.readText())
        } catch (e: Exception) {
            cacheFile.delete()
            null
        }
    }

    private fun loadBundled(): List<MemeTemplate> {
        val stream = context.resources.openRawResource(R.raw.templates)
        return json.decodeFromString(stream.bufferedReader().use { it.readText() })
    }
}
