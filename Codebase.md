# Codebase Structure

## App Structure

- **Package**: com.example.ifu
- **MainActivity** (Home screen of the Easy Recovery app)
  - onCreate: Initializes the app UI
  - setupUI: Sets up click listeners for recovery option cards
  - updateStatistics: Updates statistics display with recovered data info
  - formatSize: Formats byte sizes to human-readable form (B, KB, MB, GB)
  
- **Package**: com.example.ifu.recovery
- **PhotoRecoveryActivity** (Photo recovery screen)
  - onCreate: Initializes the UI for photo recovery
  - checkPermission: Checks if the app has storage permissions
  - requestPermission: Requests storage permissions from the user
  - scanForDeletedPhotos: Scans device for deleted photos
  - simulateFindingDeletedPhotos: Simulates finding deleted photos (demo)
  - recoverSelectedPhotos: Recovers selected photos
  
- **VideoRecoveryActivity** (Video recovery screen)
  - onCreate: Initializes the UI for video recovery
  - checkPermission: Checks if the app has storage permissions
  - requestPermission: Requests storage permissions from the user
  - scanForDeletedVideos: Scans device for deleted videos
  - simulateFindingDeletedVideos: Simulates finding deleted videos (demo)
  - recoverSelectedVideos: Recovers selected videos
  
- **FileRecoveryActivity** (Other files recovery screen)
  - onCreate: Initializes the UI for file recovery
  - checkPermission: Checks if the app has storage permissions
  - requestPermission: Requests storage permissions from the user
  - scanForDeletedFiles: Scans device for deleted files
  - simulateFindingDeletedFiles: Simulates finding deleted files (demo)
  - recoverSelectedFiles: Recovers selected files

- **Package**: com.example.ifu.model
- **RecoveredFile** (Data class for recovered files)
  - path: Path to the recovered file
  - name: File name
  - size: File size in bytes
  - modifiedDate: Last modified date
  - isSelected: Whether the file is selected for recovery

- **Package**: com.example.ifu.adapter
- **PhotoAdapter** (Adapter for displaying photos in RecyclerView)
  - onBindViewHolder: Binds photo data to view holder
  - notifyItemsRecovered: Updates UI after recovery
  
- **VideoAdapter** (Adapter for displaying videos in RecyclerView)
  - onBindViewHolder: Binds video data to view holder
  - notifyItemsRecovered: Updates UI after recovery
  
- **FileAdapter** (Adapter for displaying other files in RecyclerView)
  - onBindViewHolder: Binds file data to view holder
  - getFileIconResource: Gets the appropriate icon for file type
  - notifyItemsRecovered: Updates UI after recovery

## Resources

### Layouts
- **activity_main.xml**: Main screen layout with recovery options and statistics
- **activity_photo_recovery.xml**: Layout for photo recovery screen
- **activity_video_recovery.xml**: Layout for video recovery screen
- **activity_file_recovery.xml**: Layout for other files recovery screen
- **item_photo.xml**: Layout for photo item in RecyclerView
- **item_video.xml**: Layout for video item in RecyclerView
- **item_file.xml**: Layout for file item in RecyclerView

### Drawables
- **ic_photo.xml**: Vector icon for photo recovery
- **ic_video.xml**: Vector icon for video recovery
- **ic_file.xml**: Vector icon for other files recovery
- **ic_settings.xml**: Vector icon for settings
- **bg_card.xml**: Background drawable for cards
- **progress_bar_colors.xml**: Customized progress bar with gradient colors

### Values
- **strings.xml**: String resources for the app (English)
- **strings.xml (vi)**: Vietnamese translations for the app

## Features

1. Photo Recovery
   - UI for initiating photo scan and recovery
   - Grid display of recoverable photos with thumbnails
   - Selection mechanism for choosing photos to recover
   - Recovery process with progress indication
   - Storage of recovered photos in a dedicated folder

2. Video Recovery
   - UI for initiating video scan and recovery
   - List display of recoverable videos with details
   - Selection mechanism for choosing videos to recover
   - Recovery process with progress indication
   - Storage of recovered videos in a dedicated folder

3. Other Files Recovery
   - UI for initiating file scan and recovery
   - List display of recoverable files with details and icons
   - Selection mechanism for choosing files to recover
   - Recovery process with progress indication
   - Storage of recovered files in a dedicated folder

4. Statistics Display
   - Shows count and size of recovered items
   - Categorizes by photos, videos, and other files
   - Visual progress indicator

## UI Components

- **Cards**: Used for recovery options and statistics display
- **RecyclerViews**: For displaying lists of recoverable items
- **Checkboxes**: For selecting items to recover
- **Progress Bar**: For indicating scan and recovery progress
- **Buttons**: For initiating actions
- **Text Views**: For displaying information

## Danh Sách Tệp Tin Đã Khôi Phục

### Module/Package: com.htnguyen.ifu
- `RecoveredFilesActivity` (Hiển thị danh sách phân loại các tệp đã khôi phục)
  setupActionBar, setupCategoryViews, setupCategoryItem, getRecoveredPhotoFiles, getRecoveredVideoFiles, getRecoveredOtherFiles, calculateTotalSize, formatSize

- `RecoveredFilesDetailActivity` (Hiển thị chi tiết danh sách tệp tin đã khôi phục theo loại)
  setupViews, setupRecyclerView, loadRecoveredFiles, getRecoveredPhotoFiles, getRecoveredVideoFiles, getRecoveredOtherFiles

### Module/Package: com.htnguyen.ifu.adapter
- `RecoveredFileAdapter` (Hiển thị danh sách các tệp tin đã khôi phục)
  updateFiles, onCreateViewHolder, onBindViewHolder, getItemCount, FileViewHolder.bind, isImageFile, isVideoFile, formatSize

### Module/Package: com.htnguyen.ifu.model
- `RecoveredFile` (Mô hình dữ liệu cho tệp tin đã khôi phục)
  path, name, size, modifiedDate, isSelected

## Cơ sở dữ liệu

### Module/Package: com.htnguyen.ifu.db
- `RecoveredFilesDatabase` (Lưu trữ thông tin về các tệp tin đã khôi phục)
  onCreate, onUpgrade, addRecoveredFile, getRecoveredFiles, countRecoveredFiles, getTotalSize, fileExists, deleteFile, cursorToRecoveredFile

## Adapter

### RecoveredFileAdapter
- **Chức năng**: Hiển thị danh sách file đã khôi phục với thumbnail và thông tin chi tiết
- **Các phương thức chính**:
  - `updateFiles`: Cập nhật danh sách file
  - `bind`: Hiển thị thông tin file
  - `loadThumbnail`: Tải và hiển thị thumbnail cho ảnh và video
  - `openFile`: Mở file đã khôi phục với ứng dụng mặc định
  - `onDestroy`: Thu hồi tài nguyên khi adapter không còn được sử dụng

## Activity

### RecoveredFilesDetailActivity
- **Chức năng**: Hiển thị danh sách chi tiết các file đã khôi phục theo loại
- **Các phương thức chính**:
  - `setupViews`: Thiết lập các thành phần giao diện
  - `setupRecyclerView`: Khởi tạo RecyclerView và adapter
  - `loadRecoveredFiles`: Tải danh sách file từ database
  - `onDestroy`: Thu hồi tài nguyên adapter khi activity bị hủy
