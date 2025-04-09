# Cấu trúc mã nguồn ứng dụng

## Activities

### MainActivity
- Chức năng chính: Màn hình chính hiển thị các tùy chọn khôi phục và thống kê
- Các phương thức:
  - onCreate: Khởi tạo giao diện và các sự kiện click
  - setupCards: Thiết lập các thẻ tùy chọn khôi phục
  - updateStatistics: Cập nhật số liệu thống kê (files, kích thước, v.v.)
  - formatFileSize: Định dạng kích thước file để hiển thị

### PhotoRecoveryActivity
- Chức năng chính: Quét và khôi phục ảnh đã xóa
- Các phương thức:
  - onCreate: Khởi tạo giao diện và các sự kiện
  - scanForDeletedPhotos: Quét thiết bị tìm ảnh đã xóa
  - simulateFindingDeletedPhotos: Mô phỏng tìm ảnh đã xóa
  - recoverSelectedPhotos: Khôi phục các ảnh đã chọn

### VideoRecoveryActivity
- Chức năng chính: Quét và khôi phục video đã xóa
- Các phương thức:
  - onCreate: Khởi tạo giao diện và các sự kiện
  - scanForDeletedVideos: Quét thiết bị tìm video đã xóa
  - simulateFindingDeletedVideos: Mô phỏng tìm video đã xóa
  - recoverSelectedVideos: Khôi phục các video đã chọn

### FileRecoveryActivity
- Chức năng chính: Quét và khôi phục các tệp tin khác đã xóa
- Các phương thức:
  - onCreate: Khởi tạo giao diện và các sự kiện
  - scanForDeletedFiles: Quét thiết bị tìm tệp tin đã xóa
  - simulateFindingDeletedFiles: Mô phỏng tìm tệp tin đã xóa
  - recoverSelectedFiles: Khôi phục các tệp tin đã chọn

## Adapters

### PhotoAdapter
- Chức năng chính: Hiển thị danh sách ảnh có thể khôi phục
- Các phương thức:
  - onCreateViewHolder: Tạo ViewHolder từ layout
  - onBindViewHolder: Gắn dữ liệu ảnh vào ViewHolder
  - checkIfAnySelected: Kiểm tra xem có ảnh nào được chọn không

### VideoAdapter
- Chức năng chính: Hiển thị danh sách video có thể khôi phục
- Các phương thức:
  - onCreateViewHolder: Tạo ViewHolder từ layout
  - onBindViewHolder: Gắn dữ liệu video vào ViewHolder
  - formatFileSize: Định dạng kích thước file để hiển thị
  - checkIfAnySelected: Kiểm tra xem có video nào được chọn không

### FileAdapter
- Chức năng chính: Hiển thị danh sách tệp tin có thể khôi phục
- Các phương thức:
  - onCreateViewHolder: Tạo ViewHolder từ layout
  - onBindViewHolder: Gắn dữ liệu tệp tin vào ViewHolder
  - checkIfAnySelected: Kiểm tra xem có tệp tin nào được chọn không

## Models

### RecoverableItem
- Chức năng chính: Đại diện cho một mục có thể khôi phục (ảnh, video, tệp tin)
- Thuộc tính:
  - file: Đối tượng File đại diện cho mục
  - size: Kích thước của mục (bytes)
  - type: Loại mục (ảnh, video, tệp tin)
  - selected: Trạng thái đã chọn hay chưa
- Các phương thức:
  - getFile: Lấy đối tượng File
  - getSize: Lấy kích thước
  - getType: Lấy loại
  - isSelected: Kiểm tra đã chọn chưa
  - setSelected: Đặt trạng thái đã chọn
  - toggleSelection: Đảo ngược trạng thái đã chọn
  - getFormattedSize: Lấy kích thước đã định dạng

## Layouts

### activity_main.xml
- Layout chính hiển thị các tùy chọn khôi phục và thống kê

### activity_photo_recovery.xml
- Layout cho màn hình khôi phục ảnh

### activity_video_recovery.xml
- Layout cho màn hình khôi phục video

### activity_file_recovery.xml
- Layout cho màn hình khôi phục tệp tin

### item_photo_recovery.xml
- Layout cho một mục ảnh trong danh sách

### item_recoverable_video.xml
- Layout cho một mục video trong danh sách

### item_file_recovery.xml
- Layout cho một mục tệp tin trong danh sách 