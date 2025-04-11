package com.restore.trashfiles.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.restore.trashfiles.R

/**
 * Helper class to manage ads in activities
 */
object AdHelper {
    private const val TAG = "AdHelper"
    
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
     * Loads a native ad
     * @param context The context
     * @param adContainer The ViewGroup container for the ad
     * @param layoutResId Optional custom layout resource ID
     */
    fun loadNativeAd(context: Context, adContainer: ViewGroup, layoutResId: Int = AdManager.LAYOUT_DEFAULT) {
        AdManager.getInstance().loadNativeAd(context, adContainer, layoutResId)
    }
    
    /**
     * Loads a native ad with intro layout
     * @param context The context
     * @param adContainer The ViewGroup container for the ad
     */
    fun loadIntroNativeAd(context: Context, adContainer: ViewGroup) {
        AdManager.getInstance().loadNativeAd(context, adContainer, AdManager.LAYOUT_INTRO)
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
    
    /**
     * Toggle expandable text in native ad
     */
    fun setupExpandableAdText(adView: View) {
        val bodyTextView = adView.findViewById<TextView>(R.id.ad_body)
        val hintTextView = adView.findViewById<TextView>(R.id.ad_body_hint)
        
        bodyTextView?.let { textView ->
            // Ban đầu hiển thị tối đa 3 dòng
            textView.maxLines = 3
            
            // Tính toán số dòng thực tế sau khi layout hoàn tất
            textView.post {
                val lineCount = textView.lineCount
                // Nếu text ngắn (dưới 3 dòng), ẩn hint đi
                if (lineCount <= 3) {
                    hintTextView?.visibility = View.GONE
                }
            }
            
            // Thêm sự kiện click để mở rộng/thu gọn text
            textView.setOnClickListener {
                if (textView.maxLines == 3) {
                    // Mở rộng để hiển thị toàn bộ text
                    textView.maxLines = Integer.MAX_VALUE
                    hintTextView?.text = "Nhấn để thu gọn"
                } else {
                    // Thu gọn về 3 dòng
                    textView.maxLines = 3
                    hintTextView?.text = "Nhấn để xem thêm"
                }
            }
            
            // Hint cũng có thể được click
            hintTextView?.setOnClickListener {
                // Kích hoạt cùng hành động với bodyTextView
                textView.performClick()
            }
        }
    }
} 