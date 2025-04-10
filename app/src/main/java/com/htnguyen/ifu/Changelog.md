# Changelog

## [Unreleased]

### Added
- Tính năng khôi phục ảnh (PhotoRecoveryActivity)
  - Giao diện để quét và hiển thị các ảnh có thể khôi phục
  - Chức năng chọn và khôi phục ảnh đã xóa
  - Adapter hiển thị danh sách ảnh với thumbnail

- Tính năng khôi phục video (VideoRecoveryActivity)
  - Giao diện để quét và hiển thị các video có thể khôi phục
  - Chức năng chọn và khôi phục video đã xóa
  - Adapter hiển thị danh sách video với thumbnail

- Tính năng khôi phục tệp tin (FileRecoveryActivity)
  - Giao diện để quét và hiển thị các tệp tin có thể khôi phục
  - Chức năng chọn và khôi phục tệp tin đã xóa
  - Adapter hiển thị danh sách tệp tin với icon tương ứng

- Mô hình dữ liệu
  - Lớp RecoverableItem để quản lý các mục có thể khôi phục
  - Phương thức định dạng kích thước file để hiển thị

- Tài nguyên đồ họa
  - Icon placeholder cho ảnh và video
  - Icon cho các loại tệp tin
  - Biểu tượng cho giao diện người dùng

- Tính năng khôi phục ảnh đã xóa thực sự:
  - Quét thùng rác trên Android 12+ (MediaStore.Trash)
  - Tìm kiếm trong các thư mục ẩn tiềm năng (.trash, .deleted,...)
  - Mô phỏng khôi phục dữ liệu cấp thấp (để demo)
  - Hiển thị chỉ báo đánh dấu các ảnh đã bị xóa
  - Cập nhật RecoverableItem để hỗ trợ trạng thái đã xóa

### Changed
- Cập nhật giao diện chính để dẫn đến các màn hình khôi phục
- Cải thiện giao diện hiển thị các mục có thể khôi phục

### Fixed
- Sửa lỗi định dạng kích thước file
- Cập nhật model RecoverableItem để phù hợp với phiên bản Java
- Sửa lỗi hiển thị ảnh chưa xóa trong kết quả quét khôi phục:
  - Thêm đánh dấu rõ ràng cho ảnh đã xóa/chưa xóa
  - Thêm kiểm tra trùng lặp file
  - Tăng cường log để debug dễ dàng
  - Bổ sung kiểm tra cứng cuối cùng để đảm bảo chỉ hiển thị ảnh đã xóa

### Cải tiến
- Lọc kết quả quét để chỉ hiển thị các ảnh đã xóa thực sự
- Xóa giới hạn số lượng tìm kiếm (20 ảnh/video/file) trong tính năng khôi phục, cho phép quét và hiển thị tất cả các file tìm thấy.

## [0.1.1] - 2023-07-02
### Cập nhật
- **Yêu cầu**: Cập nhật giao diện màn hình Home theo mẫu.
- **Thực hiện**: 
  - Thay đổi gradient header từ tím sang xanh-lục
  - Điều chỉnh các card layout cho phù hợp với thiết kế mới
  - Thay đổi font size và padding cho các thành phần
  - Cập nhật hiển thị thống kê từ "B" sang "bytes"
  - Loại bỏ các khoảng cách và thuộc tính không cần thiết

## [1.1.0] - yyyy-mm-dd
### Added
- Thêm dialog yêu cầu quyền truy cập tất cả file khi mở ứng dụng
- Thêm quy trình xử lý quyền đầy đủ cho Android 11+ (MANAGE_EXTERNAL_STORAGE) và Android 10 trở xuống

## [1.2.3] - 2023-08-15
### Cải thiện
- Nâng cao chức năng khôi phục ảnh đã xóa để chỉ hiển thị những ảnh thực sự đã bị xóa
- Thêm chức năng quét thùng rác hệ thống trên Android 12+ (MediaStore.Trash)
- Thêm chức năng quét thùng rác của các ứng dụng thư viện ảnh phổ biến
- Cải thiện thuật toán quét các thư mục ẩn để tìm kiếm ảnh đã xóa
- Cập nhật lớp RecoverableItem để hỗ trợ lưu trữ contentUri cho khôi phục
- Nâng cao việc khôi phục ảnh từ thùng rác hệ thống trên Android 12+
- Cập nhật tài liệu hướng dẫn Help.md để phản ánh các tính năng mới

### Sửa lỗi
- Sửa lỗi hiển thị tất cả ảnh thay vì chỉ ảnh đã xóa trong kết quả quét
- Sửa lỗi không lọc đúng ảnh đã xóa trong quá trình quét
- Cải thiện khả năng phát hiện ảnh đã xóa trong các thư mục ẩn và thùng rác