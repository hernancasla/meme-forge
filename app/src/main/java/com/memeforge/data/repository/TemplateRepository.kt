package com.memeforge.data.repository

import android.content.Context
import com.memeforge.R
import com.memeforge.data.model.MemeTemplate
import com.memeforge.data.model.TextZone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for meme templates.
 *
 * Loading strategy (stale-while-revalidate):
 *   1. Emit disk cache immediately  → user sees content with zero wait
 *      (first install: no cache → fall back to bundled APK JSON as seed)
 *   2. Fetch CDN in background      → emit updated list, overwrite cache
 *   3. CDN failure                  → keep whatever was emitted in step 1
 *
 * Result: new templates pushed to GitHub appear on the next app launch
 * (after jsDelivr propagates, ~5–10 min) — no recompile ever needed.
 */
@Singleton
class TemplateRepository @Inject constructor(
    private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }

    private val remoteUrl =
        "https://cdn.jsdelivr.net/gh/hernancasla/meme-forge@main/templates/templates.json"

    private val cacheFile: File
        get() = File(context.filesDir, "templates_cache.json")

    /** In-memory snapshot kept in sync by streamTemplates(); used by getTemplateById(). */
    @Volatile
    private var liveTemplates: List<MemeTemplate> = emptyList()

    // Keep premium templates separate; they are never fetched remotely.
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

    /**
     * Cold Flow that implements stale-while-revalidate:
     *
     *  emit #1 — disk cache (fast, offline-capable) or bundled seed on first install
     *  emit #2 — CDN response when it arrives (may be same list or contain new templates)
     *
     * Collectors always get fresh content as soon as it is available.
     */
    fun streamTemplates(): Flow<List<MemeTemplate>> = flow {
        // ── Step 1: serve from disk immediately ──────────────────────────────
        val initial = loadCached() ?: loadBundled()
        liveTemplates = initial
        emit(initial)

        // ── Step 2: refresh from CDN in background ───────────────────────────
        try {
            val connection = URL(remoteUrl).openConnection() as HttpURLConnection
            connection.connectTimeout = 8_000
            connection.readTimeout = 8_000
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val remote: List<MemeTemplate> = json.decodeFromString(body)
            cacheFile.writeText(body)          // persist for next cold start
            liveTemplates = remote
            emit(remote)
        } catch (_: Exception) {
            // Network/CDN unavailable — initial list is already showing, nothing to do
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Looks up a single template by id.
     * Uses the in-memory live list so CDN-only templates (not yet in the bundled APK)
     * are always reachable from the editor.
     */
    fun getTemplateById(id: String): MemeTemplate? =
        (liveTemplates.ifEmpty { loadBundled() } + premiumTemplates).find { it.id == id }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun loadCached(): List<MemeTemplate>? {
        if (!cacheFile.exists()) return null
        return try {
            json.decodeFromString(cacheFile.readText())
        } catch (_: Exception) {
            cacheFile.delete()
            null
        }
    }

    /** Last-resort seed: used only on first install when there is no disk cache. */
    private fun loadBundled(): List<MemeTemplate> {
        val stream = context.resources.openRawResource(R.raw.templates)
        return json.decodeFromString(stream.bufferedReader().use { it.readText() })
    }
}
