package com.memeforge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.android.gms.ads.MobileAds
import com.memeforge.ui.navigation.AppNavGraph
import com.memeforge.ui.theme.MemeForgeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        setContent {
            MemeForgeTheme {
                AppNavGraph()
            }
        }
    }
}
