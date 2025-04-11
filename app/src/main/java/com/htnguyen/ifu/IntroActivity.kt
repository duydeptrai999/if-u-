package com.htnguyen.ifu

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.viewpager2.widget.ViewPager2
import com.htnguyen.ifu.intro.IntroPageTransformer
import com.htnguyen.ifu.intro.IntroSlideAdapter
import com.htnguyen.ifu.intro.WormDotsIndicator
import androidx.core.content.ContextCompat

class IntroActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var skipButton: TextView
    private lateinit var nextButton: TextView
    private lateinit var skipButtonContainer: CardView
    private lateinit var nextButtonContainer: CardView
    private lateinit var wormDotsIndicator: WormDotsIndicator
    private lateinit var sharedPreferences: SharedPreferences
    
    private val introSlideAdapter by lazy { IntroSlideAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Kiểm tra xem người dùng đã xem intro chưa
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        if (isIntroDone()) {
            startMainActivity()
            return
        }
        
        setContentView(R.layout.activity_intro)
        
        initViews()
        setupViewPager()
        setupButtons()
        setupPageIndicators()
        
        // Thêm hiệu ứng cho các nút
        animateIntroButtons()
    }
    
    private fun initViews() {
        viewPager = findViewById(R.id.introViewPager)
        skipButton = findViewById(R.id.skipButton)
        nextButton = findViewById(R.id.nextButton)
        skipButtonContainer = findViewById(R.id.skipButtonContainer)
        nextButtonContainer = findViewById(R.id.nextButtonContainer)
        wormDotsIndicator = findViewById(R.id.wormDotsIndicator)
    }
    
    private fun setupViewPager() {
        // Áp dụng PageTransformer
        viewPager.setPageTransformer(IntroPageTransformer())
        
        // Thiết lập adapter
        viewPager.adapter = introSlideAdapter
        
        // Thiết lập offscreen page limit để giữ các trang gần đó trong bộ nhớ
        viewPager.offscreenPageLimit = 1
        
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                
                // Cập nhật indicator
                wormDotsIndicator.animatePageChange(position)
                
                // Cập nhật nút Next/Start
                if (position == introSlideAdapter.itemCount - 1) {
                    animateNextButtonToStart()
                } else if (nextButton.text.toString() == getString(R.string.start)) {
                    animateStartButtonToNext()
                }
                
                // Ẩn/hiện nút Skip
                updateSkipButtonVisibility(position)
            }
        })
    }
    
    private fun setupButtons() {
        // Thêm hiệu ứng ripple khi nhấn
        skipButtonContainer.setOnClickListener {
            animateButtonClick(skipButtonContainer)
            skipIntro()
        }
        
        nextButtonContainer.setOnClickListener {
            animateButtonClick(nextButtonContainer)
            nextSlide()
        }
    }
    
    private fun setupPageIndicators() {
        // Thiết lập indicator với số lượng trang bằng số lượng slide
        wormDotsIndicator.setUpDots(introSlideAdapter.itemCount)
    }
    
    private fun nextSlide() {
        val currentPosition = viewPager.currentItem
        if (currentPosition < introSlideAdapter.itemCount - 1) {
            // Nếu chưa phải trang cuối cùng, chuyển đến trang tiếp theo
            viewPager.currentItem = currentPosition + 1
        } else {
            // Nếu là trang cuối cùng, chuyển đến MainActivity
            skipIntro()
        }
    }
    
    private fun skipIntro() {
        saveIntroDone()
        startMainActivity()
    }
    
    private fun animateButtonClick(button: View) {
        button.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .setInterpolator(OvershootInterpolator())
                    .start()
            }
            .start()
    }
    
    private fun animateNextButtonToStart() {
        // Fade out và thay đổi nội dung
        nextButton.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction {
                nextButton.text = getString(R.string.start)
                nextButtonContainer.setCardBackgroundColor(getColor(android.R.color.holo_green_dark))
                
                // Fade in với hiệu ứng scale up
                val scaleX = ObjectAnimator.ofFloat(nextButtonContainer, "scaleX", 0.8f, 1.1f, 1.0f)
                val scaleY = ObjectAnimator.ofFloat(nextButtonContainer, "scaleY", 0.8f, 1.1f, 1.0f)
                val alpha = ObjectAnimator.ofFloat(nextButton, "alpha", 0f, 1f)
                
                val animSet = AnimatorSet()
                animSet.playTogether(scaleX, scaleY, alpha)
                animSet.duration = 300
                animSet.interpolator = OvershootInterpolator()
                animSet.start()
            }
            .start()
    }
    
    private fun animateStartButtonToNext() {
        // Fade out và thay đổi nội dung
        nextButton.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction {
                nextButton.text = getString(R.string.next)
                nextButtonContainer.setCardBackgroundColor(ContextCompat.getColor(this, R.color.design_default_color_primary))
                
                // Fade in
                nextButton.animate()
                    .alpha(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }
    
    private fun updateSkipButtonVisibility(position: Int) {
        if (position == introSlideAdapter.itemCount - 1) {
            skipButtonContainer.animate()
                .alpha(0f)
                .translationX(-100f)
                .setDuration(200)
                .withEndAction {
                    skipButtonContainer.visibility = View.GONE
                }
                .start()
        } else if (skipButtonContainer.visibility == View.GONE) {
            skipButtonContainer.visibility = View.VISIBLE
            skipButtonContainer.translationX = -100f
            skipButtonContainer.alpha = 0f
            
            skipButtonContainer.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(200)
                .start()
        }
    }
    
    private fun animateIntroButtons() {
        // Ẩn các nút ban đầu
        skipButtonContainer.alpha = 0f
        skipButtonContainer.translationY = 100f
        
        nextButtonContainer.alpha = 0f
        nextButtonContainer.translationY = 100f
        
        // Animation cho nút Skip
        skipButtonContainer.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setInterpolator(OvershootInterpolator(1.2f))
            .start()
        
        // Animation cho nút Next (delay một chút)
        nextButtonContainer.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(100)
            .setDuration(500)
            .setInterpolator(OvershootInterpolator(1.2f))
            .start()
    }
    
    private fun saveIntroDone() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("intro_done", true)
        editor.apply()
    }
    
    private fun isIntroDone(): Boolean {
        return sharedPreferences.getBoolean("intro_done", false)
    }
    
    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        
        // Thêm hiệu ứng transition đẹp
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
} 