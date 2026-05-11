package com.memeforge.util;

import android.app.Activity;
import android.content.Context;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.memeforge.BuildConfig;
import javax.inject.Inject;
import javax.inject.Singleton;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/memeforge/util/AdConfig;", "", "()V", "BANNER_ID", "", "INTERSTITIAL_ID", "REWARDED_ID", "app_debug"})
public final class AdConfig {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String BANNER_ID = "ca-app-pub-3940256099942544/6300978111";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String REWARDED_ID = "ca-app-pub-3940256099942544/5224354917";
    @org.jetbrains.annotations.NotNull()
    public static final com.memeforge.util.AdConfig INSTANCE = null;
    
    private AdConfig() {
        super();
    }
}