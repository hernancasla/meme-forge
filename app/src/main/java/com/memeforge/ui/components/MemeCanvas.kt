package com.memeforge.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale

@Composable
fun MemeCanvas(
    bitmap: Bitmap,
    modifier: Modifier = Modifier
) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        modifier = modifier.fillMaxWidth(),
        contentScale = ContentScale.Fit
    )
}
