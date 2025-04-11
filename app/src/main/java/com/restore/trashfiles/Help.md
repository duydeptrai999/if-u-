# Hướng dẫn sử dụng tính năng khôi phục dữ liệu

## Tính năng khôi phục hình ảnh

### Mô tả
Tính năng khôi phục hình ảnh cho phép bạn tìm và khôi phục các ảnh đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục ảnh"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các ảnh đã xóa
3. Sau khi quét xong, chọn các ảnh bạn muốn khôi phục bằng cách nhấn vào ảnh
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các ảnh đã khôi phục sẽ được lưu trong thư mục "RecoveredPhotos" của ứng dụng

## Tính năng khôi phục video

### Mô tả
Tính năng khôi phục video cho phép bạn tìm và khôi phục các video đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục video"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các video đã xóa
3. Sau khi quét xong, chọn các video bạn muốn khôi phục bằng cách nhấn vào video
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các video đã khôi phục sẽ được lưu trong thư mục "RecoveredVideos" của ứng dụng

## Tính năng khôi phục tệp tin

### Mô tả
Tính năng khôi phục tệp tin cho phép bạn tìm và khôi phục các tệp tin khác (PDF, DOC, XLS, v.v.) đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục các tệp khác"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các tệp tin đã xóa
3. Sau khi quét xong, chọn các tệp tin bạn muốn khôi phục bằng cách nhấn vào tệp tin
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các tệp tin đã khôi phục sẽ được lưu trong thư mục "RecoveredFiles" của ứng dụng

## Lưu ý quan trọng
- Ứng dụng cần quyền truy cập bộ nhớ để thực hiện chức năng khôi phục
- Tỷ lệ khôi phục thành công phụ thuộc vào nhiều yếu tố như thời gian xóa, mức độ sử dụng thiết bị sau khi xóa
- Để tăng khả năng khôi phục, hãy sử dụng tính năng này ngay sau khi dữ liệu bị xóa
- Không lưu thêm dữ liệu mới vào thiết bị sau khi xóa dữ liệu để tránh việc ghi đè lên dữ liệu đã xóa

## Quyền truy cập tất cả file

Khi bạn mở ứng dụng lần đầu tiên, sẽ xuất hiện một dialog yêu cầu quyền truy cập tất cả file. Đây là quyền cần thiết để ứng dụng có thể quét và khôi phục các tệp đã bị xóa trong bộ nhớ thiết bị.

### Cách cấp quyền:

1. Khi dialog xuất hiện, chọn "Cấp quyền"
2. Bạn sẽ được chuyển đến màn hình cài đặt của hệ thống
3. Bật công tắc "Cho phép quản lý tất cả tệp"
4. Quay lại ứng dụng

Nếu bạn không cấp quyền, ứng dụng sẽ không thể thực hiện chức năng khôi phục dữ liệu đã xóa.

## Khôi phục ảnh đã xóa thực sự

Tính năng khôi phục ảnh đã xóa giúp bạn tìm và khôi phục các ảnh đã thực sự bị xóa trên thiết bị.

### Cách hoạt động

Khi bạn nhấn nút "Quét", ứng dụng sẽ thực hiện các bước sau:

1. **Quét thùng rác hệ thống** - Trên Android 12 trở lên, ứng dụng sẽ tìm kiếm các ảnh trong thùng rác của hệ thống (MediaStore.Trash). Đây là nơi các ảnh bị xóa gần đây sẽ được lưu trữ trước khi bị xóa vĩnh viễn.

2. **Quét thùng rác của các ứng dụng Thư viện Ảnh** - Ứng dụng sẽ tìm kiếm trong các thư mục thùng rác của ứng dụng thư viện ảnh phổ biến như Google Photos, Samsung Gallery, MIUI Gallery, v.v.

3. **Tìm kiếm trong các thư mục ẩn** - Ứng dụng sẽ tìm kiếm các ảnh trong các thư mục ẩn có thể chứa dữ liệu đã xóa như ".trash", ".Trash", "Recently Deleted", v.v.

4. **Quét dữ liệu cấp thấp** - Nếu không tìm thấy ảnh đã xóa trong các bước trên, ứng dụng sẽ tìm kiếm các dữ liệu còn sót lại của ảnh đã xóa trong bộ nhớ.

### Cách sử dụng

1. **Quét thiết bị**: 
   - Từ màn hình chính, nhấn vào phần "Khôi phục ảnh"
   - Nhấn nút "Bắt đầu quét" để tìm kiếm các ảnh đã xóa
   - Trong quá trình quét, màn hình hiển thị tiến trình và thông báo trạng thái

2. **Xem kết quả quét**:
   - Sau khi quét xong, màn hình hiển thị thông tin về số lượng ảnh đã xóa tìm thấy
   - Tất cả ảnh được hiển thị đều là ảnh đã bị xóa, được đánh dấu bằng nhãn "ĐÃ XÓA" màu đỏ
   - Ứng dụng phân loại chính xác và chỉ hiển thị các ảnh đã bị xóa thực sự, bỏ qua ảnh thông thường
   - Nhấn "Xem kết quả" để xem danh sách đầy đủ

3. **Khôi phục ảnh**:
   - Chọn các ảnh muốn khôi phục bằng cách nhấn vào ảnh
   - Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
   - Đối với ảnh trong thùng rác của hệ thống (Android 12+), ứng dụng sẽ khôi phục trực tiếp từ thùng rác
   - Các ảnh khác sẽ được sao chép vào thư mục "RecoveredPhotos" của ứng dụng
   - Bạn có thể truy cập ảnh đã khôi phục từ ứng dụng Quản lý File của thiết bị

### Lưu ý quan trọng

- Khả năng tìm thấy ảnh đã xóa phụ thuộc vào:
  - Thời gian đã trôi qua kể từ khi xóa
  - Mức độ sử dụng thiết bị sau khi xóa
  - Phiên bản Android của thiết bị (Android 12+ có chức năng thùng rác tốt hơn)
  
- Để có kết quả tốt nhất:
  - Sử dụng tính năng này ngay sau khi vô tình xóa ảnh
  - Giảm thiểu việc lưu dữ liệu mới sau khi xóa ảnh
  - Tắt WiFi/4G để tránh các ứng dụng tự động tải xuống dữ liệu mới
  - Nếu sử dụng Android 12+, ảnh bị xóa sẽ có khả năng khôi phục cao hơn

## Tính năng khôi phục video

### Mô tả
Tính năng khôi phục video cho phép bạn tìm và khôi phục các video đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục video"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các video đã xóa
3. Sau khi quét xong, chọn các video bạn muốn khôi phục bằng cách nhấn vào video
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các video đã khôi phục sẽ được lưu trong thư mục "RecoveredVideos" của ứng dụng

## Tính năng khôi phục tệp tin

### Mô tả
Tính năng khôi phục tệp tin cho phép bạn tìm và khôi phục các tệp tin khác (PDF, DOC, XLS, v.v.) đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục các tệp khác"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các tệp tin đã xóa
3. Sau khi quét xong, chọn các tệp tin bạn muốn khôi phục bằng cách nhấn vào tệp tin
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các tệp tin đã khôi phục sẽ được lưu trong thư mục "RecoveredFiles" của ứng dụng

## Lưu ý quan trọng
- Ứng dụng cần quyền truy cập bộ nhớ để thực hiện chức năng khôi phục
- Tỷ lệ khôi phục thành công phụ thuộc vào nhiều yếu tố như thời gian xóa, mức độ sử dụng thiết bị sau khi xóa
- Để tăng khả năng khôi phục, hãy sử dụng tính năng này ngay sau khi dữ liệu bị xóa
- Không lưu thêm dữ liệu mới vào thiết bị sau khi xóa dữ liệu để tránh việc ghi đè lên dữ liệu đã xóa

## Quyền truy cập tất cả file

Khi bạn mở ứng dụng lần đầu tiên, sẽ xuất hiện một dialog yêu cầu quyền truy cập tất cả file. Đây là quyền cần thiết để ứng dụng có thể quét và khôi phục các tệp đã bị xóa trong bộ nhớ thiết bị.

### Cách cấp quyền:

1. Khi dialog xuất hiện, chọn "Cấp quyền"
2. Bạn sẽ được chuyển đến màn hình cài đặt của hệ thống
3. Bật công tắc "Cho phép quản lý tất cả tệp"
4. Quay lại ứng dụng

Nếu bạn không cấp quyền, ứng dụng sẽ không thể thực hiện chức năng khôi phục dữ liệu đã xóa.

## Khôi phục ảnh đã xóa thực sự

Tính năng khôi phục ảnh đã xóa giúp bạn tìm và khôi phục các ảnh đã thực sự bị xóa trên thiết bị.

### Cách hoạt động

Khi bạn nhấn nút "Quét", ứng dụng sẽ thực hiện các bước sau:

1. **Quét thùng rác hệ thống** - Trên Android 12 trở lên, ứng dụng sẽ tìm kiếm các ảnh trong thùng rác của hệ thống (MediaStore.Trash). Đây là nơi các ảnh bị xóa gần đây sẽ được lưu trữ trước khi bị xóa vĩnh viễn.

2. **Quét thùng rác của các ứng dụng Thư viện Ảnh** - Ứng dụng sẽ tìm kiếm trong các thư mục thùng rác của ứng dụng thư viện ảnh phổ biến như Google Photos, Samsung Gallery, MIUI Gallery, v.v.

3. **Tìm kiếm trong các thư mục ẩn** - Ứng dụng sẽ tìm kiếm các ảnh trong các thư mục ẩn có thể chứa dữ liệu đã xóa như ".trash", ".Trash", "Recently Deleted", v.v.

4. **Quét dữ liệu cấp thấp** - Nếu không tìm thấy ảnh đã xóa trong các bước trên, ứng dụng sẽ tìm kiếm các dữ liệu còn sót lại của ảnh đã xóa trong bộ nhớ.

### Cách sử dụng

1. **Quét thiết bị**: 
   - Từ màn hình chính, nhấn vào phần "Khôi phục ảnh"
   - Nhấn nút "Bắt đầu quét" để tìm kiếm các ảnh đã xóa
   - Trong quá trình quét, màn hình hiển thị tiến trình và thông báo trạng thái

2. **Xem kết quả quét**:
   - Sau khi quét xong, màn hình hiển thị thông tin về số lượng ảnh đã xóa tìm thấy
   - Tất cả ảnh được hiển thị đều là ảnh đã bị xóa, được đánh dấu bằng nhãn "ĐÃ XÓA" màu đỏ
   - Ứng dụng phân loại chính xác và chỉ hiển thị các ảnh đã bị xóa thực sự, bỏ qua ảnh thông thường
   - Nhấn "Xem kết quả" để xem danh sách đầy đủ

3. **Khôi phục ảnh**:
   - Chọn các ảnh muốn khôi phục bằng cách nhấn vào ảnh
   - Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
   - Đối với ảnh trong thùng rác của hệ thống (Android 12+), ứng dụng sẽ khôi phục trực tiếp từ thùng rác
   - Các ảnh khác sẽ được sao chép vào thư mục "RecoveredPhotos" của ứng dụng
   - Bạn có thể truy cập ảnh đã khôi phục từ ứng dụng Quản lý File của thiết bị

### Lưu ý quan trọng

- Khả năng tìm thấy ảnh đã xóa phụ thuộc vào:
  - Thời gian đã trôi qua kể từ khi xóa
  - Mức độ sử dụng thiết bị sau khi xóa
  - Phiên bản Android của thiết bị (Android 12+ có chức năng thùng rác tốt hơn)
  
- Để có kết quả tốt nhất:
  - Sử dụng tính năng này ngay sau khi vô tình xóa ảnh
  - Giảm thiểu việc lưu dữ liệu mới sau khi xóa ảnh
  - Tắt WiFi/4G để tránh các ứng dụng tự động tải xuống dữ liệu mới
  - Nếu sử dụng Android 12+, ảnh bị xóa sẽ có khả năng khôi phục cao hơn

## Tính năng khôi phục video

### Mô tả
Tính năng khôi phục video cho phép bạn tìm và khôi phục các video đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục video"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các video đã xóa
3. Sau khi quét xong, chọn các video bạn muốn khôi phục bằng cách nhấn vào video
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các video đã khôi phục sẽ được lưu trong thư mục "RecoveredVideos" của ứng dụng

## Tính năng khôi phục tệp tin

