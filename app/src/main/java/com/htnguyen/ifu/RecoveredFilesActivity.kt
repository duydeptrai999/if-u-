package com.htnguyen.ifu

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.htnguyen.ifu.db.RecoveredFilesDatabase
import com.htnguyen.ifu.model.RecoveredFile

/**
 * Activity hiển thị danh sách các tệp tin đã được khôi phục, được phân loại thành ảnh, video và tệp tin khác.
 */
class RecoveredFilesActivity : AppCompatActivity() {

    private lateinit var db: RecoveredFilesDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovered_files)

        db = RecoveredFilesDatabase.getInstance(this)
        
        setupActionBar()
        setupCategoryViews()
    }

    /**
     * Thiết lập thanh công cụ và nút quay lại
     */
    private fun setupActionBar() {
        // Thiết lập nút quay lại
        val backButton = findViewById<ImageView>(R.id.ivBack)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    /**
     * Thiết lập các thành phần hiển thị cho các loại tệp đã khôi phục
     */
    private fun setupCategoryViews() {
        // Lấy dữ liệu về các tệp đã khôi phục từ database
        val photoCount = db.countRecoveredFiles(RecoveredFilesDatabase.TYPE_PHOTO)
        val videoCount = db.countRecoveredFiles(RecoveredFilesDatabase.TYPE_VIDEO)
        val otherCount = db.countRecoveredFiles(RecoveredFilesDatabase.TYPE_FILE)
        
        val photoSize = db.getTotalSize(RecoveredFilesDatabase.TYPE_PHOTO)
        val videoSize = db.getTotalSize(RecoveredFilesDatabase.TYPE_VIDEO)
        val otherSize = db.getTotalSize(RecoveredFilesDatabase.TYPE_FILE)

        // Thiết lập thông tin cho mục ảnh
        setupCategoryItem(
            findViewById(R.id.cardPhotos),
            findViewById(R.id.tvPhotoDetails),
            R.string.photos,
            photoCount,
            photoSize
        )

        // Thiết lập thông tin cho mục video
        setupCategoryItem(
            findViewById(R.id.cardVideos),
            findViewById(R.id.tvVideoDetails),
            R.string.videos,
            videoCount,
            videoSize
        )

        // Thiết lập thông tin cho mục tệp tin khác
        setupCategoryItem(
            findViewById(R.id.cardOtherFiles),
            findViewById(R.id.tvOtherDetails),
            R.string.others,
            otherCount,
            otherSize
        )
    }

    /**
     * Thiết lập thông tin cho một mục hiển thị
     */
    private fun setupCategoryItem(
        cardView: CardView,
        textView: TextView,
        titleResId: Int,
        count: Int,
        totalSize: Long
    ) {
        // Hiển thị thông tin số lượng tệp và kích thước
        val sizeFormatted = formatSize(totalSize)
        textView.text = "$count ${getString(R.string.files)} · $sizeFormatted"

        // Thiết lập sự kiện khi nhấn vào mục
        cardView.setOnClickListener {
            // Xác định loại mục được chọn
            val category = when (titleResId) {
                R.string.photos -> RecoveredFilesDatabase.TYPE_PHOTO
                R.string.videos -> RecoveredFilesDatabase.TYPE_VIDEO
                else -> RecoveredFilesDatabase.TYPE_FILE
            }

            // Chuyển đến màn hình xem chi tiết tương ứng
            val intent = Intent(this, RecoveredFilesDetailActivity::class.java)
            intent.putExtra("category", category)
            startActivity(intent)
        }

        // Ẩn mục nếu không có tệp nào
        if (count == 0) {
            cardView.visibility = View.GONE
        } else {
            cardView.visibility = View.VISIBLE
        }
    }

    /**
     * Định dạng kích thước tệp tin để hiển thị
     */
    private fun formatSize(size: Long): String {
        if (size < 1024) {
            return "$size B"
        } else if (size < 1024 * 1024) {
            return "${size / 1024} KB"
        } else if (size < 1024 * 1024 * 1024) {
            val sizeInMB = size.toFloat() / (1024 * 1024)
            return String.format("%.1f MB", sizeInMB)
        } else {
            val sizeInGB = size.toFloat() / (1024 * 1024 * 1024)
            return String.format("%.2f GB", sizeInGB)
        }
    }
} 