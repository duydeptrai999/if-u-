package com.htnguyen.ifu.adapter

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.htnguyen.ifu.R
import com.htnguyen.ifu.model.RecoveredFile
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.*

/**
 * Adapter để hiển thị danh sách các tệp tin đã khôi phục
 */
class RecoveredFileAdapter(private var files: List<RecoveredFile>) : 
    RecyclerView.Adapter<RecoveredFileAdapter.FileViewHolder>() {

    // Định dạng ngày giờ
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private val thumbnailCoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    /**
     * Cập nhật danh sách tệp tin và thông báo thay đổi
     */
    fun updateFiles(newFiles: List<RecoveredFile>) {
        this.files = newFiles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recovered_file, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        holder.bind(file)
    }

    override fun getItemCount(): Int = files.size
    
    override fun onViewRecycled(holder: FileViewHolder) {
        super.onViewRecycled(holder)
        holder.cancelThumbnailLoading()
    }
    
    fun onDestroy() {
        thumbnailCoroutineScope.cancel()
    }

    /**
     * ViewHolder để hiển thị mỗi mục tệp tin
     */
    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fileIcon: ImageView = itemView.findViewById(R.id.ivFileIcon)
        private val fileName: TextView = itemView.findViewById(R.id.tvFileName)
        private val fileDetails: TextView = itemView.findViewById(R.id.tvFileDetails)
        private val thumbnailView: ImageView = itemView.findViewById(R.id.ivThumbnail)
        private var thumbnailJob: Job? = null

        /**
         * Hiển thị thông tin tệp tin
         */
        fun bind(file: RecoveredFile) {
            fileName.text = file.name
            
            // Định dạng thông tin chi tiết tệp (kích thước và ngày)
            val sizeFormatted = formatSize(file.size)
            val dateFormatted = dateFormat.format(file.modifiedDate)
            fileDetails.text = "$sizeFormatted • $dateFormatted"
            
            // Thiết lập biểu tượng tương ứng với loại tệp
            when {
                isImageFile(file.name) -> {
                    fileIcon.setImageResource(R.drawable.ic_photo)
                    loadThumbnail(file.path, true)
                }
                isVideoFile(file.name) -> {
                    fileIcon.setImageResource(R.drawable.ic_video)
                    loadThumbnail(file.path, false)
                }
                else -> {
                    fileIcon.setImageResource(R.drawable.ic_document)
                    thumbnailView.visibility = View.GONE
                }
            }
            
            // Thiết lập sự kiện click để mở file
            itemView.setOnClickListener {
                openFile(file)
            }
        }
        
        /**
         * Hủy công việc tải thumbnail khi view bị recycle
         */
        fun cancelThumbnailLoading() {
            thumbnailJob?.cancel()
            thumbnailJob = null
        }
        
        /**
         * Tải thumbnail cho ảnh hoặc video
         */
        private fun loadThumbnail(filePath: String, isImage: Boolean) {
            thumbnailView.visibility = View.VISIBLE
            thumbnailView.setImageResource(R.drawable.ic_photo) // Placeholder
            
            cancelThumbnailLoading()
            
            thumbnailJob = thumbnailCoroutineScope.launch(Dispatchers.IO) {
                try {
                    if (isImage) {
                        // Tải thumbnail cho ảnh
                        val options = BitmapFactory.Options().apply {
                            inSampleSize = 8 // Giảm độ phân giải để tối ưu bộ nhớ
                        }
                        val bitmap = BitmapFactory.decodeFile(filePath, options)
                        
                        withContext(Dispatchers.Main) {
                            thumbnailView.setImageBitmap(bitmap)
                        }
                    } else {
                        // Tải thumbnail cho video
                        val retriever = MediaMetadataRetriever()
                        retriever.setDataSource(filePath)
                        val bitmap = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        retriever.release()
                        
                        withContext(Dispatchers.Main) {
                            if (bitmap != null) {
                                thumbnailView.setImageBitmap(bitmap)
                            } else {
                                // Nếu không thể lấy frame từ video
                                thumbnailView.setImageResource(R.drawable.ic_video)
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        // Nếu xảy ra lỗi, hiển thị icon mặc định
                        if (isImage) {
                            thumbnailView.setImageResource(R.drawable.ic_photo)
                        } else {
                            thumbnailView.setImageResource(R.drawable.ic_video)
                        }
                    }
                }
            }
        }
        
        /**
         * Mở file để xem
         */
        private fun openFile(file: RecoveredFile) {
            val context = itemView.context
            val fileObj = File(file.path)
            
            // Kiểm tra file có tồn tại không
            if (!fileObj.exists()) {
                android.widget.Toast.makeText(
                    context,
                    context.getString(R.string.open_file_error) + ": File không tồn tại",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                return
            }
            
            // Kiểm tra xem đây có phải là ảnh hoặc video không để sử dụng cách mở tương ứng
            if (isImageFile(file.name) || isVideoFile(file.name)) {
                try {
                    val fileUri = FileProvider.getUriForFile(
                        context,
                        context.applicationContext.packageName + ".provider",
                        fileObj
                    )
                    
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(fileUri, getMimeType(file.name))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    
                    // Kiểm tra xem có ứng dụng nào có thể xử lý intent này không
                    val activities = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                    if (activities.isNotEmpty()) {
                        context.startActivity(intent)
                    } else {
                        // Nếu không có ứng dụng khác, thử mở trong ứng dụng hiện tại
                        openInCurrentApp(context, file)
                    }
                } catch (e: Exception) {
                    // Nếu có lỗi, thử mở trong ứng dụng hiện tại
                    openInCurrentApp(context, file)
                    e.printStackTrace()
                }
            } else {
                // Đối với các tệp khác, sử dụng cách mở thông thường
                try {
                    val fileUri = FileProvider.getUriForFile(
                        context,
                        context.applicationContext.packageName + ".provider",
                        fileObj
                    )
                    
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(fileUri, getMimeType(file.name))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    } else {
                        android.widget.Toast.makeText(
                            context,
                            context.getString(R.string.open_file_error) + ": Không có ứng dụng hỗ trợ loại file này",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    android.widget.Toast.makeText(
                        context,
                        context.getString(R.string.open_file_error) + ": ${e.message}",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }
            }
        }
        
        /**
         * Mở file trong ứng dụng hiện tại
         */
        private fun openInCurrentApp(context: Context, file: RecoveredFile) {
            // Mở intent để xem chi tiết file trong ứng dụng hiện tại
            val intent = Intent(context, com.htnguyen.ifu.FileViewerActivity::class.java).apply {
                putExtra("file_path", file.path)
                putExtra("file_name", file.name)
                putExtra("file_type", if (isImageFile(file.name)) "image" else if (isVideoFile(file.name)) "video" else "other")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
        
        /**
         * Lấy loại MIME dựa trên tên file
         */
        private fun getMimeType(fileName: String): String {
            val lowerCaseName = fileName.lowercase(Locale.ROOT)
            
            return when {
                // Ảnh
                lowerCaseName.endsWith(".jpg") || lowerCaseName.endsWith(".jpeg") -> "image/jpeg"
                lowerCaseName.endsWith(".png") -> "image/png"
                lowerCaseName.endsWith(".gif") -> "image/gif"
                lowerCaseName.endsWith(".webp") -> "image/webp"
                lowerCaseName.endsWith(".bmp") -> "image/bmp"
                isImageFile(fileName) -> "image/*"
                
                // Video
                lowerCaseName.endsWith(".mp4") -> "video/mp4"
                lowerCaseName.endsWith(".3gp") -> "video/3gpp"
                lowerCaseName.endsWith(".mkv") -> "video/x-matroska"
                lowerCaseName.endsWith(".webm") -> "video/webm"
                lowerCaseName.endsWith(".avi") -> "video/x-msvideo"
                isVideoFile(fileName) -> "video/*"
                
                // Văn bản
                lowerCaseName.endsWith(".txt") -> "text/plain"
                lowerCaseName.endsWith(".html") || lowerCaseName.endsWith(".htm") -> "text/html"
                lowerCaseName.endsWith(".pdf") -> "application/pdf"
                
                // Tài liệu Office
                lowerCaseName.endsWith(".doc") || lowerCaseName.endsWith(".docx") -> "application/msword"
                lowerCaseName.endsWith(".xls") || lowerCaseName.endsWith(".xlsx") -> "application/vnd.ms-excel"
                lowerCaseName.endsWith(".ppt") || lowerCaseName.endsWith(".pptx") -> "application/vnd.ms-powerpoint"
                
                // Nén
                lowerCaseName.endsWith(".zip") -> "application/zip"
                lowerCaseName.endsWith(".rar") -> "application/x-rar-compressed"
                
                // Mặc định cho các loại tệp khác
                else -> "*/*"
            }
        }
        
        /**
         * Kiểm tra xem tệp tin có phải là ảnh không
         */
        private fun isImageFile(fileName: String): Boolean {
            val extensions = listOf(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp")
            return extensions.any { fileName.lowercase(Locale.ROOT).endsWith(it) }
        }
        
        /**
         * Kiểm tra xem tệp tin có phải là video không
         */
        private fun isVideoFile(fileName: String): Boolean {
            val extensions = listOf(".mp4", ".mkv", ".avi", ".mov", ".wmv", ".3gp")
            return extensions.any { fileName.lowercase(Locale.ROOT).endsWith(it) }
        }
        
        /**
         * Định dạng kích thước tệp tin
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
} 