### Mô tả
Tính năng khôi phục tệp tin cho phép bạn tìm và khôi phục các tệp tin khác (PDF, DOC, XLS, v.v.) đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục các tệp khác"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các tệp tin đã xóa
3. Sau khi quét xong, chọn các tệp tin bạn muốn khôi phục bằng cách nhấn vào tệp tin
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các tệp tin đã khôi phục sẽ được lưu trong thư mục "RecoveredFiles" của ứng dụng

## Lưu ý quan trọng
- Ứng dụng cần quyền truy cập bộ nhớ để thực hiện chức năng khôi phục
- Tỷ lệ khôi phục thành công phụ thuộc vào nhiều yếu tố như thời gian xóa, mức độ sử dụng thiết bị sau khi xóa
- Để tăng khả năng khôi phục, hãy sử dụng tính năng này ngay sau khi dữ liệu bị xóa
- Không lưu thêm dữ liệu mới vào thiết bị sau khi xóa dữ liệu để tránh việc ghi đè lên dữ liệu đã xóa

## Quyền truy cập tất cả file

Khi bạn mở ứng dụng lần đầu tiên, sẽ xuất hiện một dialog yêu cầu quyền truy cập tất cả file. Đây là quyền cần thiết để ứng dụng có thể quét và khôi phục các tệp đã bị xóa trong bộ nhớ thiết bị.

### Cách cấp quyền:

1. Khi dialog xuất hiện, chọn "Cấp quyền"
2. Bạn sẽ được chuyển đến màn hình cài đặt của hệ thống
3. Bật công tắc "Cho phép quản lý tất cả tệp"
4. Quay lại ứng dụng

Nếu bạn không cấp quyền, ứng dụng sẽ không thể thực hiện chức năng khôi phục dữ liệu đã xóa.

## Khôi phục ảnh đã xóa thực sự

Tính năng khôi phục ảnh đã xóa giúp bạn tìm và khôi phục các ảnh đã thực sự bị xóa trên thiết bị.

### Cách hoạt động

Khi bạn nhấn nút "Quét", ứng dụng sẽ thực hiện các bước sau:

1. **Quét thùng rác hệ thống** - Trên Android 12 trở lên, ứng dụng sẽ tìm kiếm các ảnh trong thùng rác của hệ thống (MediaStore.Trash). Đây là nơi các ảnh bị xóa gần đây sẽ được lưu trữ trước khi bị xóa vĩnh viễn.

2. **Quét thùng rác của các ứng dụng Thư viện Ảnh** - Ứng dụng sẽ tìm kiếm trong các thư mục thùng rác của ứng dụng thư viện ảnh phổ biến như Google Photos, Samsung Gallery, MIUI Gallery, v.v.

3. **Tìm kiếm trong các thư mục ẩn** - Ứng dụng sẽ tìm kiếm các ảnh trong các thư mục ẩn có thể chứa dữ liệu đã xóa như ".trash", ".Trash", "Recently Deleted", v.v.

4. **Quét dữ liệu cấp thấp** - Nếu không tìm thấy ảnh đã xóa trong các bước trên, ứng dụng sẽ tìm kiếm các dữ liệu còn sót lại của ảnh đã xóa trong bộ nhớ.

### Cách sử dụng

1. **Quét thiết bị**: 
   - Từ màn hình chính, nhấn vào phần "Khôi phục ảnh"
   - Nhấn nút "Bắt đầu quét" để tìm kiếm các ảnh đã xóa
   - Trong quá trình quét, màn hình hiển thị tiến trình và thông báo trạng thái

2. **Xem kết quả quét**:
   - Sau khi quét xong, màn hình hiển thị thông tin về số lượng ảnh đã xóa tìm thấy
   - Tất cả ảnh được hiển thị đều là ảnh đã bị xóa, được đánh dấu bằng nhãn "ĐÃ XÓA" màu đỏ
   - Ứng dụng phân loại chính xác và chỉ hiển thị các ảnh đã bị xóa thực sự, bỏ qua ảnh thông thường
   - Nhấn "Xem kết quả" để xem danh sách đầy đủ

3. **Khôi phục ảnh**:
   - Chọn các ảnh muốn khôi phục bằng cách nhấn vào ảnh
   - Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
   - Đối với ảnh trong thùng rác của hệ thống (Android 12+), ứng dụng sẽ khôi phục trực tiếp từ thùng rác
   - Các ảnh khác sẽ được sao chép vào thư mục "RecoveredPhotos" của ứng dụng
   - Bạn có thể truy cập ảnh đã khôi phục từ ứng dụng Quản lý File của thiết bị

### Lưu ý quan trọng

- Khả năng tìm thấy ảnh đã xóa phụ thuộc vào:
  - Thời gian đã trôi qua kể từ khi xóa
  - Mức độ sử dụng thiết bị sau khi xóa
  - Phiên bản Android của thiết bị (Android 12+ có chức năng thùng rác tốt hơn)
  
- Để có kết quả tốt nhất:
  - Sử dụng tính năng này ngay sau khi vô tình xóa ảnh
  - Giảm thiểu việc lưu dữ liệu mới sau khi xóa ảnh
  - Tắt WiFi/4G để tránh các ứng dụng tự động tải xuống dữ liệu mới
  - Nếu sử dụng Android 12+, ảnh bị xóa sẽ có khả năng khôi phục cao hơn

## Tính năng khôi phục video

### Mô tả
Tính năng khôi phục video cho phép bạn tìm và khôi phục các video đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục video"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các video đã xóa
3. Sau khi quét xong, chọn các video bạn muốn khôi phục bằng cách nhấn vào video
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các video đã khôi phục sẽ được lưu trong thư mục "RecoveredVideos" của ứng dụng

## Tính năng khôi phục tệp tin

### Mô tả
Tính năng khôi phục tệp tin cho phép bạn tìm và khôi phục các tệp tin khác (PDF, DOC, XLS, v.v.) đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục các tệp khác"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các tệp tin đã xóa
3. Sau khi quét xong, chọn các tệp tin bạn muốn khôi phục bằng cách nhấn vào tệp tin
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các tệp tin đã khôi phục sẽ được lưu trong thư mục "RecoveredFiles" của ứng dụng

## Lưu ý quan trọng
- Ứng dụng cần quyền truy cập bộ nhớ để thực hiện chức năng khôi phục
- Tỷ lệ khôi phục thành công phụ thuộc vào nhiều yếu tố như thời gian xóa, mức độ sử dụng thiết bị sau khi xóa
- Để tăng khả năng khôi phục, hãy sử dụng tính năng này ngay sau khi dữ liệu bị xóa
- Không lưu thêm dữ liệu mới vào thiết bị sau khi xóa dữ liệu để tránh việc ghi đè lên dữ liệu đã xóa

## Quyền truy cập tất cả file

Khi bạn mở ứng dụng lần đầu tiên, sẽ xuất hiện một dialog yêu cầu quyền truy cập tất cả file. Đây là quyền cần thiết để ứng dụng có thể quét và khôi phục các tệp đã bị xóa trong bộ nhớ thiết bị.

### Cách cấp quyền:

1. Khi dialog xuất hiện, chọn "Cấp quyền"
2. Bạn sẽ được chuyển đến màn hình cài đặt của hệ thống
3. Bật công tắc "Cho phép quản lý tất cả tệp"
4. Quay lại ứng dụng

Nếu bạn không cấp quyền, ứng dụng sẽ không thể thực hiện chức năng khôi phục dữ liệu đã xóa.

## Khôi phục ảnh đã xóa thực sự

Tính năng khôi phục ảnh đã xóa giúp bạn tìm và khôi phục các ảnh đã thực sự bị xóa trên thiết bị.

### Cách hoạt động

Khi bạn nhấn nút "Quét", ứng dụng sẽ thực hiện các bước sau:

1. **Quét thùng rác hệ thống** - Trên Android 12 trở lên, ứng dụng sẽ tìm kiếm các ảnh trong thùng rác của hệ thống (MediaStore.Trash). Đây là nơi các ảnh bị xóa gần đây sẽ được lưu trữ trước khi bị xóa vĩnh viễn.

2. **Quét thùng rác của các ứng dụng Thư viện Ảnh** - Ứng dụng sẽ tìm kiếm trong các thư mục thùng rác của ứng dụng thư viện ảnh phổ biến như Google Photos, Samsung Gallery, MIUI Gallery, v.v.

3. **Tìm kiếm trong các thư mục ẩn** - Ứng dụng sẽ tìm kiếm các ảnh trong các thư mục ẩn có thể chứa dữ liệu đã xóa như ".trash", ".Trash", "Recently Deleted", v.v.

4. **Quét dữ liệu cấp thấp** - Nếu không tìm thấy ảnh đã xóa trong các bước trên, ứng dụng sẽ tìm kiếm các dữ liệu còn sót lại của ảnh đã xóa trong bộ nhớ.

### Cách sử dụng

1. **Quét thiết bị**: 
   - Từ màn hình chính, nhấn vào phần "Khôi phục ảnh"
   - Nhấn nút "Bắt đầu quét" để tìm kiếm các ảnh đã xóa
   - Trong quá trình quét, màn hình hiển thị tiến trình và thông báo trạng thái

2. **Xem kết quả quét**:
   - Sau khi quét xong, màn hình hiển thị thông tin về số lượng ảnh đã xóa tìm thấy
   - Tất cả ảnh được hiển thị đều là ảnh đã bị xóa, được đánh dấu bằng nhãn "ĐÃ XÓA" màu đỏ
   - Ứng dụng phân loại chính xác và chỉ hiển thị các ảnh đã bị xóa thực sự, bỏ qua ảnh thông thường
   - Nhấn "Xem kết quả" để xem danh sách đầy đủ

3. **Khôi phục ảnh**:
   - Chọn các ảnh muốn khôi phục bằng cách nhấn vào ảnh
   - Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
   - Đối với ảnh trong thùng rác của hệ thống (Android 12+), ứng dụng sẽ khôi phục trực tiếp từ thùng rác
   - Các ảnh khác sẽ được sao chép vào thư mục "RecoveredPhotos" của ứng dụng
   - Bạn có thể truy cập ảnh đã khôi phục từ ứng dụng Quản lý File của thiết bị

### Lưu ý quan trọng

- Khả năng tìm thấy ảnh đã xóa phụ thuộc vào:
  - Thời gian đã trôi qua kể từ khi xóa
  - Mức độ sử dụng thiết bị sau khi xóa
  - Phiên bản Android của thiết bị (Android 12+ có chức năng thùng rác tốt hơn)
  
- Để có kết quả tốt nhất:
  - Sử dụng tính năng này ngay sau khi vô tình xóa ảnh
  - Giảm thiểu việc lưu dữ liệu mới sau khi xóa ảnh
  - Tắt WiFi/4G để tránh các ứng dụng tự động tải xuống dữ liệu mới
  - Nếu sử dụng Android 12+, ảnh bị xóa sẽ có khả năng khôi phục cao hơn

## Tính năng khôi phục video

### Mô tả
Tính năng khôi phục video cho phép bạn tìm và khôi phục các video đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục video"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các video đã xóa
3. Sau khi quét xong, chọn các video bạn muốn khôi phục bằng cách nhấn vào video
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các video đã khôi phục sẽ được lưu trong thư mục "RecoveredVideos" của ứng dụng

## Tính năng khôi phục tệp tin

### Mô tả
Tính năng khôi phục tệp tin cho phép bạn tìm và khôi phục các tệp tin khác (PDF, DOC, XLS, v.v.) đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục các tệp khác"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các tệp tin đã xóa
3. Sau khi quét xong, chọn các tệp tin bạn muốn khôi phục bằng cách nhấn vào tệp tin
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các tệp tin đã khôi phục sẽ được lưu trong thư mục "RecoveredFiles" của ứng dụng

## Lưu ý quan trọng
- Ứng dụng cần quyền truy cập bộ nhớ để thực hiện chức năng khôi phục
- Tỷ lệ khôi phục thành công phụ thuộc vào nhiều yếu tố như thời gian xóa, mức độ sử dụng thiết bị sau khi xóa
- Để tăng khả năng khôi phục, hãy sử dụng tính năng này ngay sau khi dữ liệu bị xóa
- Không lưu thêm dữ liệu mới vào thiết bị sau khi xóa dữ liệu để tránh việc ghi đè lên dữ liệu đã xóa

## Quyền truy cập tất cả file

Khi bạn mở ứng dụng lần đầu tiên, sẽ xuất hiện một dialog yêu cầu quyền truy cập tất cả file. Đây là quyền cần thiết để ứng dụng có thể quét và khôi phục các tệp đã bị xóa trong bộ nhớ thiết bị.

