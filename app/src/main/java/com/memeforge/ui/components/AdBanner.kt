package com.memeforge.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.memeforge.util.AdConfig

@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                // TODO: Reemplazar con ID real de AdMob
                adUnitId = AdConfig.BANNER_ID
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
