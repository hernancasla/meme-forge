package com.memeforge.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import com.memeforge.data.model.MemeText

object MemeRenderer {

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

        val basePaint = Paint().apply {
            isAntiAlias = true
            typeface = Typeface.DEFAULT_BOLD
            textSize = memeText.fontSize
            textAlign = Paint.Align.CENTER
        }

        if (memeText.hasStroke) {
            val strokePaint = Paint(basePaint).apply {
                color = android.graphics.Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = memeText.fontSize * 0.12f
            }
            canvas.drawText(memeText.content, x, y, strokePaint)
        }

        basePaint.apply {
            color = memeText.color
            style = Paint.Style.FILL
        }
        canvas.drawText(memeText.content, x, y, basePaint)
    }
}
