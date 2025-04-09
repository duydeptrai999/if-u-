package com.htnguyen.ifu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.cardview.widget.CardView
import android.widget.ImageView
import android.widget.TextView
import com.htnguyen.ifu.recovery.FileRecoveryActivity
import com.htnguyen.ifu.recovery.PhotoRecoveryActivity
import com.htnguyen.ifu.recovery.VideoRecoveryActivity
import android.view.WindowManager
import android.os.Build
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransparentStatusBar()
        setContentView(R.layout.activity_main)

        setupUI()
    }

    private fun setupTransparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Xóa FLAG_LAYOUT_NO_LIMITS để tránh view bị kéo lên phía trên
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            
            // Thiết lập status bar icon màu tối (do status bar có màu nền nhạt)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            
            // Đã thiết lập trong themes.xml:
            // - statusBarColor: transparent
            // - windowDrawsSystemBarBackgrounds: true
            // - windowTranslucentStatus: false
        }
    }

    private fun setupUI() {
        // Card views for recovery options
        val photoRecoveryCard = findViewById<CardView>(R.id.photoRecoveryCard)
        val videoRecoveryCard = findViewById<CardView>(R.id.videoRecoveryCard)
        val fileRecoveryCard = findViewById<CardView>(R.id.fileRecoveryCard)
        
        // Settings button
        val settingsButton = findViewById<ImageView>(R.id.ivSettings)

        // Set click listeners for each recovery option
        photoRecoveryCard.setOnClickListener {
            // Mở màn hình khôi phục ảnh
            val intent = Intent(this, PhotoRecoveryActivity::class.java)
            startActivity(intent)
        }

        videoRecoveryCard.setOnClickListener {
            // Mở màn hình khôi phục video
            val intent = Intent(this, VideoRecoveryActivity::class.java)
            startActivity(intent)
        }

        fileRecoveryCard.setOnClickListener {
            // Mở màn hình khôi phục tệp tin khác
            val intent = Intent(this, FileRecoveryActivity::class.java)
            startActivity(intent)
        }

        settingsButton.setOnClickListener {
            // Hiện tại chỉ hiển thị thông báo
            Toast.makeText(this, getString(R.string.settings), Toast.LENGTH_SHORT).show()
        }

        // Initialize statistics values to zero (in a real app, these would be loaded from storage)
        updateStatistics(0, 0, 0, 0, 0, 0)
    }

    private fun updateStatistics(
        photoCount: Int, photoSize: Long,
        videoCount: Int, videoSize: Long,
        otherCount: Int, otherSize: Long
    ) {
        val totalFiles = photoCount + videoCount + otherCount
        val totalSize = photoSize + videoSize + otherSize

        // Update total statistics
        findViewById<TextView>(R.id.tvTotalFiles).text = "$totalFiles ${getString(R.string.files)}"
        findViewById<TextView>(R.id.tvTotalBytes).text = "${formatSize(totalSize)}"

        // Update photo statistics
        findViewById<TextView>(R.id.tvPhotoCount).text = "$photoCount ${getString(R.string.files)}"
        findViewById<TextView>(R.id.tvPhotoSize).text = formatSize(photoSize)

        // Update video statistics
        findViewById<TextView>(R.id.tvVideoCount).text = "$videoCount ${getString(R.string.files)}"
        findViewById<TextView>(R.id.tvVideoSize).text = formatSize(videoSize)

        // Update other files statistics
        findViewById<TextView>(R.id.tvOthersCount).text = "$otherCount ${getString(R.string.files)}"
        findViewById<TextView>(R.id.tvOthersSize).text = formatSize(otherSize)
    }

    private fun formatSize(size: Long): String {
        val kb = 1024L
        val mb = kb * 1024
        val gb = mb * 1024

        return when {
            size < kb -> "$size ${getString(R.string.bytes)}"
            size < mb -> String.format("%.2f KB", size.toFloat() / kb)
            size < mb * 1024 -> String.format("%.2f MB", size.toFloat() / mb)
            else -> String.format("%.2f GB", size.toFloat() / gb)
        }
    }
} 