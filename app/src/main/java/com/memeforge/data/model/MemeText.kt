package com.memeforge.data.model

data class MemeText(
    val zoneId: String,
    val content: String,
    val xRatio: Float,
    val yRatio: Float,
    val fontSize: Float = 48f,
    val color: Int = android.graphics.Color.WHITE,
    val hasStroke: Boolean = true,
    /** Width of the box the text must wrap/shrink to fit, as a fraction of the image width. */
    val maxWidthRatio: Float = 1f,
    /** Height of the box the text must wrap/shrink to fit, as a fraction of the image height. */
    val maxHeightRatio: Float = 1f
)
