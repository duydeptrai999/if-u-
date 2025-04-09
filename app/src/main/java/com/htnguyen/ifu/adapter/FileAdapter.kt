package com.htnguyen.ifu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.htnguyen.ifu.R
import com.htnguyen.ifu.model.RecoverableItem

class FileAdapter(
    private val files: List<RecoverableItem>,
    private val onSelectionChanged: (Boolean) -> Unit
) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file_recovery, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        
        // Hiển thị tên file và kích thước
        holder.fileName.text = file.getFile().name
        holder.fileSize.text = file.getFormattedSize()
        
        // Hiển thị icon dựa vào loại file
        when (file.getType()) {
            "pdf" -> holder.fileIcon.setImageResource(R.drawable.ic_file)
            "word" -> holder.fileIcon.setImageResource(R.drawable.ic_file)
            "excel" -> holder.fileIcon.setImageResource(R.drawable.ic_file)
            "powerpoint" -> holder.fileIcon.setImageResource(R.drawable.ic_file)
            "archive" -> holder.fileIcon.setImageResource(R.drawable.ic_file)
            else -> holder.fileIcon.setImageResource(R.drawable.ic_file)
        }
            
        // Cập nhật trạng thái đã chọn
        holder.checkBox.isChecked = file.isSelected()
        
        // Xử lý sự kiện click
        holder.itemView.setOnClickListener {
            file.setSelected(!file.isSelected())
            holder.checkBox.isChecked = file.isSelected()
            checkIfAnySelected()
        }
        
        holder.checkBox.setOnClickListener {
            file.setSelected(holder.checkBox.isChecked)
            checkIfAnySelected()
        }
    }

    override fun getItemCount(): Int = files.size
    
    private fun checkIfAnySelected() {
        val anySelected = files.any { it.isSelected() }
        onSelectionChanged(anySelected)
    }
    
    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileIcon: ImageView = itemView.findViewById(R.id.fileIcon)
        val fileName: TextView = itemView.findViewById(R.id.fileName)
        val fileSize: TextView = itemView.findViewById(R.id.fileSize)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }
} 