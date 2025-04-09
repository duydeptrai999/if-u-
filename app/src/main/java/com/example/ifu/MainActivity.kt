package com.example.ifu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.cardview.widget.CardView
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupUI()
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
            // For now just show a toast message
            Toast.makeText(this, getString(R.string.recover_photos), Toast.LENGTH_SHORT).show()
            // In a real app, navigate to photo recovery screen
        }

        videoRecoveryCard.setOnClickListener {
            // For now just show a toast message
            Toast.makeText(this, getString(R.string.recover_videos), Toast.LENGTH_SHORT).show()
            // In a real app, navigate to video recovery screen
        }

        fileRecoveryCard.setOnClickListener {
            // For now just show a toast message
            Toast.makeText(this, getString(R.string.recover_other_files), Toast.LENGTH_SHORT).show()
            // In a real app, navigate to other files recovery screen
        }

        settingsButton.setOnClickListener {
            // For now just show a toast message
            Toast.makeText(this, getString(R.string.settings), Toast.LENGTH_SHORT).show()
            // In a real app, navigate to settings screen
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
            size < gb -> String.format("%.2f MB", size.toFloat() / mb)
            else -> String.format("%.2f GB", size.toFloat() / gb)
        }
    }
} 