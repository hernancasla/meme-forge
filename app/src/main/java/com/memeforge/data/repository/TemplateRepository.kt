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

    // Carga sincrónica: cache local primero, si no existe usa el bundled del APK
    fun getTemplates(): List<MemeTemplate> = loadCached() ?: loadBundled()

    // Fetch remoto en background — actualiza el cache y devuelve la lista nueva
    suspend fun refreshTemplates(): List<MemeTemplate> = withContext(Dispatchers.IO) {
        try {
            val connection = URL(remoteUrl).openConnection() as HttpURLConnection
            connection.connectTimeout = 5_000
            connection.readTimeout = 5_000
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            cacheFile.writeText(body)
            json.decodeFromString(body)
        } catch (e: Exception) {
            // Sin red o URL no configurada: devuelve lo que ya hay
            getTemplates()
        }
    }

    fun getTemplateById(id: String): MemeTemplate? =
        (getTemplates() + premiumTemplates).find { it.id == id }

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
