package com.htnguyen.ifu.intro

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max

class IntroPageTransformer : ViewPager2.PageTransformer {
    
    private val MIN_SCALE = 0.9f
    private val MIN_ALPHA = 0.7f
    
    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width
        val pageHeight = page.height
        
        when {
            // Trang không nhìn thấy
            position < -1 -> {
                // Ẩn trang
                page.alpha = 0f
            }
            
            // Trang đang di chuyển ra khỏi màn hình bên trái
            position <= 0 -> {
                // Sử dụng hiệu ứng mặc định cho trang di chuyển ra
                page.alpha = 1 + position * 0.5f
                
                // Hiệu ứng chống xếp chồng
                page.translationX = pageWidth * -position
                
                // Thu nhỏ trang (giữa MIN_SCALE và 1)
                val scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(position))
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor
                
                // Loại bỏ hiệu ứng xoay
                page.rotation = 0f
            }
            
            // Trang đang đi vào màn hình từ bên phải
            position <= 1 -> {
                // Làm nổi bật trang đang đi vào
                page.alpha = 1f
                
                // Loại bỏ hiệu ứng xoay
                page.rotation = 0f
                
                // Phóng to từ thu nhỏ
                val scaleFactor = max(MIN_SCALE, 1 - abs(position * 0.2f))
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor
                
                // Thêm hiệu ứng parallax cải tiến - di chuyển nhẹ nhàng hơn
                val imageView = page.findViewById<View>(com.htnguyen.ifu.R.id.introImage)
                imageView?.let {
                    // Giảm mức độ di chuyển để ảnh không bị nghiêng nhiều
                    it.translationX = -position * (pageWidth / 8f)
                    // Đảm bảo ảnh không bị nghiêng
                    it.rotation = 0f
                }
                
                // Di chuyển trang vào từ bên phải
                page.translationX = pageWidth * -position
            }
            
            // Trang không nhìn thấy
            else -> {
                page.alpha = 0f
            }
        }
    }
} 