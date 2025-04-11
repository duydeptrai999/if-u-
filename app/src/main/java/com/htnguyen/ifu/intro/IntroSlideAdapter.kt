package com.htnguyen.ifu.intro

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.htnguyen.ifu.R

class IntroSlideAdapter(private val context: Context) : 
    RecyclerView.Adapter<IntroSlideAdapter.SlideViewHolder>() {
    
    // Thêm mảng màu gradient cho từng slide
    private val backgroundColors = arrayOf(
        intArrayOf(
            ContextCompat.getColor(context, android.R.color.holo_blue_light),
            ContextCompat.getColor(context, android.R.color.holo_blue_dark)
        ),
        intArrayOf(
            ContextCompat.getColor(context, android.R.color.holo_green_light),
            ContextCompat.getColor(context, android.R.color.holo_green_dark)
        ),
        intArrayOf(
            ContextCompat.getColor(context, android.R.color.holo_orange_light),
            ContextCompat.getColor(context, android.R.color.holo_orange_dark)
        ),
        intArrayOf(
            ContextCompat.getColor(context, android.R.color.holo_purple),
            ContextCompat.getColor(context, R.color.design_default_color_primary)
        )
    )
    
    // Đường dẫn tới các file animation Lottie
    private val lottieAnimations = arrayOf(
        "intro_welcome.json",
        "intro_photo.json",
        "intro_video.json",
        "intro_file.json"
    )
    
    // Fallback drawables nếu lottie không hoạt động
    private val fallbackDrawables = arrayOf(
        R.drawable.intro_welcome,
        R.drawable.intro_photo,
        R.drawable.intro_video,
        R.drawable.intro_file
    )
    
    private val slides = listOf(
        IntroSlide(
            animationResId = "intro_welcome",
            titleResId = R.string.intro_title_1,
            descriptionResId = R.string.intro_desc_1
        ),
        IntroSlide(
            animationResId = "intro_photo",
            titleResId = R.string.intro_title_2,
            descriptionResId = R.string.intro_desc_2
        ),
        IntroSlide(
            animationResId = "intro_video",
            titleResId = R.string.intro_title_3,
            descriptionResId = R.string.intro_desc_3
        ),
        IntroSlide(
            animationResId = "intro_file",
            titleResId = R.string.intro_title_4,
            descriptionResId = R.string.intro_desc_4
        )
    )
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.intro_slide, parent, false)
        return SlideViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
        val slide = slides[position]
        
        // Reset trạng thái hiển thị ban đầu
        holder.imageView.alpha = 0f
        holder.titleView.alpha = 0f
        holder.descriptionView.alpha = 0f
        
        // Thiết lập nội dung
        try {
            // Thử tải animation Lottie từ raw resource
            val animationName = slide.animationResId
            val resId = context.resources.getIdentifier(animationName, "raw", context.packageName)
            
            if (resId != 0) {
                holder.imageView.setAnimation(resId)
            } else {
                // Fallback to drawable if animation not found
                holder.imageView.setImageResource(fallbackDrawables[position])
            }
        } catch (e: Exception) {
            // Fallback to drawable if animation fails
            holder.imageView.setImageResource(fallbackDrawables[position])
        }
        
        holder.titleView.setText(slide.titleResId)
        holder.descriptionView.setText(slide.descriptionResId)
        
        // Thiết lập màu gradient cho slide với hiệu ứng animating gradient
        setupAnimatingGradient(holder.itemView, position)
        
        // Thêm animation xuất hiện tuần tự
        animateSlideItems(holder)
        
        // Thêm tương tác cử chỉ cho animation
        setupAnimationInteraction(holder.imageView)
    }
    
    private fun setupAnimatingGradient(view: View, position: Int) {
        val gradientColors = backgroundColors[position % backgroundColors.size]
        
        // Tạo GradientDrawable ban đầu
        val gradientDrawable = android.graphics.drawable.GradientDrawable(
            android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,
            gradientColors
        )
        view.background = gradientDrawable
        
        // Animate gradient rotation
        val valueAnimator = ValueAnimator.ofInt(0, 360)
        valueAnimator.addUpdateListener { animator ->
            val value = animator.animatedValue as Int
            
            // Rotate gradient orientation based on animation value
            val orientation = when {
                value < 90 -> android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM
                value < 180 -> android.graphics.drawable.GradientDrawable.Orientation.TR_BL
                value < 270 -> android.graphics.drawable.GradientDrawable.Orientation.RIGHT_LEFT
                else -> android.graphics.drawable.GradientDrawable.Orientation.BR_TL
            }
            
            val newDrawable = android.graphics.drawable.GradientDrawable(
                orientation,
                gradientColors
            )
            view.background = newDrawable
        }
        
        valueAnimator.duration = 10000 // 10 seconds for one full rotation
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.repeatMode = ValueAnimator.RESTART
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.start()
    }
    
    private fun animateSlideItems(holder: SlideViewHolder) {
        // Animation cho hình ảnh - xuất hiện đầu tiên với hiệu ứng bounce
        holder.imageView.translationY = 150f
        holder.imageView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setInterpolator(OvershootInterpolator(1.2f))
            .withEndAction {
                // Animation cho tiêu đề - xuất hiện thứ hai
                holder.titleView.translationX = -150f
                holder.titleView.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(500)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .withEndAction {
                        // Animation cho mô tả - xuất hiện cuối cùng
                        holder.descriptionView.translationX = 150f
                        holder.descriptionView.animate()
                            .alpha(1f)
                            .translationX(0f)
                            .setDuration(500)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .start()
                    }
                    .start()
            }
            .start()
    }
    
    private fun setupAnimationInteraction(imageView: LottieAnimationView) {
        // Thêm tương tác với animation - nhấn để tăng tốc độ
        imageView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Tăng tốc độ animation khi nhấn vào
                    imageView.speed = 2.5f
                    
                    // Hiệu ứng phóng to
                    imageView.animate()
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(200)
                        .start()
                    
                    // Tạo feedback xúc giác
                    performHapticFeedback(v)
                    
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Trở về tốc độ bình thường
                    imageView.speed = 0.8f
                    
                    // Trở về kích thước bình thường
                    imageView.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(200)
                        .start()
                    
                    true
                }
                else -> false
            }
        }
    }
    
    private fun performHapticFeedback(view: View) {
        // Tạo feedback xúc giác khi tương tác
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            view.performHapticFeedback(HapticFeedbackConstants.TEXT_HANDLE_MOVE)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }
    
    override fun getItemCount(): Int = slides.size
    
    inner class SlideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: LottieAnimationView = itemView.findViewById(R.id.introImage)
        val titleView: TextView = itemView.findViewById(R.id.introTitle)
        val descriptionView: TextView = itemView.findViewById(R.id.introDescription)
    }
    
    data class IntroSlide(
        val animationResId: String,
        val titleResId: Int,
        val descriptionResId: Int
    )
} 