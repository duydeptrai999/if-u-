# Module Giao Diện Người Dùng (UI)

## Tổng quan

Module này định nghĩa toàn bộ giao diện người dùng của ứng dụng "Easy Recovery", sử dụng Jetpack Compose và Material Design 3. Tài liệu này hướng dẫn triển khai các màn hình và thành phần UI chính dựa trên thiết kế đã được phê duyệt.

## Thành phần chính

### 1. HomeScreen

**Mô tả**: Màn hình chính của ứng dụng hiển thị 3 tùy chọn khôi phục và trạng thái đã khôi phục.

**Yêu cầu chức năng**:
- Header với tiêu đề "Easy Recovery" và icon cài đặt
- 3 card tùy chọn khôi phục:
  * Khôi phục ảnh (hiển thị icon hình ảnh)
  * Khôi phục video (hiển thị icon video)
  * Khôi phục tệp tin khác (hiển thị icon thư mục)
- Section "ĐÃ KHÔI PHỤC" hiển thị:
  * Tổng dung lượng đã khôi phục (GB)
  * Tổng số file đã khôi phục
  * Progress bar tổng hợp
  * 3 tab thống kê: Ảnh, Video, Khác
  * Mỗi tab hiển thị số tệp và dung lượng
- Banner quảng cáo ở cuối màn hình

**Triển khai**:
```kotlin
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        HomeHeader()
        RecoveryOptions(navController)
        Spacer(modifier = Modifier.height(24.dp))
        RecoveryStats(viewModel.recoveryStats)
        Spacer(modifier = Modifier.weight(1f))
        AdBanner()
    }
}
```

### 2. ScanScreen

**Mô tả**: Màn hình quét và hiển thị kết quả tìm kiếm file có thể khôi phục.

**Yêu cầu chức năng**:
- Header với nút quay lại và tiêu đề dựa theo loại quét (Ảnh/Video/Khác)
- Hiệu ứng quét trực quan (animation)
- Hiển thị danh sách file được tìm thấy:
  * Thumbnail cho ảnh và video
  * Icon phù hợp cho các loại file khác
  * Thông tin kích thước và ngày xóa
  * Chỉ số khả năng khôi phục thành công
- Nút lọc và sắp xếp kết quả
- Chế độ xem dạng lưới (grid) và danh sách (list)
- Nút "Khôi phục" ở cuối màn hình

**Triển khai**:
```kotlin
@Composable
fun ScanScreen(
    fileType: FileType,
    navController: NavController,
    viewModel: ScanViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ScanHeader(fileType, onBackClick = { navController.popBackStack() })
        
        if (viewModel.isScanning) {
            ScanningAnimation(progress = viewModel.scanProgress)
        }
        
        FileList(
            files = viewModel.discoveredFiles,
            viewMode = viewModel.viewMode,
            onFileClick = { file -> navController.navigate("fileDetail/${file.id}") },
            onSelectionChange = { file, selected -> viewModel.updateSelection(file, selected) }
        )
        
        RecoveryButton(
            selectedCount = viewModel.selectedFiles.size,
            onClick = { navController.navigate("recovery") }
        )
    }
}
```

### 3. FileDetailScreen

**Mô tả**: Màn hình hiển thị chi tiết của một file trước khi khôi phục.

**Yêu cầu chức năng**:
- Preview đầy đủ kích thước (nếu là ảnh/video)
- Các thông tin chi tiết:
  * Tên file
  * Kích thước
  * Định dạng
  * Ngày xóa
  * Vị trí gốc
  * Khả năng khôi phục (%)
- Nút khôi phục
- Nút chia sẻ (cho phiên bản premium)

