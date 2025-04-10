package com.htnguyen.ifu

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.htnguyen.ifu.model.RecoveredFile
import java.util.*

/**
 * Activity hiển thị danh sách các tệp tin đã được khôi phục, được phân loại thành ảnh, video và tệp tin khác.
 */
class RecoveredFilesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovered_files)

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
        // Lấy dữ liệu về các tệp đã khôi phục (trong ứng dụng thực tế, dữ liệu sẽ được lấy từ cơ sở dữ liệu hoặc bộ nhớ)
        val photoFilesList = getRecoveredPhotoFiles()
        val videoFilesList = getRecoveredVideoFiles()
        val otherFilesList = getRecoveredOtherFiles()

        // Thiết lập thông tin cho mục ảnh
        setupCategoryItem(
            findViewById(R.id.cardPhotos),
            findViewById(R.id.tvPhotoDetails),
            R.string.photos,
            photoFilesList.size,
            calculateTotalSize(photoFilesList)
        )

        // Thiết lập thông tin cho mục video
        setupCategoryItem(
            findViewById(R.id.cardVideos),
            findViewById(R.id.tvVideoDetails),
            R.string.videos,
            videoFilesList.size,
            calculateTotalSize(videoFilesList)
        )

        // Thiết lập thông tin cho mục tệp tin khác
        setupCategoryItem(
            findViewById(R.id.cardOtherFiles),
            findViewById(R.id.tvOtherDetails),
            R.string.others,
            otherFilesList.size,
            calculateTotalSize(otherFilesList)
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
                R.string.photos -> "photos"
                R.string.videos -> "videos"
                else -> "others"
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
     * Hàm mô phỏng việc lấy danh sách ảnh đã khôi phục.
     * Trong ứng dụng thực tế, đây sẽ truy vấn từ cơ sở dữ liệu hoặc bộ nhớ.
     */
    private fun getRecoveredPhotoFiles(): List<RecoveredFile> {
        // Mô phỏng một số ảnh đã khôi phục
        // Trong ứng dụng thực tế, dữ liệu này sẽ được lấy từ cơ sở dữ liệu
        return listOf(
            RecoveredFile("/storage/emulated/0/Pictures/recovered_1.jpg", "recovered_1.jpg", 1024 * 1024, Date(), false),
            RecoveredFile("/storage/emulated/0/Pictures/recovered_2.jpg", "recovered_2.jpg", 1024 * 1024 * 2, Date(), false),
            RecoveredFile("/storage/emulated/0/Pictures/recovered_3.jpg", "recovered_3.jpg", 1024 * 1024 * 3, Date(), false),
            RecoveredFile("/storage/emulated/0/Pictures/recovered_4.jpg", "recovered_4.jpg", 1024 * 1024 * 1, Date(), false)
        )
    }

    /**
     * Hàm mô phỏng việc lấy danh sách video đã khôi phục.
     * Trong ứng dụng thực tế, đây sẽ truy vấn từ cơ sở dữ liệu hoặc bộ nhớ.
     */
    private fun getRecoveredVideoFiles(): List<RecoveredFile> {
        // Không có video nào đã được khôi phục
        return emptyList()
    }

    /**
     * Hàm mô phỏng việc lấy danh sách tệp tin khác đã khôi phục.
     * Trong ứng dụng thực tế, đây sẽ truy vấn từ cơ sở dữ liệu hoặc bộ nhớ.
     */
    private fun getRecoveredOtherFiles(): List<RecoveredFile> {
        // Không có tệp tin khác nào đã được khôi phục
        return emptyList()
    }

    /**
     * Tính tổng kích thước của danh sách tệp tin
     */
    private fun calculateTotalSize(files: List<RecoveredFile>): Long {
        return files.sumOf { it.size }
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