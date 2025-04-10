package com.htnguyen.ifu

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.htnguyen.ifu.adapter.RecoveredFileAdapter
import com.htnguyen.ifu.db.RecoveredFilesDatabase
import com.htnguyen.ifu.model.RecoveredFile

/**
 * Activity hiển thị chi tiết các tệp tin đã khôi phục theo từng loại
 */
class RecoveredFilesDetailActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecoveredFileAdapter
    private lateinit var categoryTitle: TextView
    private lateinit var emptyView: TextView
    private lateinit var db: RecoveredFilesDatabase
    private var category: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovered_files_detail)

        // Lấy thông tin loại tệp tin từ intent
        category = intent.getStringExtra("category") ?: RecoveredFilesDatabase.TYPE_PHOTO
        
        // Khởi tạo database
        db = RecoveredFilesDatabase.getInstance(this)

        setupViews()
        setupRecyclerView()
        loadRecoveredFiles()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Thu hồi tài nguyên của adapter khi activity bị hủy
        if (::adapter.isInitialized) {
            adapter.onDestroy()
        }
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
            RecoveredFilesDatabase.TYPE_PHOTO -> R.string.recovered_photos
            RecoveredFilesDatabase.TYPE_VIDEO -> R.string.recovered_videos
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
        // Lấy dữ liệu từ database
        val files = db.getRecoveredFiles(category)

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
} 