### Cách cấp quyền:

1. Khi dialog xuất hiện, chọn "Cấp quyền"
2. Bạn sẽ được chuyển đến màn hình cài đặt của hệ thống
3. Bật công tắc "Cho phép quản lý tất cả tệp"
4. Quay lại ứng dụng

Nếu bạn không cấp quyền, ứng dụng sẽ không thể thực hiện chức năng khôi phục dữ liệu đã xóa.

## Khôi phục ảnh đã xóa thực sự

Tính năng khôi phục ảnh đã xóa giúp bạn tìm và khôi phục các ảnh đã thực sự bị xóa trên thiết bị.

### Cách hoạt động

Khi bạn nhấn nút "Quét", ứng dụng sẽ thực hiện các bước sau:

1. **Quét thùng rác hệ thống** - Trên Android 12 trở lên, ứng dụng sẽ tìm kiếm các ảnh trong thùng rác của hệ thống (MediaStore.Trash). Đây là nơi các ảnh bị xóa gần đây sẽ được lưu trữ trước khi bị xóa vĩnh viễn.

2. **Quét thùng rác của các ứng dụng Thư viện Ảnh** - Ứng dụng sẽ tìm kiếm trong các thư mục thùng rác của ứng dụng thư viện ảnh phổ biến như Google Photos, Samsung Gallery, MIUI Gallery, v.v.

3. **Tìm kiếm trong các thư mục ẩn** - Ứng dụng sẽ tìm kiếm các ảnh trong các thư mục ẩn có thể chứa dữ liệu đã xóa như ".trash", ".Trash", "Recently Deleted", v.v.

4. **Quét dữ liệu cấp thấp** - Nếu không tìm thấy ảnh đã xóa trong các bước trên, ứng dụng sẽ tìm kiếm các dữ liệu còn sót lại của ảnh đã xóa trong bộ nhớ.

### Cách sử dụng

1. **Quét thiết bị**: 
   - Từ màn hình chính, nhấn vào phần "Khôi phục ảnh"
   - Nhấn nút "Bắt đầu quét" để tìm kiếm các ảnh đã xóa
   - Trong quá trình quét, màn hình hiển thị tiến trình và thông báo trạng thái

2. **Xem kết quả quét**:
   - Sau khi quét xong, màn hình hiển thị thông tin về số lượng ảnh đã xóa tìm thấy
   - Tất cả ảnh được hiển thị đều là ảnh đã bị xóa, được đánh dấu bằng nhãn "ĐÃ XÓA" màu đỏ
   - Ứng dụng phân loại chính xác và chỉ hiển thị các ảnh đã bị xóa thực sự, bỏ qua ảnh thông thường
   - Nhấn "Xem kết quả" để xem danh sách đầy đủ

3. **Khôi phục ảnh**:
   - Chọn các ảnh muốn khôi phục bằng cách nhấn vào ảnh
   - Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
   - Đối với ảnh trong thùng rác của hệ thống (Android 12+), ứng dụng sẽ khôi phục trực tiếp từ thùng rác
   - Các ảnh khác sẽ được sao chép vào thư mục "RecoveredPhotos" của ứng dụng
   - Bạn có thể truy cập ảnh đã khôi phục từ ứng dụng Quản lý File của thiết bị

### Lưu ý quan trọng

- Khả năng tìm thấy ảnh đã xóa phụ thuộc vào:
  - Thời gian đã trôi qua kể từ khi xóa
  - Mức độ sử dụng thiết bị sau khi xóa
  - Phiên bản Android của thiết bị (Android 12+ có chức năng thùng rác tốt hơn)
  
- Để có kết quả tốt nhất:
  - Sử dụng tính năng này ngay sau khi vô tình xóa ảnh
  - Giảm thiểu việc lưu dữ liệu mới sau khi xóa ảnh
  - Tắt WiFi/4G để tránh các ứng dụng tự động tải xuống dữ liệu mới
  - Nếu sử dụng Android 12+, ảnh bị xóa sẽ có khả năng khôi phục cao hơn

## Tính năng khôi phục video

### Mô tả
Tính năng khôi phục video cho phép bạn tìm và khôi phục các video đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục video"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các video đã xóa
3. Sau khi quét xong, chọn các video bạn muốn khôi phục bằng cách nhấn vào video
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các video đã khôi phục sẽ được lưu trong thư mục "RecoveredVideos" của ứng dụng

## Tính năng khôi phục tệp tin

### Mô tả
Tính năng khôi phục tệp tin cho phép bạn tìm và khôi phục các tệp tin khác (PDF, DOC, XLS, v.v.) đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục các tệp khác"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các tệp tin đã xóa
3. Sau khi quét xong, chọn các tệp tin bạn muốn khôi phục bằng cách nhấn vào tệp tin
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các tệp tin đã khôi phục sẽ được lưu trong thư mục "RecoveredFiles" của ứng dụng

## Lưu ý quan trọng
- Ứng dụng cần quyền truy cập bộ nhớ để thực hiện chức năng khôi phục
- Tỷ lệ khôi phục thành công phụ thuộc vào nhiều yếu tố như thời gian xóa, mức độ sử dụng thiết bị sau khi xóa
- Để tăng khả năng khôi phục, hãy sử dụng tính năng này ngay sau khi dữ liệu bị xóa
- Không lưu thêm dữ liệu mới vào thiết bị sau khi xóa dữ liệu để tránh việc ghi đè lên dữ liệu đã xóa

## Quyền truy cập tất cả file

Khi bạn mở ứng dụng lần đầu tiên, sẽ xuất hiện một dialog yêu cầu quyền truy cập tất cả file. Đây là quyền cần thiết để ứng dụng có thể quét và khôi phục các tệp đã bị xóa trong bộ nhớ thiết bị.

### Cách cấp quyền:

1. Khi dialog xuất hiện, chọn "Cấp quyền"
2. Bạn sẽ được chuyển đến màn hình cài đặt của hệ thống
3. Bật công tắc "Cho phép quản lý tất cả tệp"
4. Quay lại ứng dụng

Nếu bạn không cấp quyền, ứng dụng sẽ không thể thực hiện chức năng khôi phục dữ liệu đã xóa.

## Khôi phục ảnh đã xóa thực sự

Tính năng khôi phục ảnh đã xóa giúp bạn tìm và khôi phục các ảnh đã thực sự bị xóa trên thiết bị.

### Cách hoạt động

Khi bạn nhấn nút "Quét", ứng dụng sẽ thực hiện các bước sau:

1. **Quét thùng rác hệ thống** - Trên Android 12 trở lên, ứng dụng sẽ tìm kiếm các ảnh trong thùng rác của hệ thống (MediaStore.Trash). Đây là nơi các ảnh bị xóa gần đây sẽ được lưu trữ trước khi bị xóa vĩnh viễn.

2. **Quét thùng rác của các ứng dụng Thư viện Ảnh** - Ứng dụng sẽ tìm kiếm trong các thư mục thùng rác của ứng dụng thư viện ảnh phổ biến như Google Photos, Samsung Gallery, MIUI Gallery, v.v.

3. **Tìm kiếm trong các thư mục ẩn** - Ứng dụng sẽ tìm kiếm các ảnh trong các thư mục ẩn có thể chứa dữ liệu đã xóa như ".trash", ".Trash", "Recently Deleted", v.v.

4. **Quét dữ liệu cấp thấp** - Nếu không tìm thấy ảnh đã xóa trong các bước trên, ứng dụng sẽ tìm kiếm các dữ liệu còn sót lại của ảnh đã xóa trong bộ nhớ.

### Cách sử dụng

1. **Quét thiết bị**: 
   - Từ màn hình chính, nhấn vào phần "Khôi phục ảnh"
   - Nhấn nút "Bắt đầu quét" để tìm kiếm các ảnh đã xóa
   - Trong quá trình quét, màn hình hiển thị tiến trình và thông báo trạng thái

2. **Xem kết quả quét**:
   - Sau khi quét xong, màn hình hiển thị thông tin về số lượng ảnh đã xóa tìm thấy
   - Tất cả ảnh được hiển thị đều là ảnh đã bị xóa, được đánh dấu bằng nhãn "ĐÃ XÓA" màu đỏ
   - Ứng dụng phân loại chính xác và chỉ hiển thị các ảnh đã bị xóa thực sự, bỏ qua ảnh thông thường
   - Nhấn "Xem kết quả" để xem danh sách đầy đủ

3. **Khôi phục ảnh**:
   - Chọn các ảnh muốn khôi phục bằng cách nhấn vào ảnh
   - Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
   - Đối với ảnh trong thùng rác của hệ thống (Android 12+), ứng dụng sẽ khôi phục trực tiếp từ thùng rác
   - Các ảnh khác sẽ được sao chép vào thư mục "RecoveredPhotos" của ứng dụng
   - Bạn có thể truy cập ảnh đã khôi phục từ ứng dụng Quản lý File của thiết bị

### Lưu ý quan trọng

- Khả năng tìm thấy ảnh đã xóa phụ thuộc vào:
  - Thời gian đã trôi qua kể từ khi xóa
  - Mức độ sử dụng thiết bị sau khi xóa
  - Phiên bản Android của thiết bị (Android 12+ có chức năng thùng rác tốt hơn)
  
- Để có kết quả tốt nhất:
  - Sử dụng tính năng này ngay sau khi vô tình xóa ảnh
  - Giảm thiểu việc lưu dữ liệu mới sau khi xóa ảnh
  - Tắt WiFi/4G để tránh các ứng dụng tự động tải xuống dữ liệu mới
  - Nếu sử dụng Android 12+, ảnh bị xóa sẽ có khả năng khôi phục cao hơn

## Tính năng khôi phục video

### Mô tả
Tính năng khôi phục video cho phép bạn tìm và khôi phục các video đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục video"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các video đã xóa
3. Sau khi quét xong, chọn các video bạn muốn khôi phục bằng cách nhấn vào video
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các video đã khôi phục sẽ được lưu trong thư mục "RecoveredVideos" của ứng dụng

## Tính năng khôi phục tệp tin

### Mô tả
Tính năng khôi phục tệp tin cho phép bạn tìm và khôi phục các tệp tin khác (PDF, DOC, XLS, v.v.) đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục các tệp khác"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các tệp tin đã xóa
3. Sau khi quét xong, chọn các tệp tin bạn muốn khôi phục bằng cách nhấn vào tệp tin
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các tệp tin đã khôi phục sẽ được lưu trong thư mục "RecoveredFiles" của ứng dụng

## Lưu ý quan trọng
- Ứng dụng cần quyền truy cập bộ nhớ để thực hiện chức năng khôi phục
- Tỷ lệ khôi phục thành công phụ thuộc vào nhiều yếu tố như thời gian xóa, mức độ sử dụng thiết bị sau khi xóa
- Để tăng khả năng khôi phục, hãy sử dụng tính năng này ngay sau khi dữ liệu bị xóa
- Không lưu thêm dữ liệu mới vào thiết bị sau khi xóa dữ liệu để tránh việc ghi đè lên dữ liệu đã xóa

## Quyền truy cập tất cả file

Khi bạn mở ứng dụng lần đầu tiên, sẽ xuất hiện một dialog yêu cầu quyền truy cập tất cả file. Đây là quyền cần thiết để ứng dụng có thể quét và khôi phục các tệp đã bị xóa trong bộ nhớ thiết bị.

### Cách cấp quyền:

1. Khi dialog xuất hiện, chọn "Cấp quyền"
2. Bạn sẽ được chuyển đến màn hình cài đặt của hệ thống
3. Bật công tắc "Cho phép quản lý tất cả tệp"
4. Quay lại ứng dụng

Nếu bạn không cấp quyền, ứng dụng sẽ không thể thực hiện chức năng khôi phục dữ liệu đã xóa.

## Khôi phục ảnh đã xóa thực sự

Tính năng khôi phục ảnh đã xóa giúp bạn tìm và khôi phục các ảnh đã thực sự bị xóa trên thiết bị.

### Cách hoạt động

Khi bạn nhấn nút "Quét", ứng dụng sẽ thực hiện các bước sau:

1. **Quét thùng rác hệ thống** - Trên Android 12 trở lên, ứng dụng sẽ tìm kiếm các ảnh trong thùng rác của hệ thống (MediaStore.Trash). Đây là nơi các ảnh bị xóa gần đây sẽ được lưu trữ trước khi bị xóa vĩnh viễn.

