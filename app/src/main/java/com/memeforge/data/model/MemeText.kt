package com.memeforge.data.model

data class MemeText(
    val zoneId: String,
    val content: String,
    val xRatio: Float,
    val yRatio: Float,
    val fontSize: Float = 48f,
    val color: Int = android.graphics.Color.WHITE,
    val hasStroke: Boolean = true
)
