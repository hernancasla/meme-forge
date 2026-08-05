package com.memeforge.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import com.memeforge.data.model.MemeText

object MemeRenderer {

    private const val MIN_FONT_SIZE = 16f
    private const val FONT_STEP = 2f

    fun renderMeme(
        sourceBitmap: Bitmap,
        texts: List<MemeText>,
        canvasWidth: Int,
        canvasHeight: Int
    ): Bitmap {
        val scaled = Bitmap.createScaledBitmap(sourceBitmap, canvasWidth, canvasHeight, true)
        val result = scaled.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        texts.filter { it.content.isNotBlank() }.forEach { drawText(canvas, it, canvasWidth, canvasHeight) }
        return result
    }

    private fun drawText(canvas: Canvas, memeText: MemeText, canvasWidth: Int, canvasHeight: Int) {
        val x = memeText.xRatio * canvasWidth
        val y = memeText.yRatio * canvasHeight
        val maxWidth = memeText.maxWidthRatio * canvasWidth
        val maxHeight = memeText.maxHeightRatio * canvasHeight

        val measurePaint = Paint().apply {
            isAntiAlias = true
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }

        // Shrink the font until the wrapped text fits inside the zone's box,
        // or we hit the minimum readable size.
        var fontSize = memeText.fontSize
        var lines: List<String>
        while (true) {
            measurePaint.textSize = fontSize
            lines = wrapText(memeText.content, measurePaint, maxWidth)
            val fits = lines.size * measurePaint.fontSpacing() <= maxHeight &&
                lines.all { measurePaint.measureText(it) <= maxWidth }
            if (fits || fontSize <= MIN_FONT_SIZE) break
            fontSize -= FONT_STEP
        }

        val basePaint = Paint(measurePaint).apply { textSize = fontSize }
        val strokePaint = if (memeText.hasStroke) {
            Paint(basePaint).apply {
                color = android.graphics.Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = fontSize * 0.12f
            }
        } else null

        val lineHeight = basePaint.fontSpacing()
        val blockHeight = lineHeight * lines.size
        val fm = basePaint.fontMetrics
        var baselineY = y - blockHeight / 2f - fm.ascent

        lines.forEach { line ->
            strokePaint?.let { canvas.drawText(line, x, baselineY, it) }
            basePaint.apply {
                color = memeText.color
                style = Paint.Style.FILL
            }
            canvas.drawText(line, x, baselineY, basePaint)
            baselineY += lineHeight
        }
    }

    private fun Paint.fontSpacing(): Float = fontMetrics.let { it.descent - it.ascent + it.leading }

    /** Greedily wraps [text] into lines that fit within [maxWidth], respecting manual line breaks. */
    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val lines = mutableListOf<String>()
        text.split("\n").forEach { paragraph ->
            var current = ""
            paragraph.split(" ").forEach { word ->
                val candidate = if (current.isEmpty()) word else "$current $word"
                if (current.isEmpty() || paint.measureText(candidate) <= maxWidth) {
                    current = candidate
                } else {
                    lines.add(current)
                    current = word
                }
            }
            lines.add(current)
        }
        return lines
    }
}