2. **Quét thùng rác của các ứng dụng Thư viện Ảnh** - Ứng dụng sẽ tìm kiếm trong các thư mục thùng rác của ứng dụng thư viện ảnh phổ biến như Google Photos, Samsung Gallery, MIUI Gallery, v.v.

3. **Tìm kiếm trong các thư mục ẩn** - Ứng dụng sẽ tìm kiếm các ảnh trong các thư mục ẩn có thể chứa dữ liệu đã xóa như ".trash", ".Trash", "Recently Deleted", v.v.

4. **Quét dữ liệu cấp thấp** - Nếu không tìm thấy ảnh đã xóa trong các bước trên, ứng dụng sẽ tìm kiếm các dữ liệu còn sót lại của ảnh đã xóa trong bộ nhớ.

### Cách sử dụng

1. **Quét thiết bị**: 
   - Từ màn hình chính, nhấn vào phần "Khôi phục ảnh"
   - Nhấn nút "Bắt đầu quét" để tìm kiếm các ảnh đã xóa
   - Trong quá trình quét, màn hình hiển thị tiến trình và thông báo trạng thái

2. **Xem kết quả quét**:
   - Sau khi quét xong, màn hình hiển thị thông tin về số lượng ảnh đã xóa tìm thấy
   - Tất cả ảnh được hiển thị đều là ảnh đã bị xóa, được đánh dấu bằng nhãn "ĐÃ XÓA" màu đỏ
   - Ứng dụng phân loại chính xác và chỉ hiển thị các ảnh đã bị xóa thực sự, bỏ qua ảnh thông thường
   - Nhấn "Xem kết quả" để xem danh sách đầy đủ

3. **Khôi phục ảnh**:
   - Chọn các ảnh muốn khôi phục bằng cách nhấn vào ảnh
   - Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
   - Đối với ảnh trong thùng rác của hệ thống (Android 12+), ứng dụng sẽ khôi phục trực tiếp từ thùng rác
   - Các ảnh khác sẽ được sao chép vào thư mục "RecoveredPhotos" của ứng dụng
   - Bạn có thể truy cập ảnh đã khôi phục từ ứng dụng Quản lý File của thiết bị

### Lưu ý quan trọng

- Khả năng tìm thấy ảnh đã xóa phụ thuộc vào:
  - Thời gian đã trôi qua kể từ khi xóa
  - Mức độ sử dụng thiết bị sau khi xóa
  - Phiên bản Android của thiết bị (Android 12+ có chức năng thùng rác tốt hơn)
  
- Để có kết quả tốt nhất:
  - Sử dụng tính năng này ngay sau khi vô tình xóa ảnh
  - Giảm thiểu việc lưu dữ liệu mới sau khi xóa ảnh
  - Tắt WiFi/4G để tránh các ứng dụng tự động tải xuống dữ liệu mới
  - Nếu sử dụng Android 12+, ảnh bị xóa sẽ có khả năng khôi phục cao hơn

## Tính năng khôi phục video

### Mô tả
Tính năng khôi phục video cho phép bạn tìm và khôi phục các video đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục video"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các video đã xóa
3. Sau khi quét xong, chọn các video bạn muốn khôi phục bằng cách nhấn vào video
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các video đã khôi phục sẽ được lưu trong thư mục "RecoveredVideos" của ứng dụng

## Tính năng khôi phục tệp tin

### Mô tả
Tính năng khôi phục tệp tin cho phép bạn tìm và khôi phục các tệp tin khác (PDF, DOC, XLS, v.v.) đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục các tệp khác"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các tệp tin đã xóa
3. Sau khi quét xong, chọn các tệp tin bạn muốn khôi phục bằng cách nhấn vào tệp tin
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các tệp tin đã khôi phục sẽ được lưu trong thư mục "RecoveredFiles" của ứng dụng

## Lưu ý quan trọng
- Ứng dụng cần quyền truy cập bộ nhớ để thực hiện chức năng khôi phục
- Tỷ lệ khôi phục thành công phụ thuộc vào nhiều yếu tố như thời gian xóa, mức độ sử dụng thiết bị sau khi xóa
- Để tăng khả năng khôi phục, hãy sử dụng tính năng này ngay sau khi dữ liệu bị xóa
- Không lưu thêm dữ liệu mới vào thiết bị sau khi xóa dữ liệu để tránh việc ghi đè lên dữ liệu đã xóa

## Quyền truy cập tất cả file

Khi bạn mở ứng dụng lần đầu tiên, sẽ xuất hiện một dialog yêu cầu quyền truy cập tất cả file. Đây là quyền cần thiết để ứng dụng có thể quét và khôi phục các tệp đã bị xóa trong bộ nhớ thiết bị.

### Cách cấp quyền:

1. Khi dialog xuất hiện, chọn "Cấp quyền"
2. Bạn sẽ được chuyển đến màn hình cài đặt của hệ thống
3. Bật công tắc "Cho phép quản lý tất cả tệp"
4. Quay lại ứng dụng

Nếu bạn không cấp quyền, ứng dụng sẽ không thể thực hiện chức năng khôi phục dữ liệu đã xóa.

## Khôi phục ảnh đã xóa thực sự

Tính năng khôi phục ảnh đã xóa giúp bạn tìm và khôi phục các ảnh đã thực sự bị xóa trên thiết bị.

### Cách hoạt động

Khi bạn nhấn nút "Quét", ứng dụng sẽ thực hiện các bước sau:

1. **Quét thùng rác hệ thống** - Trên Android 12 trở lên, ứng dụng sẽ tìm kiếm các ảnh trong thùng rác của hệ thống (MediaStore.Trash). Đây là nơi các ảnh bị xóa gần đây sẽ được lưu trữ trước khi bị xóa vĩnh viễn.

2. **Quét thùng rác của các ứng dụng Thư viện Ảnh** - Ứng dụng sẽ tìm kiếm trong các thư mục thùng rác của ứng dụng thư viện ảnh phổ biến như Google Photos, Samsung Gallery, MIUI Gallery, v.v.

3. **Tìm kiếm trong các thư mục ẩn** - Ứng dụng sẽ tìm kiếm các ảnh trong các thư mục ẩn có thể chứa dữ liệu đã xóa như ".trash", ".Trash", "Recently Deleted", v.v.

4. **Quét dữ liệu cấp thấp** - Nếu không tìm thấy ảnh đã xóa trong các bước trên, ứng dụng sẽ tìm kiếm các dữ liệu còn sót lại của ảnh đã xóa trong bộ nhớ.

### Cách sử dụng

1. **Quét thiết bị**: 
   - Từ màn hình chính, nhấn vào phần "Khôi phục ảnh"
   - Nhấn nút "Bắt đầu quét" để tìm kiếm các ảnh đã xóa
   - Trong quá trình quét, màn hình hiển thị tiến trình và thông báo trạng thái

2. **Xem kết quả quét**:
   - Sau khi quét xong, màn hình hiển thị thông tin về số lượng ảnh đã xóa tìm thấy
   - Tất cả ảnh được hiển thị đều là ảnh đã bị xóa, được đánh dấu bằng nhãn "ĐÃ XÓA" màu đỏ
   - Ứng dụng phân loại chính xác và chỉ hiển thị các ảnh đã bị xóa thực sự, bỏ qua ảnh thông thường
   - Nhấn "Xem kết quả" để xem danh sách đầy đủ

3. **Khôi phục ảnh**:
   - Chọn các ảnh muốn khôi phục bằng cách nhấn vào ảnh
   - Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
   - Đối với ảnh trong thùng rác của hệ thống (Android 12+), ứng dụng sẽ khôi phục trực tiếp từ thùng rác
   - Các ảnh khác sẽ được sao chép vào thư mục "RecoveredPhotos" của ứng dụng
   - Bạn có thể truy cập ảnh đã khôi phục từ ứng dụng Quản lý File của thiết bị

### Lưu ý quan trọng

- Khả năng tìm thấy ảnh đã xóa phụ thuộc vào:
  - Thời gian đã trôi qua kể từ khi xóa
  - Mức độ sử dụng thiết bị sau khi xóa
  - Phiên bản Android của thiết bị (Android 12+ có chức năng thùng rác tốt hơn)
  
- Để có kết quả tốt nhất:
  - Sử dụng tính năng này ngay sau khi vô tình xóa ảnh
  - Giảm thiểu việc lưu dữ liệu mới sau khi xóa ảnh
  - Tắt WiFi/4G để tránh các ứng dụng tự động tải xuống dữ liệu mới
  - Nếu sử dụng Android 12+, ảnh bị xóa sẽ có khả năng khôi phục cao hơn

## Tính năng khôi phục video

### Mô tả
Tính năng khôi phục video cho phép bạn tìm và khôi phục các video đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục video"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các video đã xóa
3. Sau khi quét xong, chọn các video bạn muốn khôi phục bằng cách nhấn vào video
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các video đã khôi phục sẽ được lưu trong thư mục "RecoveredVideos" của ứng dụng

## Tính năng khôi phục tệp tin

### Mô tả
Tính năng khôi phục tệp tin cho phép bạn tìm và khôi phục các tệp tin khác (PDF, DOC, XLS, v.v.) đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục các tệp khác"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các tệp tin đã xóa
3. Sau khi quét xong, chọn các tệp tin bạn muốn khôi phục bằng cách nhấn vào tệp tin
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các tệp tin đã khôi phục sẽ được lưu trong thư mục "RecoveredFiles" của ứng dụng

## Lưu ý quan trọng
- Ứng dụng cần quyền truy cập bộ nhớ để thực hiện chức năng khôi phục
- Tỷ lệ khôi phục thành công phụ thuộc vào nhiều yếu tố như thời gian xóa, mức độ sử dụng thiết bị sau khi xóa
- Để tăng khả năng khôi phục, hãy sử dụng tính năng này ngay sau khi dữ liệu bị xóa
- Không lưu thêm dữ liệu mới vào thiết bị sau khi xóa dữ liệu để tránh việc ghi đè lên dữ liệu đã xóa

## Quyền truy cập tất cả file

Khi bạn mở ứng dụng lần đầu tiên, sẽ xuất hiện một dialog yêu cầu quyền truy cập tất cả file. Đây là quyền cần thiết để ứng dụng có thể quét và khôi phục các tệp đã bị xóa trong bộ nhớ thiết bị.

### Cách cấp quyền:

1. Khi dialog xuất hiện, chọn "Cấp quyền"
2. Bạn sẽ được chuyển đến màn hình cài đặt của hệ thống
3. Bật công tắc "Cho phép quản lý tất cả tệp"
4. Quay lại ứng dụng

Nếu bạn không cấp quyền, ứng dụng sẽ không thể thực hiện chức năng khôi phục dữ liệu đã xóa.

## Khôi phục ảnh đã xóa thực sự

Tính năng khôi phục ảnh đã xóa giúp bạn tìm và khôi phục các ảnh đã thực sự bị xóa trên thiết bị.

### Cách hoạt động

Khi bạn nhấn nút "Quét", ứng dụng sẽ thực hiện các bước sau:

1. **Quét thùng rác hệ thống** - Trên Android 12 trở lên, ứng dụng sẽ tìm kiếm các ảnh trong thùng rác của hệ thống (MediaStore.Trash). Đây là nơi các ảnh bị xóa gần đây sẽ được lưu trữ trước khi bị xóa vĩnh viễn.

2. **Quét thùng rác của các ứng dụng Thư viện Ảnh** - Ứng dụng sẽ tìm kiếm trong các thư mục thùng rác của ứng dụng thư viện ảnh phổ biến như Google Photos, Samsung Gallery, MIUI Gallery, v.v.

3. **Tìm kiếm trong các thư mục ẩn** - Ứng dụng sẽ tìm kiếm các ảnh trong các thư mục ẩn có thể chứa dữ liệu đã xóa như ".trash", ".Trash", "Recently Deleted", v.v.

4. **Quét dữ liệu cấp thấp** - Nếu không tìm thấy ảnh đã xóa trong các bước trên, ứng dụng sẽ tìm kiếm các dữ liệu còn sót lại của ảnh đã xóa trong bộ nhớ.

### Cách sử dụng

1. **Quét thiết bị**: 
   - Từ màn hình chính, nhấn vào phần "Khôi phục ảnh"
   - Nhấn nút "Bắt đầu quét" để tìm kiếm các ảnh đã xóa
   - Trong quá trình quét, màn hình hiển thị tiến trình và thông báo trạng thái

