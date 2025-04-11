package com.restore.trashfiles.intro

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.restore.trashfiles.R

class WormDotsIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val dots = mutableListOf<View>()
    private var selectedDot: View? = null
    
    private val selectedColor = ContextCompat.getColor(context, R.color.design_default_color_primary)
    private val unselectedColor = ContextCompat.getColor(context, android.R.color.darker_gray)
    
    private val dotSize = resources.getDimensionPixelSize(R.dimen.indicator_width)
    private val dotSpacing = resources.getDimensionPixelSize(R.dimen.indicator_margin)
    private val animDuration = 300L
    
    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
    }
    
    fun setUpDots(count: Int, initialPosition: Int = 0) {
        removeAllViews()
        dots.clear()
        
        // Tạo dots
        for (i in 0 until count) {
            val dot = View(context)
            val size = dotSize
            val params = LayoutParams(size, size)
            params.marginStart = if (i == 0) 0 else dotSpacing
            params.marginEnd = if (i == count - 1) 0 else dotSpacing
            
            dot.layoutParams = params
            
            // Màu và hình dạng
            val background = GradientDrawable()
            background.shape = GradientDrawable.OVAL
            background.setColor(if (i == initialPosition) selectedColor else unselectedColor)
            dot.background = background
            
            addView(dot)
            dots.add(dot)
            
            if (i == initialPosition) {
                selectedDot = dot
                // Làm cho dot được chọn lớn hơn một chút
                selectedDot?.scaleX = 1.5f
                selectedDot?.scaleY = 1.5f
            }
        }
    }
    
    fun animatePageChange(newPosition: Int) {
        val newDot = dots.getOrNull(newPosition) ?: return
        val previousDot = selectedDot ?: return
        
        if (newDot == previousDot) return
        
        // 1. Thu nhỏ dot trước đó
        val shrinkAnim1 = ValueAnimator.ofFloat(previousDot.scaleX, 1.0f)
        shrinkAnim1.duration = animDuration / 2
        shrinkAnim1.interpolator = AccelerateDecelerateInterpolator()
        shrinkAnim1.addUpdateListener { animator ->
            val value = animator.animatedValue as Float
            previousDot.scaleX = value
            previousDot.scaleY = value
        }
        
        // 2. Đổi màu dot trước đó
        val colorAnim1 = ValueAnimator.ofArgb(selectedColor, unselectedColor)
        colorAnim1.duration = animDuration / 2
        colorAnim1.addUpdateListener { animator ->
            val color = animator.animatedValue as Int
            (previousDot.background as GradientDrawable).setColor(color)
        }
        
        // 3. Phóng to dot mới
        val growAnim = ValueAnimator.ofFloat(1.0f, 1.5f)
        growAnim.duration = animDuration / 2
        growAnim.interpolator = AccelerateDecelerateInterpolator()
        growAnim.addUpdateListener { animator ->
            val value = animator.animatedValue as Float
            newDot.scaleX = value
            newDot.scaleY = value
        }
        
        // 4. Đổi màu dot mới
        val colorAnim2 = ValueAnimator.ofArgb(unselectedColor, selectedColor)
        colorAnim2.duration = animDuration / 2
        colorAnim2.addUpdateListener { animator ->
            val color = animator.animatedValue as Int
            (newDot.background as GradientDrawable).setColor(color)
        }
        
        // Chạy các animation theo thứ tự
        val animSet1 = AnimatorSet()
        animSet1.playTogether(shrinkAnim1, colorAnim1)
        
        val animSet2 = AnimatorSet()
        animSet2.playTogether(growAnim, colorAnim2)
        
        val fullAnim = AnimatorSet()
        fullAnim.playSequentially(animSet1, animSet2)
        fullAnim.start()
        
        selectedDot = newDot
    }
} 