**Triển khai**:
```kotlin
@Composable
fun FileDetailScreen(
    fileId: String,
    navController: NavController,
    viewModel: FileDetailViewModel
) {
    val file = viewModel.getFileById(fileId)
    
    Column(modifier = Modifier.fillMaxSize()) {
        DetailHeader(
            title = file.name,
            onBackClick = { navController.popBackStack() }
        )
        
        FilePreview(file)
        
        FileMetadata(file)
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RecoveryButton(
                onClick = { viewModel.recoverFile(file) }
            )
            
            if (viewModel.isPremiumUser) {
                ShareButton(
                    onClick = { viewModel.shareFile(file) }
                )
            }
        }
    }
}
```

### 4. RecoveryScreen

**Mô tả**: Màn hình quá trình khôi phục và kết quả.

**Yêu cầu chức năng**:
- Hiển thị progress của quá trình khôi phục
- Danh sách file đang được khôi phục
- Thông báo kết quả sau khi hoàn tất
- Tùy chọn vị trí lưu file
- Hiển thị quảng cáo interstitial sau khi khôi phục (cho phiên bản free)

**Triển khai**:
```kotlin
@Composable
fun RecoveryScreen(
    navController: NavController,
    viewModel: RecoveryViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        RecoveryHeader(
            onCancelClick = { viewModel.cancelRecovery() }
        )
        
        RecoveryProgress(
            progress = viewModel.recoveryProgress,
            currentFile = viewModel.currentFile,
            totalFiles = viewModel.totalFiles
        )
        
        if (viewModel.isCompleted) {
            RecoveryResults(
                successCount = viewModel.successCount,
                failedCount = viewModel.failedCount,
                onDoneClick = {
                    viewModel.showAdIfNeeded()
                    navController.navigate("home") 
                }
            )
        }
        
        RecoveryFileList(
            files = viewModel.files,
            recoveredFiles = viewModel.recoveredFiles
        )
    }
}
```

### 5. SettingsScreen

**Mô tả**: Màn hình cài đặt ứng dụng.

**Yêu cầu chức năng**:
- Chuyển đổi theme (Light/Dark)
- Tùy chọn định dạng file ưu tiên
- Cài đặt vị trí lưu mặc định
- Tùy chọn ngôn ngữ
- Thông tin phiên bản
- Nút mua premium
- Chính sách bảo mật và điều khoản sử dụng

**Triển khai**:
```kotlin
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SettingsHeader(
            onBackClick = { navController.popBackStack() }
        )
        
        SettingsGroup(title = "Giao diện") {
            ThemeSetting(
                isDarkMode = viewModel.isDarkMode,
                onThemeChange = { viewModel.updateTheme(it) }
            )
            
            LanguageSetting(
                currentLanguage = viewModel.language,
                onLanguageChange = { viewModel.updateLanguage(it) }
            )
        }
        
        SettingsGroup(title = "Khôi phục") {
            StorageLocationSetting(
                currentLocation = viewModel.storageLocation,
                onLocationChange = { viewModel.updateStorageLocation(it) }
            )
            
            PriorityFileTypesSetting(
                selectedTypes = viewModel.priorityFileTypes,
                onSelectionChange = { viewModel.updatePriorityFileTypes(it) }
            )
        }
        
        SettingsGroup(title = "Premium") {
            PremiumButton(
                isPremium = viewModel.isPremium,
                onPremiumClick = { navController.navigate("premium") }
            )
        }
        
        SettingsGroup(title = "Thông tin") {
            AboutSetting(
                version = viewModel.appVersion,
                onPrivacyClick = { navController.navigate("privacy") },
                onTermsClick = { navController.navigate("terms") }
            )
        }
    }
}
```

## Thiết kế thành phần chung

### 1. Card tùy chọn khôi phục

**Mô tả**: Card hiển thị tùy chọn khôi phục trong màn hình chính.

**Yêu cầu**:
- Nền trắng với viền bo tròn
- Icon ở giữa (xanh dương cho ảnh, đỏ cho video, vàng cho tệp khác)
- Tên tùy chọn ở dưới (căn giữa)
- Hiệu ứng khi nhấn

**Triển khai**:
```kotlin
@Composable
fun RecoveryOptionCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
            .padding(8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
```

### 2. Section thống kê khôi phục