2. **Xem kết quả quét**:
   - Sau khi quét xong, màn hình hiển thị thông tin về số lượng ảnh đã xóa tìm thấy
   - Tất cả ảnh được hiển thị đều là ảnh đã bị xóa, được đánh dấu bằng nhãn "ĐÃ XÓA" màu đỏ
   - Ứng dụng phân loại chính xác và chỉ hiển thị các ảnh đã bị xóa thực sự, bỏ qua ảnh thông thường
   - Nhấn "Xem kết quả" để xem danh sách đầy đủ

3. **Khôi phục ảnh**:
   - Chọn các ảnh muốn khôi phục bằng cách nhấn vào ảnh
   - Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
   - Đối với ảnh trong thùng rác của hệ thống (Android 12+), ứng dụng sẽ khôi phục trực tiếp từ thùng rác
   - Các ảnh khác sẽ được sao chép vào thư mục "RecoveredPhotos" của ứng dụng
   - Bạn có thể truy cập ảnh đã khôi phục từ ứng dụng Quản lý File của thiết bị

### Lưu ý quan trọng

- Khả năng tìm thấy ảnh đã xóa phụ thuộc vào:
  - Thời gian đã trôi qua kể từ khi xóa
  - Mức độ sử dụng thiết bị sau khi xóa
  - Phiên bản Android của thiết bị (Android 12+ có chức năng thùng rác tốt hơn)
  
- Để có kết quả tốt nhất:
  - Sử dụng tính năng này ngay sau khi vô tình xóa ảnh
  - Giảm thiểu việc lưu dữ liệu mới sau khi xóa ảnh
  - Tắt WiFi/4G để tránh các ứng dụng tự động tải xuống dữ liệu mới
  - Nếu sử dụng Android 12+, ảnh bị xóa sẽ có khả năng khôi phục cao hơn

## Tính năng khôi phục video

### Mô tả
Tính năng khôi phục video cho phép bạn tìm và khôi phục các video đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục video"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các video đã xóa
3. Sau khi quét xong, chọn các video bạn muốn khôi phục bằng cách nhấn vào video
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các video đã khôi phục sẽ được lưu trong thư mục "RecoveredVideos" của ứng dụng

## Tính năng khôi phục tệp tin

### Mô tả
Tính năng khôi phục tệp tin cho phép bạn tìm và khôi phục các tệp tin khác (PDF, DOC, XLS, v.v.) đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục các tệp khác"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các tệp tin đã xóa
3. Sau khi quét xong, chọn các tệp tin bạn muốn khôi phục bằng cách nhấn vào tệp tin
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các tệp tin đã khôi phục sẽ được lưu trong thư mục "RecoveredFiles" của ứng dụng

## Lưu ý quan trọng
- Ứng dụng cần quyền truy cập bộ nhớ để thực hiện chức năng khôi phục
- Tỷ lệ khôi phục thành công phụ thuộc vào nhiều yếu tố như thời gian xóa, mức độ sử dụng thiết bị sau khi xóa
- Để tăng khả năng khôi phục, hãy sử dụng tính năng này ngay sau khi dữ liệu bị xóa
- Không lưu thêm dữ liệu mới vào thiết bị sau khi xóa dữ liệu để tránh việc ghi đè lên dữ liệu đã xóa

## Quyền truy cập tất cả file

Khi bạn mở ứng dụng lần đầu tiên, sẽ xuất hiện một dialog yêu cầu quyền truy cập tất cả file. Đây là quyền cần thiết để ứng dụng có thể quét và khôi phục các tệp đã bị xóa trong bộ nhớ thiết bị.

### Cách cấp quyền:

1. Khi dialog xuất hiện, chọn "Cấp quyền"
2. Bạn sẽ được chuyển đến màn hình cài đặt của hệ thống
3. Bật công tắc "Cho phép quản lý tất cả tệp"
4. Quay lại ứng dụng

Nếu bạn không cấp quyền, ứng dụng sẽ không thể thực hiện chức năng khôi phục dữ liệu đã xóa.

## Khôi phục ảnh đã xóa thực sự

Tính năng khôi phục ảnh đã xóa giúp bạn tìm và khôi phục các ảnh đã thực sự bị xóa trên thiết bị.

### Cách hoạt động

Khi bạn nhấn nút "Quét", ứng dụng sẽ thực hiện các bước sau:

1. **Quét thùng rác hệ thống** - Trên Android 12 trở lên, ứng dụng sẽ tìm kiếm các ảnh trong thùng rác của hệ thống (MediaStore.Trash). Đây là nơi các ảnh bị xóa gần đây sẽ được lưu trữ trước khi bị xóa vĩnh viễn.

2. **Quét thùng rác của các ứng dụng Thư viện Ảnh** - Ứng dụng sẽ tìm kiếm trong các thư mục thùng rác của ứng dụng thư viện ảnh phổ biến như Google Photos, Samsung Gallery, MIUI Gallery, v.v.

3. **Tìm kiếm trong các thư mục ẩn** - Ứng dụng sẽ tìm kiếm các ảnh trong các thư mục ẩn có thể chứa dữ liệu đã xóa như ".trash", ".Trash", "Recently Deleted", v.v.

4. **Quét dữ liệu cấp thấp** - Nếu không tìm thấy ảnh đã xóa trong các bước trên, ứng dụng sẽ tìm kiếm các dữ liệu còn sót lại của ảnh đã xóa trong bộ nhớ.

### Cách sử dụng

1. **Quét thiết bị**: 
   - Từ màn hình chính, nhấn vào phần "Khôi phục ảnh"
   - Nhấn nút "Bắt đầu quét" để tìm kiếm các ảnh đã xóa
   - Trong quá trình quét, màn hình hiển thị tiến trình và thông báo trạng thái

2. **Xem kết quả quét**:
   - Sau khi quét xong, màn hình hiển thị thông tin về số lượng ảnh đã xóa tìm thấy
   - Tất cả ảnh được hiển thị đều là ảnh đã bị xóa, được đánh dấu bằng nhãn "ĐÃ XÓA" màu đỏ
   - Ứng dụng phân loại chính xác và chỉ hiển thị các ảnh đã bị xóa thực sự, bỏ qua ảnh thông thường
   - Nhấn "Xem kết quả" để xem danh sách đầy đủ

3. **Khôi phục ảnh**:
   - Chọn các ảnh muốn khôi phục bằng cách nhấn vào ảnh
   - Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
   - Đối với ảnh trong thùng rác của hệ thống (Android 12+), ứng dụng sẽ khôi phục trực tiếp từ thùng rác
   - Các ảnh khác sẽ được sao chép vào thư mục "RecoveredPhotos" của ứng dụng
   - Bạn có thể truy cập ảnh đã khôi phục từ ứng dụng Quản lý File của thiết bị

### Lưu ý quan trọng

- Khả năng tìm thấy ảnh đã xóa phụ thuộc vào:
  - Thời gian đã trôi qua kể từ khi xóa
  - Mức độ sử dụng thiết bị sau khi xóa
  - Phiên bản Android của thiết bị (Android 12+ có chức năng thùng rác tốt hơn)
  
- Để có kết quả tốt nhất:
  - Sử dụng tính năng này ngay sau khi vô tình xóa ảnh
  - Giảm thiểu việc lưu dữ liệu mới sau khi xóa ảnh
  - Tắt WiFi/4G để tránh các ứng dụng tự động tải xuống dữ liệu mới
  - Nếu sử dụng Android 12+, ảnh bị xóa sẽ có khả năng khôi phục cao hơn

## Tính năng khôi phục video

### Mô tả
Tính năng khôi phục video cho phép bạn tìm và khôi phục các video đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục video"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các video đã xóa
3. Sau khi quét xong, chọn các video bạn muốn khôi phục bằng cách nhấn vào video
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các video đã khôi phục sẽ được lưu trong thư mục "RecoveredVideos" của ứng dụng

## Tính năng khôi phục tệp tin

### Mô tả
Tính năng khôi phục tệp tin cho phép bạn tìm và khôi phục các tệp tin khác (PDF, DOC, XLS, v.v.) đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục các tệp khác"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các tệp tin đã xóa
3. Sau khi quét xong, chọn các tệp tin bạn muốn khôi phục bằng cách nhấn vào tệp tin
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các tệp tin đã khôi phục sẽ được lưu trong thư mục "RecoveredFiles" của ứng dụng

## Lưu ý quan trọng
- Ứng dụng cần quyền truy cập bộ nhớ để thực hiện chức năng khôi phục
- Tỷ lệ khôi phục thành công phụ thuộc vào nhiều yếu tố như thời gian xóa, mức độ sử dụng thiết bị sau khi xóa
- Để tăng khả năng khôi phục, hãy sử dụng tính năng này ngay sau khi dữ liệu bị xóa
- Không lưu thêm dữ liệu mới vào thiết bị sau khi xóa dữ liệu để tránh việc ghi đè lên dữ liệu đã xóa

## Quyền truy cập tất cả file

Khi bạn mở ứng dụng lần đầu tiên, sẽ xuất hiện một dialog yêu cầu quyền truy cập tất cả file. Đây là quyền cần thiết để ứng dụng có thể quét và khôi phục các tệp đã bị xóa trong bộ nhớ thiết bị.

### Cách cấp quyền:

1. Khi dialog xuất hiện, chọn "Cấp quyền"
2. Bạn sẽ được chuyển đến màn hình cài đặt của hệ thống
3. Bật công tắc "Cho phép quản lý tất cả tệp"
4. Quay lại ứng dụng

Nếu bạn không cấp quyền, ứng dụng sẽ không thể thực hiện chức năng khôi phục dữ liệu đã xóa.

## Khôi phục ảnh đã xóa thực sự

Tính năng khôi phục ảnh đã xóa giúp bạn tìm và khôi phục các ảnh đã thực sự bị xóa trên thiết bị.

### Cách hoạt động

Khi bạn nhấn nút "Quét", ứng dụng sẽ thực hiện các bước sau:

1. **Quét thùng rác hệ thống** - Trên Android 12 trở lên, ứng dụng sẽ tìm kiếm các ảnh trong thùng rác của hệ thống (MediaStore.Trash). Đây là nơi các ảnh bị xóa gần đây sẽ được lưu trữ trước khi bị xóa vĩnh viễn.

2. **Quét thùng rác của các ứng dụng Thư viện Ảnh** - Ứng dụng sẽ tìm kiếm trong các thư mục thùng rác của ứng dụng thư viện ảnh phổ biến như Google Photos, Samsung Gallery, MIUI Gallery, v.v.

3. **Tìm kiếm trong các thư mục ẩn** - Ứng dụng sẽ tìm kiếm các ảnh trong các thư mục ẩn có thể chứa dữ liệu đã xóa như ".trash", ".Trash", "Recently Deleted", v.v.

4. **Quét dữ liệu cấp thấp** - Nếu không tìm thấy ảnh đã xóa trong các bước trên, ứng dụng sẽ tìm kiếm các dữ liệu còn sót lại của ảnh đã xóa trong bộ nhớ.

### Cách sử dụng

1. **Quét thiết bị**: 
   - Từ màn hình chính, nhấn vào phần "Khôi phục ảnh"
   - Nhấn nút "Bắt đầu quét" để tìm kiếm các ảnh đã xóa
   - Trong quá trình quét, màn hình hiển thị tiến trình và thông báo trạng thái

2. **Xem kết quả quét**:
   - Sau khi quét xong, màn hình hiển thị thông tin về số lượng ảnh đã xóa tìm thấy
   - Tất cả ảnh được hiển thị đều là ảnh đã bị xóa, được đánh dấu bằng nhãn "ĐÃ XÓA" màu đỏ
   - Ứng dụng phân loại chính xác và chỉ hiển thị các ảnh đã bị xóa thực sự, bỏ qua ảnh thông thường
   - Nhấn "Xem kết quả" để xem danh sách đầy đủ

3. **Khôi phục ảnh**:
   - Chọn các ảnh muốn khôi phục bằng cách nhấn vào ảnh
   - Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
   - Đối với ảnh trong thùng rác của hệ thống (Android 12+), ứng dụng sẽ khôi phục trực tiếp từ thùng rác
   - Các ảnh khác sẽ được sao chép vào thư mục "RecoveredPhotos" của ứng dụng
   - Bạn có thể truy cập ảnh đã khôi phục từ ứng dụng Quản lý File của thiết bị

### Lưu ý quan trọng

- Khả năng tìm thấy ảnh đã xóa phụ thuộc vào:
  - Thời gian đã trôi qua kể từ khi xóa
  - Mức độ sử dụng thiết bị sau khi xóa
  - Phiên bản Android của thiết bị (Android 12+ có chức năng thùng rác tốt hơn)
  
