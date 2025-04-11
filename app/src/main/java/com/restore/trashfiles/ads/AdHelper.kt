package com.restore.trashfiles.ads

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import com.restore.trashfiles.R

/**
 * Helper class to manage ads in activities
 */
object AdHelper {
    
    /**
     * Shows a banner ad in the provided container
     * @param context The context
     * @param adContainer The ViewGroup container for the ad
     */
    fun showBannerAd(context: Context, adContainer: ViewGroup) {
        AdManager.getInstance().createBannerAd(context, adContainer)
    }
    
    /**
     * Shows an interstitial ad
     * @param activity The activity
     */
    fun showInterstitialAd(activity: Activity) {
        AdManager.getInstance().showInterstitialAd(activity)
    }
    
    /**
     * Shows a rewarded ad
     * @param activity The activity
     * @param onRewarded Callback for when user completes viewing the ad
     */
    fun showRewardedAd(activity: Activity, onRewarded: () -> Unit) {
        AdManager.getInstance().showRewardedAd(activity, onRewarded)
    }
    
    /**
     * Loads a rewarded ad in advance
     * @param context The context
     */
    fun preloadRewardedAd(context: Context) {
        AdManager.getInstance().loadRewardedAd(context)
    }
    
    /**
     * Determines if an interstitial ad should be shown based on frequency settings
     * This is a helper method that can be used for custom ad showing logic
     * @return true if ad should be shown
     */
    fun shouldShowInterstitial(): Boolean {
        // This can be expanded with more sophisticated rules
        return true
    }
} 