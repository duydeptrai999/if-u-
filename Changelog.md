# Changelog

## [Unreleased]

### Added

- Tích hợp hệ thống lưu trữ kinh nghiệm dự án với cấu trúc thư mục experiences/
- Thêm quy tắc experience-system-workflow.mdc để quản lý việc ghi lại và sử dụng kinh nghiệm
- Tạo template và ví dụ mẫu cho file kinh nghiệm với cấu trúc chuẩn
- Cấu trúc phân loại kinh nghiệm theo frontend, backend, mobile, devops, testing, AI, và common
- Tích hợp hệ thống kinh nghiệm vào các quy trình làm việc hiện có
- Tự động hóa quy trình ghi lại kinh nghiệm sau khi giải quyết vấn đề phức tạp
- Tính năng tạo ngẫu nhiên tính cách AI cho dự án để tăng trải nghiệm thú vị
- Thêm file project-personality-generator.mdc để quản lý các tính cách
- Cập nhật quy trình tạo dự án và nâng cấp dự án để bao gồm bước chọn tính cách
- Hỗ trợ 11 loại tính cách khác nhau với trọng số ưu tiên
- Bổ sung quy trình quản lý resource (icon và rule) vào workflow tạo dự án
- Bổ sung quy trình quản lý resource (icon và rule) vào workflow nâng cấp dự án
- Tích hợp hướng dẫn sử dụng Icon Library API vào quy trình làm việc
- Mô tả chi tiết quy trình đồng bộ hóa Cursor Rules
- Tạo file resource-management.mdc để quản lý tài nguyên trong dự án
- Cập nhật README.md trong thư mục assets/icons để lưu trữ hướng dẫn từ ICON-LIBRARY-API-GUIDE
- Cập nhật .cursorrc để thêm resource-management.mdc vào rules áp dụng tự động
- Bổ sung quy trình sử dụng Supabase MCP trong chế độ nhà phát triển
- Thêm quy tắc kiểm tra và cấu hình .env cho các dự án Supabase
- Tạo file supabase-mcp-workflow.mdc chứa hướng dẫn chi tiết
- Cập nhật workflow dự án mới và nâng cấp dự án để tích hợp Supabase MCP
- Hướng dẫn cài đặt và sử dụng MCP để kiểm tra database changes
- Tích hợp DALL-E API để tạo và chuyển đổi ảnh vector
- Bộ script `scripts/dalle` để tạo ảnh từ prompt, phân tích ảnh, và chuyển đổi thành vector SVG
- Quy trình làm việc mới `dalle-workflow.mdc` cho việc tạo và quản lý ảnh
- Cấu trúc thư mục `assets/icons`, `assets/images`, và `assets/illustrations`
- Cải tiến script DALL-E với tính năng tối ưu prompt tự động cho vector, icon, app icon và UI icon set
- Thêm cảnh báo chi phí sử dụng DALL-E API trước khi tạo ảnh (~0,08$ mỗi ảnh với DALL-E 3)
- Bổ sung tham số để bỏ qua cảnh báo chi phí và lưu prompt đi kèm với ảnh đã tạo
- Cập nhật script analyze_image.js để tối ưu hóa cho việc phân tích và tạo prompt cho ảnh vector
- Cải thiện script vectorize_image.js với xử lý màu sắc thông minh hơn và hỗ trợ nhiều định dạng đầu vào
- Tạo giao diện màn hình chính ứng dụng Easy Recovery với các tính năng khôi phục ảnh, video và tệp tin khác
- Thêm phần hiển thị thống kê cho dữ liệu đã khôi phục
- Cài đặt cấu trúc ứng dụng với kiến trúc MVVM
- Chuẩn bị các tài nguyên cần thiết (icon, drawable)
- Thiết lập hệ thống đa ngôn ngữ (mặc định tiếng Anh)

### Changed

- Cập nhật cách cài đặt Supabase MCP: sử dụng npm global thay vì cài đặt từ GitHub
- Bổ sung các tham số cụ thể khi chạy mcp-server-postgrest
- Nâng cấp quy trình tương tác tích hợp APK
- Cập nhật tài liệu hướng dẫn
- Cập nhật README.md để giới thiệu cấu trúc tài liệu mới và hệ thống kinh nghiệm
- Nâng cấp phiên bản lên 2.0.0 do thay đổi lớn trong cấu trúc tài liệu
- Cải thiện quy trình làm việc để tập trung vào documentation-first approach
- Tích hợp cảnh báo chi phí trong quy trình tạo ảnh DALL-E để tránh chi phí không cần thiết
- Cải thiện UX của các script DALL-E với giao diện dòng lệnh thân thiện và đầy màu sắc
- Nâng cấp dalle-workflow.mdc với hướng dẫn chi tiết về tối ưu prompt cho từng loại ảnh

