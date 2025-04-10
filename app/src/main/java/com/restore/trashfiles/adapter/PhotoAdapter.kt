package com.restore.trashfiles.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.restore.trashfiles.R
import com.restore.trashfiles.model.RecoverableItem
import android.util.Log

class PhotoAdapter(
    private val photos: List<RecoverableItem>,
    private val onSelectionChanged: (Boolean) -> Unit
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo_recovery, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        
        try {
            // Hiển thị tên file và kích thước
            Log.d("PhotoAdapter", "Binding view for photo: ${photo.getFile().name}")
            holder.fileName.text = photo.getFile().name
            holder.fileSize.text = photo.getFormattedSize()
            
            // Hiển thị hình ảnh xem trước
            if (photo.getFile().exists() && photo.getFile().canRead()) {
                Glide.with(holder.itemView.context)
                    .load(photo.getFile())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.ic_broken_image)
                    .centerCrop()
                    .into(holder.imagePreview)
            } else {
                // Nếu không thể đọc file, hiển thị ảnh lỗi
                holder.imagePreview.setImageResource(R.drawable.ic_broken_image)
                Log.d("PhotoAdapter", "File không tồn tại hoặc không thể đọc: ${photo.getFile().absolutePath}")
            }
                
            // Cập nhật trạng thái đã chọn
            holder.checkBox.isChecked = photo.isSelected()
            
            // Thêm hiển thị cho ảnh đã xóa
            if (photo.isDeleted()) {
                // Đánh dấu rõ ràng đây là ảnh đã xóa
                holder.deletedIndicator.visibility = View.VISIBLE
            } else {
                holder.deletedIndicator.visibility = View.GONE
            }
            
            // Xử lý sự kiện click
            holder.itemView.setOnClickListener {
                photo.toggleSelection()
                holder.checkBox.isChecked = photo.isSelected()
                
                // Kiểm tra xem có phần tử nào được chọn không
                val hasSelected = photos.any { it.isSelected() }
                onSelectionChanged(hasSelected)
            }
            
            holder.checkBox.setOnClickListener {
                photo.setSelected(holder.checkBox.isChecked)
                checkIfAnySelected()
            }
        } catch (e: Exception) {
            Log.e("PhotoAdapter", "Lỗi khi hiển thị ảnh: ${e.message}")
            // Hiển thị thông tin lỗi
            holder.fileName.text = "Lỗi hiển thị ảnh"
            holder.fileSize.text = ""
            holder.imagePreview.setImageResource(R.drawable.ic_broken_image)
        }
    }

    override fun getItemCount(): Int = photos.size
    
    private fun checkIfAnySelected() {
        val anySelected = photos.any { it.isSelected() }
        onSelectionChanged(anySelected)
    }
    
    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagePreview: ImageView = itemView.findViewById(R.id.imagePreview)
        val fileName: TextView = itemView.findViewById(R.id.fileName)
        val fileSize: TextView = itemView.findViewById(R.id.fileSize)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        val deletedIndicator: View = itemView.findViewById(R.id.deletedIndicator)
    }
} 