package com.htnguyen.ifu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.htnguyen.ifu.R
import com.htnguyen.ifu.model.RecoveredFile
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter để hiển thị danh sách các tệp tin đã khôi phục
 */
class RecoveredFileAdapter(private var files: List<RecoveredFile>) : 
    RecyclerView.Adapter<RecoveredFileAdapter.FileViewHolder>() {

    // Định dạng ngày giờ
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

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

    /**
     * ViewHolder để hiển thị mỗi mục tệp tin
     */
    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fileIcon: ImageView = itemView.findViewById(R.id.ivFileIcon)
        private val fileName: TextView = itemView.findViewById(R.id.tvFileName)
        private val fileDetails: TextView = itemView.findViewById(R.id.tvFileDetails)

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
                isImageFile(file.name) -> fileIcon.setImageResource(R.drawable.ic_photo)
                isVideoFile(file.name) -> fileIcon.setImageResource(R.drawable.ic_video)
                else -> fileIcon.setImageResource(R.drawable.ic_document)
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