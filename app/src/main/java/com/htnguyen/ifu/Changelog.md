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

### Changed
- Cập nhật giao diện chính để dẫn đến các màn hình khôi phục
- Cải thiện giao diện hiển thị các mục có thể khôi phục

### Fixed
- Sửa lỗi định dạng kích thước file
- Cập nhật model RecoverableItem để phù hợp với phiên bản Java