package com.memeforge.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MemeTemplate(
    val id: String,
    val name: String,
    val imageUrl: String,
    val category: String,
    val textZones: List<TextZone>
)

@Serializable
data class TextZone(
    val id: String,
    val label: String,
    val defaultX: Float,
    val defaultY: Float,
    val defaultText: String,
    /** Width of the box the text must wrap/shrink to fit, as a fraction of the image width. */
    val maxWidthRatio: Float? = null,
    /** Height of the box the text must wrap/shrink to fit, as a fraction of the image height. */
    val maxHeightRatio: Float? = null
)