**Mô tả**: Phần hiển thị thống kê khôi phục.

**Yêu cầu**:
- Tiêu đề "ĐÃ KHÔI PHỤC" in đậm
- Hiển thị dung lượng và số file đã khôi phục
- Progress bar nhiều màu (xanh cho ảnh, đỏ cho video, vàng cho tệp khác)
- 3 tab thống kê chi tiết
- Mỗi tab hiển thị số lượng tệp và dung lượng

**Triển khai**:
```kotlin
@Composable
fun RecoveryStats(
    stats: RecoveryStats,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "ĐÃ KHÔI PHỤC",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${stats.totalSize} GB",
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = "${stats.totalFiles} file",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        MultiColorProgressBar(
            imagePercent = stats.imagePercent,
            videoPercent = stats.videoPercent,
            otherPercent = stats.otherPercent
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        RecoveryStatsTabs(stats)
    }
}
```

### 3. Banner quảng cáo

**Mô tả**: Banner quảng cáo ở cuối màn hình.

**Yêu cầu**:
- Thiết kế hiển thị rõ là quảng cáo
- Icon ứng dụng bên trái
- Tiêu đề và mô tả
- Nút "CÀI ĐẶT" nổi bật

**Triển khai**:
```kotlin
@Composable
fun AdBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = "App Icon",
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Phục hồi ảnh và dữ liệu đã xóa",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Your recovery photo Assistant Phone Data transfer lets you transfer apps, Recover...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Button(
                onClick = { /* TODO: Handle install button click */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green
                ),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = "CÀI ĐẶT",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
```

## Palette màu sắc

Sử dụng palette màu sau cho toàn bộ ứng dụng:

```kotlin
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2196F3),          // Xanh dương chính
    secondary = Color(0xFFFF4081),        // Hồng đậm
    tertiary = Color(0xFFFFB74D),         // Cam nhạt
    background = Color(0xFFF5F5F5),       // Xám nhạt
    surface = Color.White,                // Trắng
    onPrimary = Color.White,              // Trắng trên nền xanh
    onSecondary = Color.White,            // Trắng trên nền hồng
    onTertiary = Color.Black,             // Đen trên nền cam
    onBackground = Color(0xFF212121),     // Đen trên nền xám
    onSurface = Color(0xFF212121),        // Đen trên nền trắng
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),          // Xanh dương nhạt
    secondary = Color(0xFFF48FB1),        // Hồng nhạt
    tertiary = Color(0xFFFFCC80),         // Cam rất nhạt
    background = Color(0xFF121212),       // Đen đậm
    surface = Color(0xFF1E1E1E),          // Đen nhạt
    onPrimary = Color.Black,              // Đen trên nền xanh nhạt
    onSecondary = Color.Black,            // Đen trên nền hồng nhạt
    onTertiary = Color.Black,             // Đen trên nền cam nhạt
    onBackground = Color.White,           // Trắng trên nền đen
    onSurface = Color.White,              // Trắng trên nền đen nhạt
)

// Màu sắc cho loại file cụ thể
val ImageColor = Color(0xFF2196F3)        // Xanh dương
val VideoColor = Color(0xFFF44336)        // Đỏ
val OtherFileColor = Color(0xFFFFB74D)    // Vàng cam
```

## Typography

Sử dụng typography sau:

```kotlin
val AppTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
```

## Responsive design

**Nguyên tắc**:
- Sử dụng Modifier.fillMaxWidth() và Modifier.fillMaxHeight() thay vì kích thước cố định
- Sử dụng Modifier.weight() để phân bố không gian
- Sử dụng WindowSizeClass để điều chỉnh layout cho các kích cỡ màn hình khác nhau
- Sử dụng tỷ lệ phần trăm thay vì giá trị cố định khi có thể

