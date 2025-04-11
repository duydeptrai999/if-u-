# Hướng dẫn phát triển

## Tổng quan

Tài liệu này chứa hướng dẫn chi tiết để phát triển [tên dự án]. Hãy đọc [Project.md](Project.md) trước để hiểu tổng quan về dự án.

## Cài đặt môi trường phát triển

1. Cài đặt các công cụ cần thiết:

   - [Công cụ 1]
   - [Công cụ 2]
   - [Công cụ 3]

2. Thiết lập cấu hình:

   - [Bước 1]
   - [Bước 2]
   - [Bước 3]

3. Khởi động ứng dụng:
   - [Lệnh chạy ứng dụng]

## Các tính năng cần triển khai

### 1. Giao diện màn hình chính Easy Recovery ✅

**Mô tả**: Tạo giao diện màn hình chính ứng dụng khôi phục dữ liệu với các tính năng khôi phục ảnh, video và các tệp tin khác.

**Thành phần**:

- Màn hình chính với 3 lựa chọn khôi phục
- Phần hiển thị thống kê dữ liệu đã khôi phục
- Biểu đồ tiến trình hiển thị tỷ lệ loại tệp tin

**Yêu cầu chức năng**:

- Hiển thị các tùy chọn khôi phục ảnh, video và các tệp tin khác
- Hiển thị thống kê số lượng và kích thước tệp tin đã khôi phục
- Cho phép chuyển đến các màn hình chức năng khi người dùng chọn

**Ràng buộc**:

- Giao diện phải hỗ trợ cả tiếng Anh và tiếng Việt
- Giao diện phải phản ánh đúng bản thiết kế từ ảnh tham khảo

**Tham chiếu**:

- Xem ảnh giao diện được cung cấp

**Tiêu chí hoàn thành**:

- Giao diện hiển thị đúng như thiết kế
- Hỗ trợ đa ngôn ngữ (Anh, Việt)
- Có các placeholder cho việc hiển thị thống kê

### 2. Tính năng chọn ngôn ngữ khi khởi động lần đầu ✅

**Mô tả**: Tạo màn hình chọn ngôn ngữ khi người dùng mở ứng dụng lần đầu tiên.

**Thành phần**:

- Màn hình chọn ngôn ngữ với 2 lựa chọn: tiếng Anh và tiếng Việt
- Hệ thống quản lý ngôn ngữ xuyên suốt ứng dụng

**Yêu cầu chức năng**:

- Hiển thị màn hình chọn ngôn ngữ khi ứng dụng khởi động lần đầu
- Lưu lựa chọn ngôn ngữ của người dùng
- Áp dụng ngôn ngữ đã chọn cho toàn bộ ứng dụng
- Cho phép thay đổi ngôn ngữ sau này trong phần Cài đặt

**Ràng buộc**:

- Hỗ trợ đầy đủ cả tiếng Anh và tiếng Việt
- Áp dụng ngôn ngữ nhất quán trên toàn bộ ứng dụng
- Lưu trữ cài đặt ngôn ngữ giữa các lần khởi động

**Tham chiếu**:

- Tham khảo cấu trúc strings.xml và values-vi/strings.xml

**Tiêu chí hoàn thành**:

- Màn hình chọn ngôn ngữ hoạt động đúng
- Ngôn ngữ được áp dụng nhất quán trên toàn bộ ứng dụng
- Người dùng có thể thay đổi ngôn ngữ trong phần Cài đặt

### 3. Tính năng Intro App ⏳

**Mô tả**: Tạo màn hình giới thiệu (intro) ứng dụng sau khi người dùng chọn ngôn ngữ và trước khi vào màn hình chính.

**Thành phần**:

- Màn hình giới thiệu với 4 slides trình bày về các tính năng chính
- Hệ thống chuyển trang với hiệu ứng vuốt
- Chỉ báo vị trí trang hiện tại
- Nút bỏ qua và nút tiếp tục

**Yêu cầu chức năng**:

- Hiển thị 4 slides giới thiệu tính năng chính của ứng dụng
- Cho phép người dùng vuốt để chuyển giữa các slides
- Hiển thị chỉ báo vị trí trang hiện tại
- Cung cấp nút bỏ qua để đi thẳng đến màn hình chính
- Lưu trạng thái đã xem intro để không hiển thị lại

**Ràng buộc**:

- Hỗ trợ đa ngôn ngữ (tiếng Anh, tiếng Việt)
- Giao diện đẹp mắt và chuyên nghiệp
- Chuyển động mượt mà khi chuyển giữa các slides
- Tối ưu trải nghiệm người dùng

**Tham chiếu**:

- Các nội dung giới thiệu trong strings.xml
- Thiết kế hiện đại và trực quan

**Tiêu chí hoàn thành**:

- Hiển thị đúng nội dung 4 slides giới thiệu
- Chuyển động mượt mà giữa các slides
- Lưu trạng thái đã xem intro thành công
- Hỗ trợ đầy đủ đa ngôn ngữ

### 4. Tính năng D ❌

**Mô tả**: [Mô tả ngắn gọn về tính năng D]

**Thành phần**:

- Component 1
- Component 2

**Yêu cầu chức năng**:

- Yêu cầu 1
- Yêu cầu 2

**Ràng buộc**:

- Ràng buộc 1
- Ràng buộc 2

**Tham chiếu**:

- [Liên kết đến tài liệu liên quan]

**Tiêu chí hoàn thành**:

- Tiêu chí 1
- Tiêu chí 2

## Quy trình làm việc

1. Chọn một tính năng chưa được triển khai (đánh dấu ❌)
2. Đánh dấu tính năng đang triển khai (⏳)
3. Triển khai theo các yêu cầu chức năng
4. Kiểm tra theo tiêu chí hoàn thành
5. Đánh dấu tính năng đã hoàn thành (✅)
6. Cập nhật Changelog.md với các thay đổi
7. Cập nhật Codebase.md với mô tả về các thành phần mới

## Legend

- ✅ Completed
- ⏳ In Progress
- ❌ Not Started
