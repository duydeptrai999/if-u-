package com.restore.trashfiles.model

import java.io.File

/**
 * Lớp đại diện cho một mục có thể khôi phục (video, hình ảnh, v.v.)
 * @param file File gốc
 * @param size Kích thước file (bytes)
 * @param type Loại file ("image", "video", v.v.)
 * @param isDeleted Đánh dấu liệu file này đã bị xóa (true) hay là file thông thường (false)
 * @param contentUri Đường dẫn content Uri cho file trong MediaStore (dùng cho khôi phục)
 */
class RecoverableItem(
    private val file: File,
    private val size: Long,
    private val type: String,
    private var isDeleted: Boolean = false,
    private val contentUri: String? = null
) {
    
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
     * Trả về đường dẫn content Uri (nếu có)
     */
    fun getContentUri(): String? = contentUri
    
    /**
     * Kiểm tra xem file này đã bị xóa hay chưa
     */
    fun isDeleted(): Boolean = isDeleted
    
    /**
     * Đặt trạng thái xóa cho mục này
     */
    fun setDeleted(isDeleted: Boolean) {
        this.isDeleted = isDeleted
    }
    
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