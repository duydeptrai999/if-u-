package com.htnguyen.ifu

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.htnguyen.ifu.adapter.RecoveredFileAdapter
import com.htnguyen.ifu.model.RecoveredFile
import java.util.*

/**
 * Activity hiển thị chi tiết các tệp tin đã khôi phục theo từng loại
 */
class RecoveredFilesDetailActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecoveredFileAdapter
    private lateinit var categoryTitle: TextView
    private lateinit var emptyView: TextView
    private var category: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovered_files_detail)

        // Lấy thông tin loại tệp tin từ intent
        category = intent.getStringExtra("category") ?: "photos"

        setupViews()
        setupRecyclerView()
        loadRecoveredFiles()
    }

    /**
     * Thiết lập các thành phần giao diện
     */
    private fun setupViews() {
        // Thiết lập recycler view và các thành phần khác
        recyclerView = findViewById(R.id.recyclerView)
        categoryTitle = findViewById(R.id.tvCategoryTitle)
        emptyView = findViewById(R.id.tvEmptyState)

        // Thiết lập tiêu đề tương ứng với loại
        val titleResId = when (category) {
            "photos" -> R.string.recovered_photos
            "videos" -> R.string.recovered_videos
            else -> R.string.recovered_other_files
        }
        categoryTitle.setText(titleResId)

        // Thiết lập nút quay lại
        val backButton = findViewById<ImageView>(R.id.ivBack)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    /**
     * Thiết lập RecyclerView để hiển thị danh sách tệp tin
     */
    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecoveredFileAdapter(emptyList())
        recyclerView.adapter = adapter
    }

    /**
     * Tải danh sách tệp tin đã khôi phục dựa trên loại
     */
    private fun loadRecoveredFiles() {
        // Trong ứng dụng thực tế, dữ liệu sẽ được lấy từ cơ sở dữ liệu hoặc bộ nhớ
        val files = when (category) {
            "photos" -> getRecoveredPhotoFiles()
            "videos" -> getRecoveredVideoFiles()
            else -> getRecoveredOtherFiles()
        }

        // Cập nhật adapter với danh sách tệp tin
        adapter.updateFiles(files)

        // Hiển thị trạng thái trống nếu không có tệp nào
        if (files.isEmpty()) {
            emptyView.visibility = android.view.View.VISIBLE
            recyclerView.visibility = android.view.View.GONE
        } else {
            emptyView.visibility = android.view.View.GONE
            recyclerView.visibility = android.view.View.VISIBLE
        }
    }

    /**
     * Hàm mô phỏng việc lấy danh sách ảnh đã khôi phục
     */
    private fun getRecoveredPhotoFiles(): List<RecoveredFile> {
        // Mô phỏng danh sách ảnh đã khôi phục
        return listOf(
            RecoveredFile("/storage/emulated/0/Pictures/recovered_1.jpg", "recovered_1.jpg", 1024 * 1024, Date(), false),
            RecoveredFile("/storage/emulated/0/Pictures/recovered_2.jpg", "recovered_2.jpg", 1024 * 1024 * 2, Date(), false),
            RecoveredFile("/storage/emulated/0/Pictures/recovered_3.jpg", "recovered_3.jpg", 1024 * 1024 * 3, Date(), false),
            RecoveredFile("/storage/emulated/0/Pictures/recovered_4.jpg", "recovered_4.jpg", 1024 * 1024 * 1, Date(), false)
        )
    }

    /**
     * Hàm mô phỏng việc lấy danh sách video đã khôi phục
     */
    private fun getRecoveredVideoFiles(): List<RecoveredFile> {
        // Không có video nào đã được khôi phục
        return emptyList()
    }

    /**
     * Hàm mô phỏng việc lấy danh sách tệp tin khác đã khôi phục
     */
    private fun getRecoveredOtherFiles(): List<RecoveredFile> {
        // Không có tệp tin khác nào đã được khôi phục
        return emptyList()
    }
} 