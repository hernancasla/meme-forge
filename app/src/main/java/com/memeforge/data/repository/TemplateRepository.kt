package com.memeforge.data.repository

import android.content.Context
import com.memeforge.R
import com.memeforge.data.model.MemeTemplate
import com.memeforge.data.model.TextZone
import kotlinx.serialization.json.Json
import javax.inject.Inject

class TemplateRepository @Inject constructor(
    private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }

    private val premiumTemplates = listOf(
        MemeTemplate(
            id = "premium_red",
            name = "Fondo Rojo",
            imageUrl = "https://via.placeholder.com/800x600/FF0000/FFFFFF?text=Premium",
            category = "premium",
            textZones = listOf(TextZone("center", "Centro", 0.5f, 0.5f, "Texto aquí"))
        ),
        MemeTemplate(
            id = "premium_blue",
            name = "Fondo Azul",
            imageUrl = "https://via.placeholder.com/800x600/0000FF/FFFFFF?text=Premium",
            category = "premium",
            textZones = listOf(TextZone("center", "Centro", 0.5f, 0.5f, "Texto aquí"))
        ),
        MemeTemplate(
            id = "premium_green",
            name = "Fondo Verde",
            imageUrl = "https://via.placeholder.com/800x600/00AA00/FFFFFF?text=Premium",
            category = "premium",
            textZones = listOf(TextZone("center", "Centro", 0.5f, 0.5f, "Texto aquí"))
        )
    )

    fun getTemplates(): List<MemeTemplate> {
        val inputStream = context.resources.openRawResource(R.raw.templates)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return json.decodeFromString(jsonString)
    }

    fun getPremiumTemplates(): List<MemeTemplate> = premiumTemplates

    fun getTemplateById(id: String): MemeTemplate? =
        (getTemplates() + premiumTemplates).find { it.id == id }
}
