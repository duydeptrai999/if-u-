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
- Hiển thị thumbnail cho ảnh và video trong màn hình danh sách file đã khôi phục
- Thêm tính năng xem trực tiếp file đã khôi phục khi nhấn vào từng mục trong danh sách
- Thêm FileViewerActivity để hiển thị ảnh và video trực tiếp trong ứng dụng khi không có ứng dụng bên ngoài phù hợp
- Cải thiện xử lý lỗi khi mở file và thông báo chi tiết cho người dùng
- Thêm tính năng chia sẻ ảnh và video từ màn hình xem file
- Thêm chế độ chọn nhiều file trong màn hình danh sách file đã khôi phục
- Thêm tính năng chia sẻ nhiều file cùng lúc từ màn hình danh sách file đã khôi phục
- Thêm tính năng chọn ngôn ngữ khi mở app lần đầu
- Tạo màn hình chọn ngôn ngữ khi người dùng mở ứng dụng lần đầu tiên
- Hỗ trợ hai ngôn ngữ: tiếng Anh và tiếng Việt
- Cải thiện hệ thống quản lý ngôn ngữ với BaseActivity và MyApplication
- Thêm tính năng thay đổi ngôn ngữ trong cài đặt
- Đảm bảo cơ chế thay đổi ngôn ngữ mượt mà và nhất quán

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
- Cập nhật giao diện màn hình danh sách file đã khôi phục để hỗ trợ chế độ chọn nhiều
- Cải thiện cách xử lý loại MIME khi chia sẻ file

### Deprecated

- Quy trình làm việc cũ không sử dụng cấu trúc "6 Docs"

### Fixed
- Sửa lỗi các phần tử UI trong màn hình quét (chữ "Đang quét" và thanh tiến trình) bị chồng lên nút "QUÉT" bằng cách thêm khoảng cách 100dp phía trên
- Xóa hiệu ứng và text "Đang quét..." của statusText bị đè lên nút QUÉT trong quá trình quét
- Ẩn dòng text "Nhấn để quét ảnh" bị chồng lên nút QUÉT
- Ẩn thanh tiến trình ngang (progress bar) nhưng vẫn giữ lại text thông báo trong quá trình quét
- Cải thiện giao diện thông báo khôi phục thành công với 2 button "CONTINUE" và "XEM ẢNH ĐÃ KHÔI PHỤC" cùng kích thước và style
- Làm đẹp hơn giao diện thông báo với padding, kích thước icon và text lớn hơn
- Giảm kích thước button khôi phục thành công từ 56dp xuống 48dp và giảm cỡ chữ từ 16sp xuống 14sp để làm gọn giao diện
- Xóa các ký tự lạ (%1$d, %2$d) trong chuỗi thông báo khôi phục thành công và thay bằng thông báo đơn giản
- Áp dụng cải thiện tương tự cho màn hình thông báo khôi phục video và file thành công để đảm bảo giao diện nhất quán
- Sửa lỗi không thể mở file ảnh và video khi nhấn vào từ danh sách đã khôi phục
- Cải thiện xác định MIME type để mở file với ứng dụng phù hợp hơn
- Thêm phương pháp thay thế để xem file khi không thể mở bằng ứng dụng bên ngoài
- Sửa lỗi ID không khớp trong RecoveredFilesDetailActivity (tvSelectedCount, ivShareSelected, ivCloseSelection)
- Thêm nút đóng chế độ chọn trong thanh công cụ chọn nhiều
- Sửa lỗi ClassCastException khi cố gắng ép kiểu MaterialButton thành ImageView cho shareSelectedButton
- Sửa lỗi nút quét trong tính năng khôi phục ảnh (PhotoRecoveryActivity) bị miss sự kiện và thực hiện lại việc quét khi nhấn nhiều lần. Thêm biến isScanning để kiểm soát trạng thái quét và ngăn không cho người dùng quét lại khi đang trong quá trình quét.

## [1.0.2] - 2024-04-11

### Improved
- Nâng cấp phần intro ứng dụng với các cải tiến tương tác và trải nghiệm người dùng:
  - Thêm Lottie Animation thay thế cho các icon tĩnh
  - Thêm tính năng vuốt xuống để bỏ qua intro
  - Thêm phản hồi xúc giác (haptic feedback) khi tương tác
  - Cải thiện hiệu ứng gradient background với animation xoay
  - Thêm tính năng tương tác với animation (chạm để tăng tốc)
  - Thêm gợi ý thông minh cho tính năng vuốt xuống sau 10 giây

## [1.0.1] - 2024-04-10

### Fixed
- Sửa lỗi luồng điều hướng: Khi người dùng chọn ngôn ngữ và ấn next, ứng dụng chuyển trực tiếp đến màn hình intro thay vì vào main activity
- Đổi tên phương thức từ `startMainActivity()` thành `startIntroActivity()` trong LanguageSelectionActivity để rõ nghĩa hơn

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

## [Phiên bản đang phát triển]

### Cải tiến
- Nâng cấp animation cho phần intro app:
  - Thêm hiệu ứng page transformer với animation chuyển trang đẹp mắt (phóng to/thu nhỏ, xoay, parallax)
  - Thêm animation cho các phần tử trong slide (hiệu ứng xuất hiện tuần tự)
  - Cải thiện indicator với hiệu ứng "worm" di chuyển mượt mà
  - Thêm nền gradient động cho từng slide
  - Nâng cấp giao diện nút với các hiệu ứng nhấn và chuyển đổi
  - Thêm hiệu ứng chuyển đổi khi đến slide cuối (Next -> Start)
  - Nâng cấp thiết kế tổng thể với card view và bóng đổ

### Thêm
- Tính năng Intro App hiển thị sau khi chọn ngôn ngữ
  - Tạo 4 màn hình giới thiệu với nội dung và hình ảnh
  - Thêm hiệu ứng chuyển trang và chỉ báo vị trí
  - Lưu trạng thái để không hiển thị lại intro sau lần đầu
  - Hỗ trợ đa ngôn ngữ (tiếng Anh, tiếng Việt)
  - Các nút bỏ qua và tiếp tục/bắt đầu
  - Tích hợp với luồng điều hướng ứng dụng
