package com.htnguyen.ifu.model

import java.io.File

/**
 * Lớp đại diện cho một mục có thể khôi phục (video, hình ảnh, v.v.)
 */
class RecoverableItem(private val file: File, private val size: Long, private val type: String) {
    
    private var selected: Boolean = false
    
    /**
     * Trả về đối tượng File
     */
    fun getFile(): File = file
    
    /**
     * Trả về đường dẫn của file
     */
    fun getPath(): String = file.absolutePath
    
    /**
     * Trả về kích thước tập tin (bytes)
     */
    fun getSize(): Long = size
    
    /**
     * Trả về loại tập tin (image, video, v.v.)
     */
    fun getType(): String = type
    
    /**
     * Kiểm tra xem mục này có được chọn hay không
     */
    fun isSelected(): Boolean = selected
    
    /**
     * Đặt trạng thái chọn cho mục này
     */
    fun setSelected(selected: Boolean) {
        this.selected = selected
    }
    
    /**
     * Chuyển đổi trạng thái chọn
     */
    fun toggleSelection() {
        selected = !selected
    }
    
    /**
     * Trả về kích thước tập tin đã được định dạng
     */
    fun getFormattedSize(): String {
        if (size < 1024) {
            return "$size B"
        } else if (size < 1024 * 1024) {
            return "${Math.round(size / 1024.0)} KB"
        } else if (size < 1024 * 1024 * 1024) {
            return "${Math.round(size / (1024.0 * 1024.0) * 10) / 10.0} MB"
        } else {
            return "${Math.round(size / (1024.0 * 1024.0 * 1024.0) * 10) / 10.0} GB"
        }
    }
} 