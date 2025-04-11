package com.restore.trashfiles

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.restore.trashfiles.adapter.RecoveredFileAdapter
import com.restore.trashfiles.db.RecoveredFilesDatabase
import com.restore.trashfiles.model.RecoveredFile
import java.io.File

/**
 * Activity hiển thị chi tiết các tệp tin đã khôi phục theo từng loại
 */
class RecoveredFilesDetailActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecoveredFileAdapter
    private lateinit var categoryTitle: TextView
    private lateinit var emptyView: TextView
    private lateinit var normalToolbar: View
    private lateinit var selectionToolbar: View
    private lateinit var selectedCountText: TextView
    private lateinit var shareButton: ImageView
    private lateinit var shareSelectedButton: Button
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
        normalToolbar = findViewById(R.id.toolbar)
        selectionToolbar = findViewById(R.id.selectionToolbar)
        selectedCountText = findViewById(R.id.selectedCountText)
        shareButton = findViewById(R.id.ivShare)
        shareSelectedButton = findViewById(R.id.shareSelectedButton)

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
        
        // Thiết lập nút chia sẻ (chỉ hiển thị cho ảnh và video)
        if (category == RecoveredFilesDatabase.TYPE_PHOTO || category == RecoveredFilesDatabase.TYPE_VIDEO) {
            shareButton.visibility = View.VISIBLE
            shareButton.setOnClickListener {
                enterSelectionMode()
            }
        }
        
        // Thiết lập nút đóng chế độ chọn
        val closeSelectionButton = findViewById<ImageView>(R.id.ivCloseSelection)
        closeSelectionButton.setOnClickListener {
            exitSelectionMode()
        }
        
        // Thiết lập nút chia sẻ các mục đã chọn
        shareSelectedButton.setOnClickListener {
            shareSelectedFiles()
        }
    }

    /**
     * Thiết lập RecyclerView để hiển thị danh sách tệp tin
     */
    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecoveredFileAdapter(emptyList())
        
        // Thiết lập listener khi nhấn vào một mục
        adapter.setOnItemClickListener { file ->
            openFileViewer(file)
        }
        
        // Thiết lập listener khi thay đổi số lượng mục đã chọn
        adapter.setOnSelectionChangeListener { selectedCount ->
            updateSelectedCount(selectedCount)
        }
        
        recyclerView.adapter = adapter
    }
    
    /**
     * Mở FileViewerActivity để xem file
     */
    private fun openFileViewer(file: RecoveredFile) {
        val intent = Intent(this, FileViewerActivity::class.java).apply {
            putExtra("file_path", file.path)
            putExtra("file_name", file.name)
            putExtra("file_type", when {
                isImageFile(file.name) -> "image"
                isVideoFile(file.name) -> "video"
                else -> "other"
            })
        }
        startActivity(intent)
    }
    
    /**
     * Vào chế độ chọn nhiều
     */
    private fun enterSelectionMode() {
        adapter.toggleSelectionMode(true)
        normalToolbar.visibility = View.GONE
        selectionToolbar.visibility = View.VISIBLE
        updateSelectedCount(0)
    }
    
    /**
     * Thoát chế độ chọn nhiều
     */
    private fun exitSelectionMode() {
        adapter.toggleSelectionMode(false)
        normalToolbar.visibility = View.VISIBLE
        selectionToolbar.visibility = View.GONE
    }
    
    /**
     * Cập nhật hiển thị số lượng mục đã chọn
     */
    private fun updateSelectedCount(count: Int) {
        selectedCountText.text = getString(R.string.selected_items_count, count)
        
        // Chỉ cho phép chia sẻ khi đã chọn ít nhất một mục
        shareSelectedButton.isEnabled = count > 0
        shareSelectedButton.alpha = if (count > 0) 1.0f else 0.5f
    }
    
    /**
     * Chia sẻ các file đã chọn
     */
    private fun shareSelectedFiles() {
        val selectedFiles = adapter.getSelectedFiles()
        
        if (selectedFiles.isEmpty()) {
            Toast.makeText(this, R.string.no_items_selected, Toast.LENGTH_SHORT).show()
            return
        }
        
        // Nếu chỉ có 1 file, sử dụng cách chia sẻ đơn giản
        if (selectedFiles.size == 1) {
            shareSingleFile(selectedFiles[0])
            return
        }
        
        // Chia sẻ nhiều file
        shareMultipleFiles(selectedFiles)
    }
    
    /**
     * Chia sẻ một file duy nhất
     */
    private fun shareSingleFile(file: RecoveredFile) {
        try {
            val fileObj = File(file.path)
            if (!fileObj.exists()) {
                Toast.makeText(this, R.string.share_error, Toast.LENGTH_SHORT).show()
                return
            }
            
            val fileUri = FileProvider.getUriForFile(
                this,
                applicationContext.packageName + ".provider",
                fileObj
            )
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = getMimeType(file.name)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            startActivity(Intent.createChooser(intent, getString(R.string.share_title)))
            
        } catch (e: Exception) {
            Toast.makeText(this, R.string.share_error, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    
    /**
     * Chia sẻ nhiều file cùng lúc
     */
    private fun shareMultipleFiles(files: List<RecoveredFile>) {
        try {
            val fileUris = ArrayList<Uri>()
            var mimeType = "*/*"
            
            // Xác định tất cả là ảnh hay video hay hỗn hợp
            val allImages = files.all { isImageFile(it.name) }
            val allVideos = files.all { isVideoFile(it.name) }
            
            // Set mime type dựa trên loại file
            mimeType = when {
                allImages -> "image/*"
                allVideos -> "video/*"
                else -> "*/*"
            }
            
            // Tạo URI cho mỗi file
            for (file in files) {
                val fileObj = File(file.path)
                if (fileObj.exists()) {
                    val uri = FileProvider.getUriForFile(
                        this,
                        applicationContext.packageName + ".provider",
                        fileObj
                    )
                    fileUris.add(uri)
                }
            }
            
            if (fileUris.isEmpty()) {
                Toast.makeText(this, R.string.share_error, Toast.LENGTH_SHORT).show()
                return
            }
            
            val intent = Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                type = mimeType
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            startActivity(Intent.createChooser(intent, getString(R.string.share_title)))
            
        } catch (e: Exception) {
            Toast.makeText(this, R.string.share_error, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
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
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
    
    /**
     * Xác định MIME type dựa trên tên file
     */
    private fun getMimeType(fileName: String): String {
        return when {
            isImageFile(fileName) -> "image/*"
            isVideoFile(fileName) -> "video/*"
            else -> "*/*"
        }
    }
    
    /**
     * Kiểm tra xem file có phải là ảnh không
     */
    private fun isImageFile(fileName: String): Boolean {
        val extensions = listOf(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp")
        return extensions.any { fileName.lowercase().endsWith(it) }
    }
    
    /**
     * Kiểm tra xem file có phải là video không
     */
    private fun isVideoFile(fileName: String): Boolean {
        val extensions = listOf(".mp4", ".mkv", ".avi", ".mov", ".wmv", ".3gp")
        return extensions.any { fileName.lowercase().endsWith(it) }
    }
    
    /**
     * Xử lý nút Back
     */
    override fun onBackPressed() {
        // Nếu đang ở chế độ chọn, thoát chế độ chọn
        if (adapter.isInSelectionMode()) {
            exitSelectionMode()
        } else {
            super.onBackPressed()
        }
    }
} 