**Triển khai**:
```kotlin
@Composable
fun ResponsiveHomeScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
    viewModel: HomeViewModel
) {
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            // Phone layout - vertical
            HomeScreen(navController, viewModel)
        }
        WindowWidthSizeClass.Medium -> {
            // Tablet layout - adjustable
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    HomeHeader()
                    RecoveryOptions(navController)
                }
                
                Column(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    RecoveryStats(viewModel.recoveryStats)
                    Spacer(modifier = Modifier.weight(1f))
                    AdBanner()
                }
            }
        }
        else -> {
            // Large tablet/desktop - horizontal with more detail
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    HomeHeader()
                    RecoveryOptions(navController)
                    Spacer(modifier = Modifier.weight(1f))
                    AdBanner()
                }
                
                Column(
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    RecoveryStats(viewModel.recoveryStats)
                    RecentActivities(viewModel.recentActivities)
                }
            }
        }
    }
}
```

## Hiệu ứng & Animations

**Mô tả**: Các hiệu ứng và animation làm ứng dụng mượt mà và hiện đại.

**Danh sách animations**:
- Hiệu ứng quét (scanning animation)
- Transition giữa các màn hình
- Loading progress animation
- Hiệu ứng nút nhấn
- Hiệu ứng thông báo (snackbar, toast)

**Triển khai scanning animation**:
```kotlin
@Composable
fun ScanningAnimation(progress: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val animation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanning"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Scanning line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .offset(y = animation * 200.dp)
                .background(MaterialTheme.colorScheme.primary)
        )
        
        // Files found animation
        FilesFoundAnimation(progress)
        
        // Progress text
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
```

## Accessibility

**Mô tả**: Hướng dẫn đảm bảo ứng dụng tiếp cận được với mọi người dùng.

**Yêu cầu**:
- Sử dụng semantics API để cung cấp thông tin cho screen readers
- Đảm bảo tương phản màu sắc đủ cao
- Hỗ trợ phóng to văn bản
- Cung cấp mô tả có ý nghĩa cho tất cả clickable elements
- Hỗ trợ điều hướng bằng bàn phím

**Triển khai semantics**:
```kotlin
@Composable
fun AccessibleRecoveryButton(
    selectedCount: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .semantics {
                contentDescription = "Khôi phục $selectedCount file đã chọn"
                role = Role.Button
            }
    ) {
        Text(
            text = if (selectedCount > 0) 
                   "Khôi phục ($selectedCount)" 
                   else "Khôi phục",
            style = MaterialTheme.typography.titleMedium
        )
    }
}
```

## Nguyên tắc testing UI

- Viết UI tests sử dụng Compose testing library
- Test mỗi màn hình với các trạng thái khác nhau
- Kiểm tra khả năng hiển thị của các thành phần chính
- Kiểm tra các interactions (clicks, scrolls, etc.)
- Kiểm tra các edge cases (danh sách trống, lỗi kết nối, etc.)

**Mẫu test**:
```kotlin
@Test
fun homeScreen_displaysAllRecoveryOptions() {
    composeTestRule.setContent {
        AppTheme {
            HomeScreen(
                navController = rememberNavController(),
                viewModel = FakeHomeViewModel()
            )
        }
    }
    
    composeTestRule.onNodeWithText("Khôi phục ảnh").assertIsDisplayed()
    composeTestRule.onNodeWithText("Khôi phục video").assertIsDisplayed()
    composeTestRule.onNodeWithText("Khôi phục tệp tin khác").assertIsDisplayed()
}
```

## Ghi chú triển khai

1. Sử dụng ViewModel để tách logic khỏi UI
2. Sử dụng Material 3 theo mặc định
3. Áp dụng thiết kế responsive cho tất cả màn hình
4. Đảm bảo hiệu suất tốt, tránh recomposition không cần thiết
5. Sử dụng lazy components cho danh sách lớn
6. Tích hợp màn hình với các module khác (scan, recovery)
7. Tuân thủ nguyên tắc thiết kế Material Design
8. Đảm bảo tất cả màn hình có dark mode tương ứng
9. Ưu tiên sử dụng các composable có thể tái sử dụng
10. Cung cấp feedback trực quan cho tất cả hành động người dùng 