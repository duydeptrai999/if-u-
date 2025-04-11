package com.restore.trashfiles.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.restore.trashfiles.R

class AdManager private constructor() {
    private val TAG = "AdManager"
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var nativeAd: NativeAd? = null
    private var adCounter = 0
    private val AD_FREQUENCY = 3 // Show interstitial ad every 3 actions

    companion object {
        const val LAYOUT_DEFAULT = R.layout.ad_unified
        const val LAYOUT_INTRO = R.layout.ad_unified_intro
        
        @Volatile
        private var instance: AdManager? = null

        fun getInstance(): AdManager {
            return instance ?: synchronized(this) {
                instance ?: AdManager().also { instance = it }
            }
        }
    }

    fun initialize(context: Context) {
        MobileAds.initialize(context) { status ->
            Log.d(TAG, "Initialization complete. Status: $status")
        }
        loadInterstitialAd(context)
    }

    // Banner Ads
    fun createBannerAd(context: Context, adContainer: ViewGroup) {
        val adView = AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = context.getString(R.string.ad_banner_id)
        }
        
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "Banner ad loaded successfully")
            }
            
            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.e(TAG, "Banner ad failed to load: ${error.message}")
            }
        }
        
        adContainer.removeAllViews()
        adContainer.addView(adView)
    }

    // Native Ads
    fun loadNativeAd(context: Context, adContainer: ViewGroup, layoutResId: Int = LAYOUT_DEFAULT) {
        val adLoader = AdLoader.Builder(context, context.getString(R.string.ad_native_id))
            .forNativeAd { ad ->
                // Nếu activity đã bị destroy thì cần giải phóng nativeAd
                if ((context as? Activity)?.isDestroyed == true) {
                    ad.destroy()
                    return@forNativeAd
                }
                
                // Giải phóng native ad cũ nếu có
                nativeAd?.destroy()
                nativeAd = ad
                
                // Tạo native ad view và đặt vào container
                val inflater = LayoutInflater.from(context)
                val adView = inflater.inflate(layoutResId, adContainer, false) as NativeAdView
                populateNativeAdView(ad, adView)
                
                adContainer.removeAllViews()
                adContainer.addView(adView)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "Native ad failed to load: ${error.message}")
                }
                
                override fun onAdLoaded() {
                    Log.d(TAG, "Native ad loaded successfully")
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                .build()
            )
            .build()
            
        adLoader.loadAd(AdRequest.Builder().build())
    }
    
    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Đặt các thành phần của quảng cáo vào view
        
        // Headline
        adView.findViewById<TextView>(R.id.ad_headline)?.let {
            it.text = nativeAd.headline
            adView.headlineView = it
        }
        
        // Body
        adView.findViewById<TextView>(R.id.ad_body)?.let {
            it.text = nativeAd.body
            adView.bodyView = it
        }
        
        // Call to action
        adView.findViewById<Button>(R.id.ad_call_to_action)?.let {
            it.text = nativeAd.callToAction
            adView.callToActionView = it
        }
        
        // Icon
        adView.findViewById<ImageView>(R.id.ad_app_icon)?.let {
            if (nativeAd.icon != null) {
                it.visibility = View.VISIBLE
                it.setImageDrawable(nativeAd.icon?.drawable)
            } else {
                it.visibility = View.GONE
            }
            adView.iconView = it
        }
        
        // Media (video, images)
        adView.findViewById<MediaView>(R.id.ad_media)?.let {
            adView.mediaView = it
        }
        
        // Price
        adView.findViewById<TextView>(R.id.ad_price)?.let {
            if (nativeAd.price != null) {
                it.visibility = View.VISIBLE
                it.text = nativeAd.price
            } else {
                it.visibility = View.INVISIBLE
            }
            adView.priceView = it
        }
        
        // Store
        adView.findViewById<TextView>(R.id.ad_store)?.let {
            if (nativeAd.store != null) {
                it.visibility = View.VISIBLE
                it.text = nativeAd.store
            } else {
                it.visibility = View.INVISIBLE
            }
            adView.storeView = it
        }
        
        // Rating
        adView.findViewById<RatingBar>(R.id.ad_stars)?.let {
            if (nativeAd.starRating != null) {
                it.visibility = View.VISIBLE
                it.rating = nativeAd.starRating!!.toFloat()
            } else {
                it.visibility = View.INVISIBLE
            }
            adView.starRatingView = it
        }
        
        // Advertiser
        adView.findViewById<TextView>(R.id.ad_advertiser)?.let {
            if (nativeAd.advertiser != null) {
                it.visibility = View.VISIBLE
                it.text = nativeAd.advertiser
            } else {
                it.visibility = View.INVISIBLE
            }
            adView.advertiserView = it
        }

        // Đặt native ad vào view
        adView.setNativeAd(nativeAd)
    }

    // Interstitial Ads
    private fun loadInterstitialAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        val adUnitId = context.getString(R.string.ad_interstitial_id)
        
        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                Log.d(TAG, "Interstitial ad loaded successfully")
                interstitialAd = ad
                
                // Set full screen content callback
                interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Interstitial ad dismissed")
                        // Load the next interstitial ad
                        loadInterstitialAd(context)
                    }
                    
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.e(TAG, "Interstitial ad failed to show: ${adError.message}")
                    }
                }
            }
            
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.e(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                interstitialAd = null
            }
        })
    }
    
    fun showInterstitialAd(activity: Activity) {
        adCounter++
        
        if (adCounter % AD_FREQUENCY != 0) {
            return // Only show ad at specified frequency
        }
        
        if (interstitialAd != null) {
            interstitialAd?.show(activity)
        } else {
            Log.d(TAG, "Interstitial ad not ready yet, loading...")
            loadInterstitialAd(activity)
        }
    }

    // Rewarded Ads
    fun loadRewardedAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        val adUnitId = context.getString(R.string.ad_rewarded_id)
        
        RewardedAd.load(context, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                Log.d(TAG, "Rewarded ad loaded successfully")
                rewardedAd = ad
            }
            
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.e(TAG, "Rewarded ad failed to load: ${loadAdError.message}")
                rewardedAd = null
            }
        })
    }
    
    fun showRewardedAd(activity: Activity, onAdRewarded: () -> Unit) {
        if (rewardedAd != null) {
            rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Rewarded ad dismissed")
                    rewardedAd = null
                    loadRewardedAd(activity)
                }
                
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "Rewarded ad failed to show: ${adError.message}")
                    rewardedAd = null
                }
            }
            
            rewardedAd?.show(activity) { rewardItem ->
                val rewardAmount = rewardItem.amount
                val rewardType = rewardItem.type
                Log.d(TAG, "User earned reward: $rewardAmount $rewardType")
                onAdRewarded()
            }
        } else {
            Log.d(TAG, "Rewarded ad not ready yet, loading...")
            Toast.makeText(activity, activity.getString(R.string.ad_failed_to_load), Toast.LENGTH_SHORT).show()
            loadRewardedAd(activity)
            // Still reward the user even if ad isn't ready
            onAdRewarded()
        }
    }
} 