package com.memeforge.util

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.memeforge.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

// TODO: Reemplazar con IDs reales de AdMob antes de publicar en Play Store
object AdConfig {
    const val BANNER_ID = BuildConfig.ADMOB_BANNER_ID
    const val INTERSTITIAL_ID = BuildConfig.ADMOB_INTERSTITIAL_ID
    const val REWARDED_ID = BuildConfig.ADMOB_REWARDED_ID
}

@Singleton
class AdManager @Inject constructor() {

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    fun loadInterstitial(context: Context) {
        // TODO: Reemplazar INTERSTITIAL_ID con ID real de AdMob
        InterstitialAd.load(
            context,
            AdConfig.INTERSTITIAL_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) { interstitialAd = ad }
                override fun onAdFailedToLoad(error: LoadAdError) { interstitialAd = null }
            }
        )
    }

    fun showInterstitial(activity: Activity, onDismissed: () -> Unit) {
        val ad = interstitialAd
        if (ad == null) {
            onDismissed()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                loadInterstitial(activity) // Pre-fetch siguiente anuncio
                onDismissed()
            }
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
                onDismissed()
            }
        }
        ad.show(activity)
    }

    fun loadRewarded(context: Context) {
        // TODO: Reemplazar REWARDED_ID con ID real de AdMob
        RewardedAd.load(
            context,
            AdConfig.REWARDED_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) { rewardedAd = ad }
                override fun onAdFailedToLoad(error: LoadAdError) { rewardedAd = null }
            }
        )
    }

    fun showRewarded(activity: Activity, onRewarded: () -> Unit, onDismissed: () -> Unit) {
        val ad = rewardedAd
        if (ad == null) {
            onDismissed()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                loadRewarded(activity) // Pre-fetch siguiente anuncio
                onDismissed()
            }
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                rewardedAd = null
                onDismissed()
            }
        }
        ad.show(activity) { onRewarded() }
    }
}
