package com.htnguyen.ifu.intro

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.htnguyen.ifu.R
import android.graphics.Color

class IntroSlideAdapter(private val context: Context) : 
    RecyclerView.Adapter<IntroSlideAdapter.SlideViewHolder>() {
    
    // Màu gradient mới phù hợp với màu chính của ứng dụng
    private val backgroundColors = arrayOf(
        intArrayOf(
            Color.parseColor("#1E5799"),    // Xanh đậm của header
            Color.parseColor("#4285F4")     // Xanh Google (màu icon khôi phục ảnh)
        ),
        intArrayOf(
            Color.parseColor("#4285F4"),    // Xanh Google (màu icon khôi phục ảnh)
            Color.parseColor("#2EB62C")     // Xanh lá của header
        ),
        intArrayOf(
            Color.parseColor("#DB4437"),    // Đỏ Google (màu icon khôi phục video)
            Color.parseColor("#F4B400")     // Vàng Google (màu icon khôi phục file)
        ),
        intArrayOf(
            Color.parseColor("#F4B400"),    // Vàng Google (màu icon khôi phục file)
            Color.parseColor("#1976D2")     // Xanh dương nhạt (màu chính của ứng dụng)
        )
    )
    
    private val slides = listOf(
        IntroSlide(
            imageResId = R.drawable.intro_welcome,
            titleResId = R.string.intro_title_1,
            descriptionResId = R.string.intro_desc_1
        ),
        IntroSlide(
            imageResId = R.drawable.intro_photo,
            titleResId = R.string.intro_title_2,
            descriptionResId = R.string.intro_desc_2
        ),
        IntroSlide(
            imageResId = R.drawable.intro_video,
            titleResId = R.string.intro_title_3,
            descriptionResId = R.string.intro_desc_3
        ),
        IntroSlide(
            imageResId = R.drawable.intro_file,
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
        holder.imageView.setImageResource(slide.imageResId)
        holder.titleView.setText(slide.titleResId)
        holder.descriptionView.setText(slide.descriptionResId)
        
        // Thiết lập màu gradient cho slide
        val gradientColors = backgroundColors[position % backgroundColors.size]
        holder.itemView.background = android.graphics.drawable.GradientDrawable(
            // Sử dụng góc gradient khác nhau để tạo hiệu ứng đa dạng
            when (position) {
                0 -> android.graphics.drawable.GradientDrawable.Orientation.TR_BL // Slide 1: Trên phải -> Dưới trái
                1 -> android.graphics.drawable.GradientDrawable.Orientation.TL_BR // Slide 2: Trên trái -> Dưới phải
                2 -> android.graphics.drawable.GradientDrawable.Orientation.BL_TR // Slide 3: Dưới trái -> Trên phải
                else -> android.graphics.drawable.GradientDrawable.Orientation.BR_TL // Slide 4: Dưới phải -> Trên trái
            },
            gradientColors
        )
        
        // Thêm animation xuất hiện tuần tự
        animateSlideItems(holder)
    }
    
    private fun animateSlideItems(holder: SlideViewHolder) {
        // Animation cho hình ảnh - xuất hiện đầu tiên với hiệu ứng phóng to từ nhỏ
        holder.imageView.alpha = 0f
        holder.imageView.scaleX = 0.7f
        holder.imageView.scaleY = 0.7f
        holder.imageView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(800)
            .setInterpolator(OvershootInterpolator(1.0f))  // Giảm overshoot xuống 1.0 để ít bị bounce hơn
            .withEndAction {
                // Animation cho tiêu đề - xuất hiện thứ hai
                holder.titleView.translationY = -50f  // Thay đổi từ translationX sang translationY
                holder.titleView.alpha = 0f
                holder.titleView.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(500)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .withEndAction {
                        // Animation cho mô tả - xuất hiện cuối cùng
                        holder.descriptionView.translationY = 50f  // Thay đổi từ translationX sang translationY
                        holder.descriptionView.alpha = 0f
                        holder.descriptionView.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(500)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .start()
                    }
                    .start()
            }
            .start()
    }
    
    override fun getItemCount(): Int = slides.size
    
    inner class SlideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.introImage)
        val titleView: TextView = itemView.findViewById(R.id.introTitle)
        val descriptionView: TextView = itemView.findViewById(R.id.introDescription)
    }
    
    data class IntroSlide(
        val imageResId: Int,
        val titleResId: Int,
        val descriptionResId: Int
    )
} 