- Để có kết quả tốt nhất:
  - Sử dụng tính năng này ngay sau khi vô tình xóa ảnh
  - Giảm thiểu việc lưu dữ liệu mới sau khi xóa ảnh
  - Tắt WiFi/4G để tránh các ứng dụng tự động tải xuống dữ liệu mới
  - Nếu sử dụng Android 12+, ảnh bị xóa sẽ có khả năng khôi phục cao hơn

## Tính năng khôi phục video

### Mô tả
Tính năng khôi phục video cho phép bạn tìm và khôi phục các video đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục video"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các video đã xóa
3. Sau khi quét xong, chọn các video bạn muốn khôi phục bằng cách nhấn vào video
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các video đã khôi phục sẽ được lưu trong thư mục "RecoveredVideos" của ứng dụng

## Tính năng khôi phục tệp tin

### Mô tả
Tính năng khôi phục tệp tin cho phép bạn tìm và khôi phục các tệp tin khác (PDF, DOC, XLS, v.v.) đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục các tệp khác"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các tệp tin đã xóa
3. Sau khi quét xong, chọn các tệp tin bạn muốn khôi phục bằng cách nhấn vào tệp tin
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các tệp tin đã khôi phục sẽ được lưu trong thư mục "RecoveredFiles" của ứng dụng

## Lưu ý quan trọng
- Ứng dụng cần quyền truy cập bộ nhớ để thực hiện chức năng khôi phục
- Tỷ lệ khôi phục thành công phụ thuộc vào nhiều yếu tố như thời gian xóa, mức độ sử dụng thiết bị sau khi xóa
- Để tăng khả năng khôi phục, hãy sử dụng tính năng này ngay sau khi dữ liệu bị xóa
- Không lưu thêm dữ liệu mới vào thiết bị sau khi xóa dữ liệu để tránh việc ghi đè lên dữ liệu đã xóa

## Quyền truy cập tất cả file

Khi bạn mở ứng dụng lần đầu tiên, sẽ xuất hiện một dialog yêu cầu quyền truy cập tất cả file. Đây là quyền cần thiết để ứng dụng có thể quét và khôi phục các tệp đã bị xóa trong bộ nhớ thiết bị.

### Cách cấp quyền:

1. Khi dialog xuất hiện, chọn "Cấp quyền"
2. Bạn sẽ được chuyển đến màn hình cài đặt của hệ thống
3. Bật công tắc "Cho phép quản lý tất cả tệp"
4. Quay lại ứng dụng

Nếu bạn không cấp quyền, ứng dụng sẽ không thể thực hiện chức năng khôi phục dữ liệu đã xóa.

## Khôi phục ảnh đã xóa thực sự

Tính năng khôi phục ảnh đã xóa giúp bạn tìm và khôi phục các ảnh đã thực sự bị xóa trên thiết bị.

### Cách hoạt động

Khi bạn nhấn nút "Quét", ứng dụng sẽ thực hiện các bước sau:

1. **Quét thùng rác hệ thống** - Trên Android 12 trở lên, ứng dụng sẽ tìm kiếm các ảnh trong thùng rác của hệ thống (MediaStore.Trash). Đây là nơi các ảnh bị xóa gần đây sẽ được lưu trữ trước khi bị xóa vĩnh viễn.

2. **Quét thùng rác của các ứng dụng Thư viện Ảnh** - Ứng dụng sẽ tìm kiếm trong các thư mục thùng rác của ứng dụng thư viện ảnh phổ biến như Google Photos, Samsung Gallery, MIUI Gallery, v.v.

3. **Tìm kiếm trong các thư mục ẩn** - Ứng dụng sẽ tìm kiếm các ảnh trong các thư mục ẩn có thể chứa dữ liệu đã xóa như ".trash", ".Trash", "Recently Deleted", v.v.

4. **Quét dữ liệu cấp thấp** - Nếu không tìm thấy ảnh đã xóa trong các bước trên, ứng dụng sẽ tìm kiếm các dữ liệu còn sót lại của ảnh đã xóa trong bộ nhớ.

### Cách sử dụng

1. **Quét thiết bị**: 
   - Từ màn hình chính, nhấn vào phần "Khôi phục ảnh"
   - Nhấn nút "Bắt đầu quét" để tìm kiếm các ảnh đã xóa
   - Trong quá trình quét, màn hình hiển thị tiến trình và thông báo trạng thái

2. **Xem kết quả quét**:
   - Sau khi quét xong, màn hình hiển thị thông tin về số lượng ảnh đã xóa tìm thấy
   - Tất cả ảnh được hiển thị đều là ảnh đã bị xóa, được đánh dấu bằng nhãn "ĐÃ XÓA" màu đỏ
   - Ứng dụng phân loại chính xác và chỉ hiển thị các ảnh đã bị xóa thực sự, bỏ qua ảnh thông thường
   - Nhấn "Xem kết quả" để xem danh sách đầy đủ

3. **Khôi phục ảnh**:
   - Chọn các ảnh muốn khôi phục bằng cách nhấn vào ảnh
   - Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
   - Đối với ảnh trong thùng rác của hệ thống (Android 12+), ứng dụng sẽ khôi phục trực tiếp từ thùng rác
   - Các ảnh khác sẽ được sao chép vào thư mục "RecoveredPhotos" của ứng dụng
   - Bạn có thể truy cập ảnh đã khôi phục từ ứng dụng Quản lý File của thiết bị

### Lưu ý quan trọng

- Khả năng tìm thấy ảnh đã xóa phụ thuộc vào:
  - Thời gian đã trôi qua kể từ khi xóa
  - Mức độ sử dụng thiết bị sau khi xóa
  - Phiên bản Android của thiết bị (Android 12+ có chức năng thùng rác tốt hơn)
  
- Để có kết quả tốt nhất:
  - Sử dụng tính năng này ngay sau khi vô tình xóa ảnh
  - Giảm thiểu việc lưu dữ liệu mới sau khi xóa ảnh
  - Tắt WiFi/4G để tránh các ứng dụng tự động tải xuống dữ liệu mới
  - Nếu sử dụng Android 12+, ảnh bị xóa sẽ có khả năng khôi phục cao hơn

## Tính năng khôi phục video

### Mô tả
Tính năng khôi phục video cho phép bạn tìm và khôi phục các video đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục video"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các video đã xóa
3. Sau khi quét xong, chọn các video bạn muốn khôi phục bằng cách nhấn vào video
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các video đã khôi phục sẽ được lưu trong thư mục "RecoveredVideos" của ứng dụng

## Tính năng khôi phục tệp tin

### Mô tả
Tính năng khôi phục tệp tin cho phép bạn tìm và khôi phục các tệp tin khác (PDF, DOC, XLS, v.v.) đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục các tệp khác"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các tệp tin đã xóa
3. Sau khi quét xong, chọn các tệp tin bạn muốn khôi phục bằng cách nhấn vào tệp tin
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các tệp tin đã khôi phục sẽ được lưu trong thư mục "RecoveredFiles" của ứng dụng

## Lưu ý quan trọng
- Ứng dụng cần quyền truy cập bộ nhớ để thực hiện chức năng khôi phục
- Tỷ lệ khôi phục thành công phụ thuộc vào nhiều yếu tố như thời gian xóa, mức độ sử dụng thiết bị sau khi xóa
- Để tăng khả năng khôi phục, hãy sử dụng tính năng này ngay sau khi dữ liệu bị xóa
- Không lưu thêm dữ liệu mới vào thiết bị sau khi xóa dữ liệu để tránh việc ghi đè lên dữ liệu đã xóa

## Quyền truy cập tất cả file

Khi bạn mở ứng dụng lần đầu tiên, sẽ xuất hiện một dialog yêu cầu quyền truy cập tất cả file. Đây là quyền cần thiết để ứng dụng có thể quét và khôi phục các tệp đã bị xóa trong bộ nhớ thiết bị.

### Cách cấp quyền:

1. Khi dialog xuất hiện, chọn "Cấp quyền"
2. Bạn sẽ được chuyển đến màn hình cài đặt của hệ thống
3. Bật công tắc "Cho phép quản lý tất cả tệp"
4. Quay lại ứng dụng

Nếu bạn không cấp quyền, ứng dụng sẽ không thể thực hiện chức năng khôi phục dữ liệu đã xóa.

## Khôi phục ảnh đã xóa thực sự

Tính năng khôi phục ảnh đã xóa giúp bạn tìm và khôi phục các ảnh đã thực sự bị xóa trên thiết bị.

### Cách hoạt động

Khi bạn nhấn nút "Quét", ứng dụng sẽ thực hiện các bước sau:

1. **Quét thùng rác hệ thống** - Trên Android 12 trở lên, ứng dụng sẽ tìm kiếm các ảnh trong thùng rác của hệ thống (MediaStore.Trash). Đây là nơi các ảnh bị xóa gần đây sẽ được lưu trữ trước khi bị xóa vĩnh viễn.

2. **Quét thùng rác của các ứng dụng Thư viện Ảnh** - Ứng dụng sẽ tìm kiếm trong các thư mục thùng rác của ứng dụng thư viện ảnh phổ biến như Google Photos, Samsung Gallery, MIUI Gallery, v.v.

3. **Tìm kiếm trong các thư mục ẩn** - Ứng dụng sẽ tìm kiếm các ảnh trong các thư mục ẩn có thể chứa dữ liệu đã xóa như ".trash", ".Trash", "Recently Deleted", v.v.

4. **Quét dữ liệu cấp thấp** - Nếu không tìm thấy ảnh đã xóa trong các bước trên, ứng dụng sẽ tìm kiếm các dữ liệu còn sót lại của ảnh đã xóa trong bộ nhớ.

### Cách sử dụng

1. **Quét thiết bị**: 
   - Từ màn hình chính, nhấn vào phần "Khôi phục ảnh"
   - Nhấn nút "Bắt đầu quét" để tìm kiếm các ảnh đã xóa
   - Trong quá trình quét, màn hình hiển thị tiến trình và thông báo trạng thái

2. **Xem kết quả quét**:
   - Sau khi quét xong, màn hình hiển thị thông tin về số lượng ảnh đã xóa tìm thấy
   - Tất cả ảnh được hiển thị đều là ảnh đã bị xóa, được đánh dấu bằng nhãn "ĐÃ XÓA" màu đỏ
   - Ứng dụng phân loại chính xác và chỉ hiển thị các ảnh đã bị xóa thực sự, bỏ qua ảnh thông thường
   - Nhấn "Xem kết quả" để xem danh sách đầy đủ

3. **Khôi phục ảnh**:
   - Chọn các ảnh muốn khôi phục bằng cách nhấn vào ảnh
   - Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
   - Đối với ảnh trong thùng rác của hệ thống (Android 12+), ứng dụng sẽ khôi phục trực tiếp từ thùng rác
   - Các ảnh khác sẽ được sao chép vào thư mục "RecoveredPhotos" của ứng dụng
   - Bạn có thể truy cập ảnh đã khôi phục từ ứng dụng Quản lý File của thiết bị

### Lưu ý quan trọng

- Khả năng tìm thấy ảnh đã xóa phụ thuộc vào:
  - Thời gian đã trôi qua kể từ khi xóa
  - Mức độ sử dụng thiết bị sau khi xóa
  - Phiên bản Android của thiết bị (Android 12+ có chức năng thùng rác tốt hơn)
  
- Để có kết quả tốt nhất:
  - Sử dụng tính năng này ngay sau khi vô tình xóa ảnh
  - Giảm thiểu việc lưu dữ liệu mới sau khi xóa ảnh
  - Tắt WiFi/4G để tránh các ứng dụng tự động tải xuống dữ liệu mới
  - Nếu sử dụng Android 12+, ảnh bị xóa sẽ có khả năng khôi phục cao hơn

## Tính năng khôi phục video

### Mô tả
Tính năng khôi phục video cho phép bạn tìm và khôi phục các video đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục video"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các video đã xóa
3. Sau khi quét xong, chọn các video bạn muốn khôi phục bằng cách nhấn vào video
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các video đã khôi phục sẽ được lưu trong thư mục "RecoveredVideos" của ứng dụng

## Tính năng khôi phục tệp tin

### Mô tả
Tính năng khôi phục tệp tin cho phép bạn tìm và khôi phục các tệp tin khác (PDF, DOC, XLS, v.v.) đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục các tệp khác"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các tệp tin đã xóa
3. Sau khi quét xong, chọn các tệp tin bạn muốn khôi phục bằng cách nhấn vào tệp tin
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các tệp tin đã khôi phục sẽ được lưu trong thư mục "RecoveredFiles" của ứng dụng