### Deprecated

- Quy trình làm việc cũ không sử dụng cấu trúc "6 Docs"

### Fixed
- Sửa lỗi các phần tử UI trong màn hình quét (chữ "Đang quét" và thanh tiến trình) bị chồng lên nút "QUÉT" bằng cách thêm khoảng cách 100dp phía trên
- Xóa hiệu ứng và text "Đang quét..." của statusText bị đè lên nút QUÉT trong quá trình quét
- Ẩn dòng text "Nhấn để quét ảnh" bị chồng lên nút QUÉT
- Ẩn thanh tiến trình ngang (progress bar) nhưng vẫn giữ lại text thông báo trong quá trình quét

## [1.0.1] - 2024-03-23

### Added

- Bổ sung quy trình nâng cấp APK từ project Android nguồn
- Hỗ trợ build APK trực tiếp từ project Android với key debug
- Cậi tiến quy trình tích hợp package từ APK nguồn sang APK đích
- Tài liệu hướng dẫn cho quy trình tích hợp mới

### Changed

- Nâng cấp quy trình tương tác tích hợp APK
- Cập nhật tài liệu hướng dẫn

## [2.0.0] - 2024-05-24

### Added

- Cấu trúc tài liệu "6 Docs" mới để giảm thiểu AI hallucination
- Templates cho 6 tài liệu chính (PRD, App Flow, Tech Stack, Frontend Guidelines, Backend Structure, Implementation Plan)
- Quy trình tạo dự án mới (project-creation-workflow.mdc)
- Quy trình nâng cấp dự án (project-upgrade-workflow.mdc)
- Thư mục docs/ với README.md giải thích về cấu trúc mới

### Changed

- Cập nhật README.md để giới thiệu cấu trúc tài liệu mới
- Nâng cấp phiên bản lên 2.0.0 do thay đổi lớn trong cấu trúc tài liệu
- Cải thiện quy trình làm việc để tập trung vào documentation-first approach

### Deprecated

- Quy trình làm việc cũ không sử dụng cấu trúc "6 Docs"

## [0.3.0] - 2023-10-27

### Thêm mới
- Tính năng lưu trữ thông tin tệp tin đã khôi phục trong cơ sở dữ liệu
- Đồng bộ các tệp đã khôi phục từ cả thư mục hệ thống và ứng dụng
- Cập nhật thống kê tự động khi có tệp mới được khôi phục

### Chi tiết triển khai
- Tạo `RecoveredFilesDatabase` để lưu trữ thông tin về các tệp tin đã khôi phục
- Cập nhật các activity khôi phục để lưu thông tin tệp tin vào database
- Cập nhật RecoveredFilesActivity để đọc dữ liệu từ database
- Cập nhật MainActivity để hiển thị thống kê từ database
- Thêm phương thức onResume để cập nhật thống kê khi trở về từ các màn hình khôi phục

## [0.2.0] - 2023-10-25

### Thêm mới
- Tính năng xem danh sách tệp tin đã khôi phục
- Giao diện hiển thị danh sách phân loại (Ảnh, Video, Tệp tin khác)
- Màn hình chi tiết hiển thị từng loại tệp tin đã khôi phục
- Kết nối từ màn hình chính đến màn hình xem tệp tin đã khôi phục

### Chi tiết triển khai
- Tạo `RecoveredFilesActivity` để hiển thị danh sách phân loại các tệp đã khôi phục
- Tạo `RecoveredFilesDetailActivity` để hiển thị chi tiết tệp tin trong mỗi danh mục
- Tạo `RecoveredFileAdapter` để hiển thị danh sách tệp tin trong RecyclerView
- Thêm xử lý sự kiện khi nhấn vào phần "ĐÃ KHÔI PHỤC" ở màn hình chính
- Thêm mô hình dữ liệu `RecoveredFile` để lưu trữ thông tin tệp tin đã khôi phục

## [0.1.0] - 2023-07-01

### Thêm mới
- Tạo giao diện người dùng cho tính năng khôi phục ảnh
- Thêm chức năng quét tìm ảnh đã xóa trên thiết bị
- Thêm khả năng hiển thị và lựa chọn ảnh để khôi phục
- Thêm màn hình thông báo kết quả quét
- Hỗ trợ đa ngôn ngữ (Tiếng Anh và Tiếng Việt)
