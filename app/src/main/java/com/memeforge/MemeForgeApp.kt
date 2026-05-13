package com.memeforge

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.DebugLogger
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient

@HiltAndroidApp
class MemeForgeApp : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .logger(DebugLogger())
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val original = chain.request()
                        val request = original.newBuilder().apply {
                            // Imgflip bloquea hotlinking sin Referer
                            if (original.url.host.contains("imgflip.com")) {
                                header("Referer", "https://imgflip.com/")
                                header(
                                    "User-Agent",
                                    "Mozilla/5.0 (Linux; Android 14) " +
                                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                        "Chrome/120.0.0.0 Mobile Safari/537.36"
                                )
                            }
                        }.build()
                        chain.proceed(request)
                    }
                    .build()
            }
            .build()
}
