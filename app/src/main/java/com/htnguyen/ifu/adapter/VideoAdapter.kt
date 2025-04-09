package com.htnguyen.ifu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.htnguyen.ifu.R
import com.htnguyen.ifu.model.RecoverableItem
import java.text.DecimalFormat
import java.util.*

class VideoAdapter(
    private val videoList: List<RecoverableItem>,
    private val selectionChangeListener: (Boolean) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recoverable_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoItem = videoList[position]
        val file = videoItem.getFile()

        // Hiển thị tên tập tin
        holder.txtVideoName.text = file.name
        
        // Hiển thị kích thước tập tin
        holder.txtVideoSize.text = formatFileSize(videoItem.getSize())
        
        // Tải và hiển thị thumbnail
        Glide.with(holder.itemView.context)
            .load(file)
            .centerCrop()
            .placeholder(R.drawable.placeholder_video)
            .into(holder.imgThumbnail)
        
        // Cập nhật trạng thái checkbox
        holder.cbSelect.isChecked = videoItem.isSelected()
        
        // Xử lý sự kiện khi người dùng nhấn vào checkbox
        holder.cbSelect.setOnClickListener {
            videoItem.toggleSelection()
            // Thông báo thay đổi để cập nhật UI
            selectionChangeListener(checkIfAnySelected())
        }
        
        // Xử lý sự kiện khi người dùng nhấn vào item
        holder.itemView.setOnClickListener {
            videoItem.toggleSelection()
            holder.cbSelect.isChecked = videoItem.isSelected()
            // Thông báo thay đổi để cập nhật UI
            selectionChangeListener(checkIfAnySelected())
        }
    }

    override fun getItemCount(): Int = videoList.size

    /**
     * Kiểm tra xem có bất kỳ video nào được chọn hay không
     */
    private fun checkIfAnySelected(): Boolean {
        return videoList.any { it.isSelected() }
    }
    
    /**
     * Định dạng kích thước tập tin sang dạng dễ đọc
     */
    private fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        
        val decimalFormat = DecimalFormat("#,##0.#")
        return decimalFormat.format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }

    /**
     * ViewHolder để giữ các thành phần UI cho mỗi mục video
     */
    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgThumbnail: ImageView = itemView.findViewById(R.id.imgVideoThumbnail)
        val txtVideoName: TextView = itemView.findViewById(R.id.txt_video_name)
        val txtVideoSize: TextView = itemView.findViewById(R.id.txt_video_size)
        val cbSelect: CheckBox = itemView.findViewById(R.id.cb_select_video)
    }
} 