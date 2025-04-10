package com.restore.trashfiles

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.cardview.widget.CardView
import android.widget.ImageView
import android.widget.TextView
import com.restore.trashfiles.recovery.FileRecoveryActivity
import com.restore.trashfiles.recovery.PhotoRecoveryActivity
import com.restore.trashfiles.recovery.VideoRecoveryActivity
import android.view.WindowManager
import android.os.Build
import android.view.View
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.restore.trashfiles.ads.AdHelper

class MainActivity : BaseActivity() {

    private val storagePermissionRequest = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                // Đã cấp quyền truy cập tất cả file
                setupUI()
            } else {
                // Từ chối quyền
                showPermissionDeniedDialog()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransparentStatusBar()
        setContentView(R.layout.activity_main)

        // Kiểm tra và yêu cầu quyền truy cập
        checkStoragePermission()
        
        // Hiển thị quảng cáo banner
        setupAds()
    }
    
    private fun setupAds() {
        // Hiển thị banner ads
        val adContainer = findViewById<FrameLayout>(R.id.adViewContainer)
        AdHelper.showBannerAd(this, adContainer)
        
        // Preload rewarded ads để có thể sử dụng sau này
        AdHelper.preloadRewardedAd(this)
    }
    
    override fun onResume() {
        super.onResume()
        // Cập nhật thống kê mỗi khi trở lại màn hình chính (sau khi khôi phục)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                updateStatisticsFromDatabase()
            }
        } else if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED) {
            updateStatisticsFromDatabase()
        }
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                // Đã có quyền truy cập tất cả file
                setupUI()
            } else {
                // Hiển thị dialog yêu cầu quyền
                showStoragePermissionDialog()
            }
        } else {
            // Với Android 10 trở xuống, sử dụng quyền READ/WRITE_EXTERNAL_STORAGE
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    setupUI()
                }
                else -> {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        STORAGE_PERMISSION_CODE
                    )
                }
            }
        }
    }

    private fun showStoragePermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.storage_permission_title)
            .setMessage(R.string.storage_permission_message)
            .setPositiveButton(R.string.storage_permission_button) { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.data = Uri.parse("package:$packageName")
                        storagePermissionRequest.launch(intent)
                    } catch (e: Exception) {
                        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        storagePermissionRequest.launch(intent)
                    }
                }
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
                showPermissionDeniedDialog()
            }
            .setCancelable(false)
            .show()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.storage_permission_title)
            .setMessage(R.string.storage_permission_denied)
            .setPositiveButton(R.string.storage_permission_settings) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                finish()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    setupUI()
                } else {
                    showPermissionDeniedDialog()
                }
                return
            }
        }
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
        
        // Thêm xử lý khi nhấn vào phần thống kê đã khôi phục
        val statsCard = findViewById<CardView>(R.id.statsCard)
        
        // Settings button
        val settingsButton = findViewById<ImageView>(R.id.ivSettings)

        // Set click listeners for each recovery option
        photoRecoveryCard.setOnClickListener {
            // Mở màn hình khôi phục ảnh
            val intent = Intent(this, PhotoRecoveryActivity::class.java)
            startActivity(intent)
            
            // Hiển thị interstitial ad khi chuyển màn hình
            AdHelper.showInterstitialAd(this)
        }

        videoRecoveryCard.setOnClickListener {
            // Mở màn hình khôi phục video
            val intent = Intent(this, VideoRecoveryActivity::class.java)
            startActivity(intent)
            
            // Hiển thị interstitial ad khi chuyển màn hình
            AdHelper.showInterstitialAd(this)
        }

        fileRecoveryCard.setOnClickListener {
            // Mở màn hình khôi phục tệp tin khác
            val intent = Intent(this, FileRecoveryActivity::class.java)
            startActivity(intent)
            
            // Hiển thị interstitial ad khi chuyển màn hình
            AdHelper.showInterstitialAd(this)
        }
        
        // Xử lý sự kiện khi nhấn vào phần thống kê đã khôi phục
        statsCard.setOnClickListener {
            // Mở màn hình danh sách tệp tin đã khôi phục
            val intent = Intent(this, RecoveredFilesActivity::class.java)
            startActivity(intent)
        }

        settingsButton.setOnClickListener {
            // Mở màn hình cài đặt
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Đọc thông tin từ database và cập nhật giao diện
        updateStatisticsFromDatabase()
    }

    /**
     * Đọc thông tin thống kê từ database và cập nhật giao diện
     */
    private fun updateStatisticsFromDatabase() {
        val db = com.restore.trashfiles.db.RecoveredFilesDatabase.getInstance(this)
        
        // Đếm số lượng và tính tổng kích thước của các loại tệp
        val photoCount = db.countRecoveredFiles(com.restore.trashfiles.db.RecoveredFilesDatabase.TYPE_PHOTO)
        val videoCount = db.countRecoveredFiles(com.restore.trashfiles.db.RecoveredFilesDatabase.TYPE_VIDEO)
        val fileCount = db.countRecoveredFiles(com.restore.trashfiles.db.RecoveredFilesDatabase.TYPE_FILE)
        
        val photoSize = db.getTotalSize(com.restore.trashfiles.db.RecoveredFilesDatabase.TYPE_PHOTO)
        val videoSize = db.getTotalSize(com.restore.trashfiles.db.RecoveredFilesDatabase.TYPE_VIDEO)
        val fileSize = db.getTotalSize(com.restore.trashfiles.db.RecoveredFilesDatabase.TYPE_FILE)
        
        // Cập nhật giao diện
        updateStatistics(photoCount, photoSize, videoCount, videoSize, fileCount, fileSize)
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
    
    companion object {
        private const val STORAGE_PERMISSION_CODE = 100
    }
} 