## Lưu ý quan trọng
- Ứng dụng cần quyền truy cập bộ nhớ để thực hiện chức năng khôi phục
- Tỷ lệ khôi phục thành công phụ thuộc vào nhiều yếu tố như thời gian xóa, mức độ sử dụng thiết bị sau khi xóa
- Để tăng khả năng khôi phục, hãy sử dụng tính năng này ngay sau khi dữ liệu bị xóa
- Không lưu thêm dữ liệu mới vào thiết bị sau khi xóa dữ liệu để tránh việc ghi đè lên dữ liệu đã xóa

## Quyền truy cập tất cả file

Khi bạn mở ứng dụng lần đầu tiên, sẽ xuất hiện một dialog yêu cầu quyền truy cập tất cả file. Đây là quyền cần thiết để ứng dụng có thể quét và khôi phục các tệp đã bị xóa trong bộ nhớ thiết bị.

### Cách cấp quyền:

1. Khi dialog xuất hiện, chọn "Cấp quyền"
2. Bạn sẽ được chuyển đến màn hình cài đặt của hệ thống
3. Bật công tắc "Cho phép quản lý tất cả tệp"
4. Quay lại ứng dụng

Nếu bạn không cấp quyền, ứng dụng sẽ không thể thực hiện chức năng khôi phục dữ liệu đã xóa.

## Khôi phục ảnh đã xóa thực sự

Tính năng khôi phục ảnh đã xóa giúp bạn tìm và khôi phục các ảnh đã thực sự bị xóa trên thiết bị.

### Cách hoạt động

Khi bạn nhấn nút "Quét", ứng dụng sẽ thực hiện các bước sau:

1. **Quét thùng rác hệ thống** - Trên Android 12 trở lên, ứng dụng sẽ tìm kiếm các ảnh trong thùng rác của hệ thống (MediaStore.Trash). Đây là nơi các ảnh bị xóa gần đây sẽ được lưu trữ trước khi bị xóa vĩnh viễn.

2. **Quét thùng rác của các ứng dụng Thư viện Ảnh** - Ứng dụng sẽ tìm kiếm trong các thư mục thùng rác của ứng dụng thư viện ảnh phổ biến như Google Photos, Samsung Gallery, MIUI Gallery, v.v.

3. **Tìm kiếm trong các thư mục ẩn** - Ứng dụng sẽ tìm kiếm các ảnh trong các thư mục ẩn có thể chứa dữ liệu đã xóa như ".trash", ".Trash", "Recently Deleted", v.v.

4. **Quét dữ liệu cấp thấp** - Nếu không tìm thấy ảnh đã xóa trong các bước trên, ứng dụng sẽ tìm kiếm các dữ liệu còn sót lại của ảnh đã xóa trong bộ nhớ.

### Cách sử dụng

1. **Quét thiết bị**: 
   - Từ màn hình chính, nhấn vào phần "Khôi phục ảnh"
   - Nhấn nút "Bắt đầu quét" để tìm kiếm các ảnh đã xóa
   - Trong quá trình quét, màn hình hiển thị tiến trình và thông báo trạng thái

2. **Xem kết quả quét**:
   - Sau khi quét xong, màn hình hiển thị thông tin về số lượng ảnh đã xóa tìm thấy
   - Tất cả ảnh được hiển thị đều là ảnh đã bị xóa, được đánh dấu bằng nhãn "ĐÃ XÓA" màu đỏ
   - Ứng dụng phân loại chính xác và chỉ hiển thị các ảnh đã bị xóa thực sự, bỏ qua ảnh thông thường
   - Nhấn "Xem kết quả" để xem danh sách đầy đủ

3. **Khôi phục ảnh**:
   - Chọn các ảnh muốn khôi phục bằng cách nhấn vào ảnh
   - Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
   - Đối với ảnh trong thùng rác của hệ thống (Android 12+), ứng dụng sẽ khôi phục trực tiếp từ thùng rác
   - Các ảnh khác sẽ được sao chép vào thư mục "RecoveredPhotos" của ứng dụng
   - Bạn có thể truy cập ảnh đã khôi phục từ ứng dụng Quản lý File của thiết bị

### Lưu ý quan trọng

- Khả năng tìm thấy ảnh đã xóa phụ thuộc vào:
  - Thời gian đã trôi qua kể từ khi xóa
  - Mức độ sử dụng thiết bị sau khi xóa
  - Phiên bản Android của thiết bị (Android 12+ có chức năng thùng rác tốt hơn)
  
- Để có kết quả tốt nhất:
  - Sử dụng tính năng này ngay sau khi vô tình xóa ảnh
  - Giảm thiểu việc lưu dữ liệu mới sau khi xóa ảnh
  - Tắt WiFi/4G để tránh các ứng dụng tự động tải xuống dữ liệu mới
  - Nếu sử dụng Android 12+, ảnh bị xóa sẽ có khả năng khôi phục cao hơn

## Tính năng khôi phục video

### Mô tả
Tính năng khôi phục video cho phép bạn tìm và khôi phục các video đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục video"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các video đã xóa
3. Sau khi quét xong, chọn các video bạn muốn khôi phục bằng cách nhấn vào video
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các video đã khôi phục sẽ được lưu trong thư mục "RecoveredVideos" của ứng dụng

## Tính năng khôi phục tệp tin

### Mô tả
Tính năng khôi phục tệp tin cho phép bạn tìm và khôi phục các tệp tin khác (PDF, DOC, XLS, v.v.) đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục các tệp khác"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các tệp tin đã xóa
3. Sau khi quét xong, chọn các tệp tin bạn muốn khôi phục bằng cách nhấn vào tệp tin
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các tệp tin đã khôi phục sẽ được lưu trong thư mục "RecoveredFiles" của ứng dụng

## Lưu ý quan trọng
- Ứng dụng cần quyền truy cập bộ nhớ để thực hiện chức năng khôi phục
- Tỷ lệ khôi phục thành công phụ thuộc vào nhiều yếu tố như thời gian xóa, mức độ sử dụng thiết bị sau khi xóa
- Để tăng khả năng khôi phục, hãy sử dụng tính năng này ngay sau khi dữ liệu bị xóa
- Không lưu thêm dữ liệu mới vào thiết bị sau khi xóa dữ liệu để tránh việc ghi đè lên dữ liệu đã xóa

## Quyền truy cập tất cả file

Khi bạn mở ứng dụng lần đầu tiên, sẽ xuất hiện một dialog yêu cầu quyền truy cập tất cả file. Đây là quyền cần thiết để ứng dụng có thể quét và khôi phục các tệp đã bị xóa trong bộ nhớ thiết bị.

### Cách cấp quyền:

1. Khi dialog xuất hiện, chọn "Cấp quyền"
2. Bạn sẽ được chuyển đến màn hình cài đặt của hệ thống
3. Bật công tắc "Cho phép quản lý tất cả tệp"
4. Quay lại ứng dụng

Nếu bạn không cấp quyền, ứng dụng sẽ không thể thực hiện chức năng khôi phục dữ liệu đã xóa.

## Khôi phục ảnh đã xóa thực sự

Tính năng khôi phục ảnh đã xóa giúp bạn tìm và khôi phục các ảnh đã thực sự bị xóa trên thiết bị.

### Cách hoạt động

Khi bạn nhấn nút "Quét", ứng dụng sẽ thực hiện các bước sau:

1. **Quét thùng rác hệ thống** - Trên Android 12 trở lên, ứng dụng sẽ tìm kiếm các ảnh trong thùng rác của hệ thống (MediaStore.Trash). Đây là nơi các ảnh bị xóa gần đây sẽ được lưu trữ trước khi bị xóa vĩnh viễn.

2. **Quét thùng rác của các ứng dụng Thư viện Ảnh** - Ứng dụng sẽ tìm kiếm trong các thư mục thùng rác của ứng dụng thư viện ảnh phổ biến như Google Photos, Samsung Gallery, MIUI Gallery, v.v.

3. **Tìm kiếm trong các thư mục ẩn** - Ứng dụng sẽ tìm kiếm các ảnh trong các thư mục ẩn có thể chứa dữ liệu đã xóa như ".trash", ".Trash", "Recently Deleted", v.v.

4. **Quét dữ liệu cấp thấp** - Nếu không tìm thấy ảnh đã xóa trong các bước trên, ứng dụng sẽ tìm kiếm các dữ liệu còn sót lại của ảnh đã xóa trong bộ nhớ.

### Cách sử dụng

1. **Quét thiết bị**: 
   - Từ màn hình chính, nhấn vào phần "Khôi phục ảnh"
   - Nhấn nút "Bắt đầu quét" để tìm kiếm các ảnh đã xóa
   - Trong quá trình quét, màn hình hiển thị tiến trình và thông báo trạng thái

2. **Xem kết quả quét**:
   - Sau khi quét xong, màn hình hiển thị thông tin về số lượng ảnh đã xóa tìm thấy
   - Tất cả ảnh được hiển thị đều là ảnh đã bị xóa, được đánh dấu bằng nhãn "ĐÃ XÓA" màu đỏ
   - Ứng dụng phân loại chính xác và chỉ hiển thị các ảnh đã bị xóa thực sự, bỏ qua ảnh thông thường
   - Nhấn "Xem kết quả" để xem danh sách đầy đủ

3. **Khôi phục ảnh**:
   - Chọn các ảnh muốn khôi phục bằng cách nhấn vào ảnh
   - Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
   - Đối với ảnh trong thùng rác của hệ thống (Android 12+), ứng dụng sẽ khôi phục trực tiếp từ thùng rác
   - Các ảnh khác sẽ được sao chép vào thư mục "RecoveredPhotos" của ứng dụng
   - Bạn có thể truy cập ảnh đã khôi phục từ ứng dụng Quản lý File của thiết bị

### Lưu ý quan trọng

- Khả năng tìm thấy ảnh đã xóa phụ thuộc vào:
  - Thời gian đã trôi qua kể từ khi xóa
  - Mức độ sử dụng thiết bị sau khi xóa
  - Phiên bản Android của thiết bị (Android 12+ có chức năng thùng rác tốt hơn)
  
- Để có kết quả tốt nhất:
  - Sử dụng tính năng này ngay sau khi vô tình xóa ảnh
  - Giảm thiểu việc lưu dữ liệu mới sau khi xóa ảnh
  - Tắt WiFi/4G để tránh các ứng dụng tự động tải xuống dữ liệu mới
  - Nếu sử dụng Android 12+, ảnh bị xóa sẽ có khả năng khôi phục cao hơn

## Tính năng khôi phục video

### Mô tả
Tính năng khôi phục video cho phép bạn tìm và khôi phục các video đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục video"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các video đã xóa
3. Sau khi quét xong, chọn các video bạn muốn khôi phục bằng cách nhấn vào video
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các video đã khôi phục sẽ được lưu trong thư mục "RecoveredVideos" của ứng dụng

## Tính năng khôi phục tệp tin

### Mô tả
Tính năng khôi phục tệp tin cho phép bạn tìm và khôi phục các tệp tin khác (PDF, DOC, XLS, v.v.) đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục các tệp khác"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các tệp tin đã xóa
3. Sau khi quét xong, chọn các tệp tin bạn muốn khôi phục bằng cách nhấn vào tệp tin
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các tệp tin đã khôi phục sẽ được lưu trong thư mục "RecoveredFiles" của ứng dụng

## Lưu ý quan trọng
- Ứng dụng cần quyền truy cập bộ nhớ để thực hiện chức năng khôi phục
- Tỷ lệ khôi phục thành công phụ thuộc vào nhiều yếu tố như thời gian xóa, mức độ sử dụng thiết bị sau khi xóa
- Để tăng khả năng khôi phục, hãy sử dụng tính năng này ngay sau khi dữ liệu bị xóa
- Không lưu thêm dữ liệu mới vào thiết bị sau khi xóa dữ liệu để tránh việc ghi đè lên dữ liệu đã xóa

## Quyền truy cập tất cả file

Khi bạn mở ứng dụng lần đầu tiên, sẽ xuất hiện một dialog yêu cầu quyền truy cập tất cả file. Đây là quyền cần thiết để ứng dụng có thể quét và khôi phục các tệp đã bị xóa trong bộ nhớ thiết bị.

### Cách cấp quyền:

1. Khi dialog xuất hiện, chọn "Cấp quyền"
2. Bạn sẽ được chuyển đến màn hình cài đặt của hệ thống
3. Bật công tắc "Cho phép quản lý tất cả tệp"
4. Quay lại ứng dụng

Nếu bạn không cấp quyền, ứng dụng sẽ không thể thực hiện chức năng khôi phục dữ liệu đã xóa.

## Khôi phục ảnh đã xóa thực sự

Tính năng khôi phục ảnh đã xóa giúp bạn tìm và khôi phục các ảnh đã thực sự bị xóa trên thiết bị.

### Cách hoạt động

Khi bạn nhấn nút "Quét", ứng dụng sẽ thực hiện các bước sau:

1. **Quét thùng rác hệ thống** - Trên Android 12 trở lên, ứng dụng sẽ tìm kiếm các ảnh trong thùng rác của hệ thống (MediaStore.Trash). Đây là nơi các ảnh bị xóa gần đây sẽ được lưu trữ trước khi bị xóa vĩnh viễn.

2. **Quét thùng rác của các ứng dụng Thư viện Ảnh** - Ứng dụng sẽ tìm kiếm trong các thư mục thùng rác của ứng dụng thư viện ảnh phổ biến như Google Photos, Samsung Gallery, MIUI Gallery, v.v.

3. **Tìm kiếm trong các thư mục ẩn** - Ứng dụng sẽ tìm kiếm các ảnh trong các thư mục ẩn có thể chứa dữ liệu đã xóa như ".trash", ".Trash", "Recently Deleted", v.v.

4. **Quét dữ liệu cấp thấp** - Nếu không tìm thấy ảnh đã xóa trong các bước trên, ứng dụng sẽ tìm kiếm các dữ liệu còn sót lại của ảnh đã xóa trong bộ nhớ.

### Cách sử dụng

1. **Quét thiết bị**: 
   - Từ màn hình chính, nhấn vào phần "Khôi phục ảnh"
   - Nhấn nút "Bắt đầu quét" để tìm kiếm các ảnh đã xóa
   - Trong quá trình quét, màn hình hiển thị tiến trình và thông báo trạng thái

2. **Xem kết quả quét**:
   - Sau khi quét xong, màn hình hiển thị thông tin về số lượng ảnh đã xóa tìm thấy
   - Tất cả ảnh được hiển thị đều là ảnh đã bị xóa, được đánh dấu bằng nhãn "ĐÃ XÓA" màu đỏ
   - Ứng dụng phân loại chính xác và chỉ hiển thị các ảnh đã bị xóa thực sự, bỏ qua ảnh thông thường
   - Nhấn "Xem kết quả" để xem danh sách đầy đủ

3. **Khôi phục ảnh**:
   - Chọn các ảnh muốn khôi phục bằng cách nhấn vào ảnh
   - Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
   - Đối với ảnh trong thùng rác của hệ thống (Android 12+), ứng dụng sẽ khôi phục trực tiếp từ thùng rác
   - Các ảnh khác sẽ được sao chép vào thư mục "RecoveredPhotos" của ứng dụng
   - Bạn có thể truy cập ảnh đã khôi phục từ ứng dụng Quản lý File của thiết bị

### Lưu ý quan trọng

- Khả năng tìm thấy ảnh đã xóa phụ thuộc vào:
  - Thời gian đã trôi qua kể từ khi xóa
  - Mức độ sử dụng thiết bị sau khi xóa
  - Phiên bản Android của thiết bị (Android 12+ có chức năng thùng rác tốt hơn)
  
- Để có kết quả tốt nhất:
  - Sử dụng tính năng này ngay sau khi vô tình xóa ảnh
  - Giảm thiểu việc lưu dữ liệu mới sau khi xóa ảnh
  - Tắt WiFi/4G để tránh các ứng dụng tự động tải xuống dữ liệu mới
  - Nếu sử dụng Android 12+, ảnh bị xóa sẽ có khả năng khôi phục cao hơn

## Tính năng khôi phục video

### Mô tả
Tính năng khôi phục video cho phép bạn tìm và khôi phục các video đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục video"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các video đã xóa
3. Sau khi quét xong, chọn các video bạn muốn khôi phục bằng cách nhấn vào video
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các video đã khôi phục sẽ được lưu trong thư mục "RecoveredVideos" của ứng dụng

## Tính năng khôi phục tệp tin

### Mô tả
Tính năng khôi phục tệp tin cho phép bạn tìm và khôi phục các tệp tin khác (PDF, DOC, XLS, v.v.) đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục các tệp khác"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các tệp tin đã xóa
3. Sau khi quét xong, chọn các tệp tin bạn muốn khôi phục bằng cách nhấn vào tệp tin
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các tệp tin đã khôi phục sẽ được lưu trong thư mục "RecoveredFiles" của ứng dụng

## Lưu ý quan trọng
- Ứng dụng cần quyền truy cập bộ nhớ để thực hiện chức năng khôi phục
- Tỷ lệ khôi phục thành công phụ thuộc vào nhiều yếu tố như thời gian xóa, mức độ sử dụng thiết bị sau khi xóa
- Để tăng khả năng khôi phục, hãy sử dụng tính năng này ngay sau khi dữ liệu bị xóa
- Không lưu thêm dữ liệu mới vào thiết bị sau khi xóa dữ liệu để tránh việc ghi đè lên dữ liệu đã xóa

## Quyền truy cập tất cả file

Khi bạn mở ứng dụng lần đầu tiên, sẽ xuất hiện một dialog yêu cầu quyền truy cập tất cả file. Đây là quyền cần thiết để ứng dụng có thể quét và khôi phục các tệp đã bị xóa trong bộ nhớ thiết bị.

### Cách cấp quyền:

1. Khi dialog xuất hiện, chọn "Cấp quyền"
2. Bạn sẽ được chuyển đến màn hình cài đặt của hệ thống
3. Bật công tắc "Cho phép quản lý tất cả tệp"
4. Quay lại ứng dụng

Nếu bạn không cấp quyền, ứng dụng sẽ không thể thực hiện chức năng khôi phục dữ liệu đã xóa.

## Khôi phục ảnh đã xóa thực sự

Tính năng khôi phục ảnh đã xóa giúp bạn tìm và khôi phục các ảnh đã thực sự bị xóa trên thiết bị.

### Cách hoạt động

Khi bạn nhấn nút "Quét", ứng dụng sẽ thực hiện các bước sau:

1. **Quét thùng rác hệ thống** - Trên Android 12 trở lên, ứng dụng sẽ tìm kiếm các ảnh trong thùng rác của hệ thống (MediaStore.Trash). Đây là nơi các ảnh bị xóa gần đây sẽ được lưu trữ trước khi bị xóa vĩnh viễn.

2. **Quét thùng rác của các ứng dụng Thư viện Ảnh** - Ứng dụng sẽ tìm kiếm trong các thư mục thùng rác của ứng dụng thư viện ảnh phổ biến như Google Photos, Samsung Gallery, MIUI Gallery, v.v.

3. **Tìm kiếm trong các thư mục ẩn** - Ứng dụng sẽ tìm kiếm các ảnh trong các thư mục ẩn có thể chứa dữ liệu đã xóa như ".trash", ".Trash", "Recently Deleted", v.v.

4. **Quét dữ liệu cấp thấp** - Nếu không tìm thấy ảnh đã xóa trong các bước trên, ứng dụng sẽ tìm kiếm các dữ liệu còn sót lại của ảnh đã xóa trong bộ nhớ.

### Cách sử dụng

1. **Quét thiết bị**: 
   - Từ màn hình chính, nhấn vào phần "Khôi phục ảnh"
   - Nhấn nút "Bắt đầu quét" để tìm kiếm các ảnh đã xóa
   - Trong quá trình quét, màn hình hiển thị tiến trình và thông báo trạng thái

2. **Xem kết quả quét**:
   - Sau khi quét xong, màn hình hiển thị thông tin về số lượng ảnh đã xóa tìm thấy
   - Tất cả ảnh được hiển thị đều là ảnh đã bị xóa, được đánh dấu bằng nhãn "ĐÃ XÓA" màu đỏ
   - Ứng dụng phân loại chính xác và chỉ hiển thị các ảnh đã bị xóa thực sự, bỏ qua ảnh thông thường
   - Nhấn "Xem kết quả" để xem danh sách đầy đủ

3. **Khôi phục ảnh**:
   - Chọn các ảnh muốn khôi phục bằng cách nhấn vào ảnh
   - Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
   - Đối với ảnh trong thùng rác của hệ thống (Android 12+), ứng dụng sẽ khôi phục trực tiếp từ thùng rác
   - Các ảnh khác sẽ được sao chép vào thư mục "RecoveredPhotos" của ứng dụng
   - Bạn có thể truy cập ảnh đã khôi phục từ ứng dụng Quản lý File của thiết bị

### Lưu ý quan trọng

- Khả năng tìm thấy ảnh đã xóa phụ thuộc vào:
  - Thời gian đã trôi qua kể từ khi xóa
  - Mức độ sử dụng thiết bị sau khi xóa
  - Phiên bản Android của thiết bị (Android 12+ có chức năng thùng rác tốt hơn)
  
- Để có kết quả tốt nhất:
  - Sử dụng tính năng này ngay sau khi vô tình xóa ảnh
  - Giảm thiểu việc lưu dữ liệu mới sau khi xóa ảnh
  - Tắt WiFi/4G để tránh các ứng dụng tự động tải xuống dữ liệu mới
  - Nếu sử dụng Android 12+, ảnh bị xóa sẽ có khả năng khôi phục cao hơn

## Tính năng khôi phục video

### Mô tả
Tính năng khôi phục video cho phép bạn tìm và khôi phục các video đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục video"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các video đã xóa
3. Sau khi quét xong, chọn các video bạn muốn khôi phục bằng cách nhấn vào video
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các video đã khôi phục sẽ được lưu trong thư mục "RecoveredVideos" của ứng dụng

## Tính năng khôi phục tệp tin

### Mô tả
Tính năng khôi phục tệp tin cho phép bạn tìm và khôi phục các tệp tin khác (PDF, DOC, XLS, v.v.) đã bị xóa trên thiết bị.

### Cách sử dụng
1. Từ màn hình chính, nhấn vào phần "Khôi phục các tệp khác"
2. Nhấn nút "Bắt đầu quét" để tìm kiếm các tệp tin đã xóa
3. Sau khi quét xong, chọn các tệp tin bạn muốn khôi phục bằng cách nhấn vào tệp tin
4. Nhấn nút "Khôi phục đã chọn" để tiến hành khôi phục
5. Các tệp tin đã khôi phục sẽ được lưu trong thư mục "RecoveredFiles" của ứng dụng

## Lưu ý quan trọng
- Ứng dụng cần quyền truy cập bộ nhớ để thực hiện chức năng khôi phục
- Tỷ lệ khôi phục thành công phụ thuộc vào nhiều yếu tố như thời gian xóa, mức độ sử dụng thiết bị sau khi xóa
- Để tăng khả năng khôi phục, hãy sử dụng tính năng này ngay sau khi dữ liệu bị xóa
- Không lưu thêm dữ liệu mới vào thiết bị sau khi xóa dữ liệu để tránh việc ghi đè lên dữ liệu đã xóa

## Quyền truy cập tất cả file

Khi bạn mở ứng dụng lần đầu tiên, sẽ xuất hiện một dialog yêu cầu quyền truy cập tất cả file. Đây là quyền cần thiết để ứng dụng có thể quét và khôi phục các tệp đã bị xóa trong bộ nhớ thiết bị.

### Cách cấp quyền:

1. Khi dialog xuất hiện, chọn "Cấp quyền"
2. Bạn sẽ được chuyển đến màn hình cài đặt của hệ thống
3. Bật công tắc "Cho phép quản lý tất cả tệp"
4. Quay lại ứng dụng

Nếu bạn không cấp quyền, ứng dụng sẽ không thể thực hiện chức năng khôi phục dữ liệu đã xóa.

## Khôi phục ảnh đã xóa thực sự

Tính năng khôi phục ảnh đã xóa giúp bạn tìm và khôi phục các ảnh đã thực sự bị xóa trên thiết bị.

### Cách hoạt động

Khi bạn nhấn nút "Quét", ứng dụng sẽ thực hiện các bước sau:

1. **Quét thùng rác hệ thống** - Trên Android 12 trở lên, ứng dụng sẽ tìm kiếm các ảnh trong thùng rác của hệ thống (MediaStore.Trash). Đây là nơi các ảnh bị xóa gần đây sẽ được