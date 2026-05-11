package com.memeforge.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import java.io.File

object StickerExporter {

    private const val STICKER_SIZE = 512

    fun exportAsWebP(source: Bitmap, outputFile: File) {
        val sticker = buildStickerBitmap(source)
        outputFile.outputStream().use { out ->
            @Suppress("DEPRECATION")
            val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                Bitmap.CompressFormat.WEBP_LOSSLESS
            else
                Bitmap.CompressFormat.WEBP
            sticker.compress(format, 100, out)
        }
        sticker.recycle()
    }

    // Escala el bitmap al cuadrado 512×512 manteniendo aspect ratio, centrado, fondo transparente
    private fun buildStickerBitmap(source: Bitmap): Bitmap {
        val result = Bitmap.createBitmap(STICKER_SIZE, STICKER_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)

        val scale = minOf(
            STICKER_SIZE.toFloat() / source.width,
            STICKER_SIZE.toFloat() / source.height
        )
        val scaledW = (source.width * scale).toInt()
        val scaledH = (source.height * scale).toInt()
        val left = (STICKER_SIZE - scaledW) / 2f
        val top = (STICKER_SIZE - scaledH) / 2f

        val scaled = Bitmap.createScaledBitmap(source, scaledW, scaledH, true)
        canvas.drawBitmap(scaled, left, top, null)
        scaled.recycle()

        return result
    }
}
