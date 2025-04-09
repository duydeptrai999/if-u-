package com.htnguyen.ifu.model

import java.util.Date

/**
 * Lớp dữ liệu đại diện cho một tệp tin đã được tìm thấy có thể khôi phục
 *
 * @property path Đường dẫn đến tệp tin
 * @property name Tên tệp tin
 * @property size Kích thước tệp tính bằng byte
 * @property modifiedDate Ngày tệp tin được chỉnh sửa lần cuối
 * @property isSelected Trạng thái chọn tệp tin trong danh sách
 */
data class RecoveredFile(
    val path: String,
    val name: String,
    val size: Long,
    val modifiedDate: Date,
    var isSelected: Boolean
) 