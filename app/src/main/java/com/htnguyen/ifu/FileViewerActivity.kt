package com.htnguyen.ifu

import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

/**
 * Activity hiển thị nội dung file (ảnh, video) trực tiếp trong ứng dụng
 */
class FileViewerActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var videoView: VideoView
    private lateinit var fileNameText: TextView
    private lateinit var backButton: ImageView
    private lateinit var noPreviewText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_viewer)

        // Lấy thông tin file từ intent
        val filePath = intent.getStringExtra("file_path") ?: ""
        val fileName = intent.getStringExtra("file_name") ?: ""
        val fileType = intent.getStringExtra("file_type") ?: "other"

        // Khởi tạo các view
        setupViews()
        
        // Hiển thị tên file
        fileNameText.text = fileName

        // Kiểm tra file tồn tại
        val file = File(filePath)
        if (!file.exists()) {
            showError("File không tồn tại")
            return
        }

        // Hiển thị nội dung tương ứng với loại file
        when (fileType) {
            "image" -> showImage(file)
            "video" -> showVideo(file)
            else -> showNoPreview()
        }
    }

    /**
     * Khởi tạo các view
     */
    private fun setupViews() {
        imageView = findViewById(R.id.imageView)
        videoView = findViewById(R.id.videoView)
        fileNameText = findViewById(R.id.fileNameText)
        backButton = findViewById(R.id.ivBack)
        noPreviewText = findViewById(R.id.noPreviewText)

        // Thiết lập nút quay lại
        backButton.setOnClickListener {
            finish()
        }
    }

    /**
     * Hiển thị ảnh
     */
    private fun showImage(file: File) {
        try {
            // Hiển thị ImageView, ẩn VideoView
            imageView.visibility = View.VISIBLE
            videoView.visibility = View.GONE
            noPreviewText.visibility = View.GONE

            // Tải và hiển thị ảnh
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            showError("Không thể hiển thị ảnh: ${e.message}")
        }
    }

    /**
     * Hiển thị video
     */
    private fun showVideo(file: File) {
        try {
            // Hiển thị VideoView, ẩn ImageView
            videoView.visibility = View.VISIBLE
            imageView.visibility = View.GONE
            noPreviewText.visibility = View.GONE

            // Thiết lập MediaController cho VideoView
            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)

            // Thiết lập nguồn video
            val videoUri = Uri.fromFile(file)
            videoView.setVideoURI(videoUri)

            // Thiết lập các sự kiện cho VideoView
            videoView.setOnPreparedListener { mp ->
                mp.setOnVideoSizeChangedListener { _, _, _ ->
                    mediaController.show()
                }
            }

            videoView.setOnErrorListener { _, what, extra ->
                showError("Lỗi phát video (${what}, ${extra})")
                true
            }

            // Bắt đầu phát video
            videoView.start()
        } catch (e: Exception) {
            showError("Không thể phát video: ${e.message}")
        }
    }

    /**
     * Hiển thị thông báo không có xem trước cho loại file
     */
    private fun showNoPreview() {
        imageView.visibility = View.GONE
        videoView.visibility = View.GONE
        noPreviewText.visibility = View.VISIBLE
    }

    /**
     * Hiển thị thông báo lỗi
     */
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        showNoPreview()
    }

    /**
     * Dừng video khi activity bị dừng
     */
    override fun onPause() {
        super.onPause()
        if (::videoView.isInitialized && videoView.isPlaying) {
            videoView.pause()
        }
    }

    /**
     * Giải phóng tài nguyên khi activity bị hủy
     */
    override fun onDestroy() {
        super.onDestroy()
        if (::videoView.isInitialized) {
            videoView.stopPlayback()
        }
    }
} 