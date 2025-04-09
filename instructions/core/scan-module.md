# Module quét & phát hiện file đã xóa

## Tổng quan

Module này chịu trách nhiệm quét thiết bị để tìm các file đã bị xóa có khả năng phục hồi, xác định loại file, và cung cấp thông tin sơ bộ về khả năng phục hồi thành công. Đây là module cốt lõi đầu tiên của ứng dụng.

## Thành phần chính

### 1. ScanEngine

Đây là thành phần trung tâm, chịu trách nhiệm điều phối quá trình quét và phân tích file.

```kotlin
package com.example.filerecovery.domain.engine

import com.example.filerecovery.domain.model.DeletedFile
import com.example.filerecovery.domain.model.ScanOptions
import com.example.filerecovery.domain.model.ScanProgress
import com.example.filerecovery.domain.model.ScanResult
import kotlinx.coroutines.flow.Flow

interface ScanEngine {
    /**
     * Quét thiết bị để tìm các file đã xóa
     * @param options Tùy chọn quét (loại file, vị trí, v.v.)
     * @return Flow chứa tiến trình quét và danh sách file đã xóa
     */
    suspend fun scan(options: ScanOptions): Flow<ScanProgress>
    
    /**
     * Hủy quá trình quét hiện tại
     */
    suspend fun cancelScan()
    
    /**
     * Kiểm tra khả năng quét thiết bị
     * @return true nếu có thể quét, false nếu không
     */
    suspend fun canScan(): Boolean
}
```

### 2. FileAnalyzer

Phân tích các file đã xóa để xác định loại file, khả năng khôi phục, và trích xuất metadata.

```kotlin
package com.example.filerecovery.domain.analyzer

import com.example.filerecovery.domain.model.DeletedFile
import com.example.filerecovery.domain.model.FileType
import com.example.filerecovery.domain.model.RecoveryProbability

interface FileAnalyzer {
    /**
     * Phân tích file đã xóa để xác định thông tin
     * @param rawData Dữ liệu thô của file
     * @param path Đường dẫn gốc của file (nếu có)
     * @return Thông tin về file đã xóa
     */
    suspend fun analyze(rawData: ByteArray, path: String?): DeletedFile
    
    /**
     * Xác định loại file dựa trên signature
     * @param rawData Dữ liệu thô của file
     * @return Loại file
     */
    suspend fun detectFileType(rawData: ByteArray): FileType
    
    /**
     * Đánh giá khả năng khôi phục thành công
     * @param rawData Dữ liệu thô của file
     * @return Xác suất khôi phục thành công
     */
    suspend fun evaluateRecoveryProbability(rawData: ByteArray): RecoveryProbability
    
    /**
     * Tạo thumbnail cho file (ảnh/video)
     * @param rawData Dữ liệu thô của file
     * @param fileType Loại file
     * @return Thumbnail dưới dạng ByteArray, null nếu không tạo được
     */
    suspend fun generateThumbnail(rawData: ByteArray, fileType: FileType): ByteArray?
}
```

### 3. MediaScanner

Chịu trách nhiệm quét các đường dẫn cụ thể trên thiết bị, tập trung vào các file media.

```kotlin
package com.example.filerecovery.domain.scanner

import com.example.filerecovery.domain.model.ScanLocation
import com.example.filerecovery.domain.model.ScanProgress
import kotlinx.coroutines.flow.Flow

interface MediaScanner {
    /**
     * Quét vị trí cụ thể để tìm file media đã xóa
     * @param location Vị trí quét
     * @return Flow chứa tiến trình quét
     */
    suspend fun scanLocation(location: ScanLocation): Flow<ScanProgress>
    
    /**
     * Lấy danh sách tất cả vị trí có thể quét
     * @return Danh sách vị trí có thể quét
     */
    suspend fun getAvailableLocations(): List<ScanLocation>
}
```

## Model classes

### DeletedFile

```kotlin
package com.example.filerecovery.domain.model

import java.util.Date

data class DeletedFile(
    val id: String,                          // ID duy nhất của file
    val originalPath: String?,               // Đường dẫn gốc nếu có thể xác định
    val name: String?,                       // Tên file nếu có thể xác định
    val fileType: FileType,                  // Loại file
    val size: Long,                          // Kích thước theo byte
    val deletionDate: Date?,                 // Ngày xóa nếu có thể xác định
    val creationDate: Date?,                 // Ngày tạo nếu có thể xác định
    val modificationDate: Date?,             // Ngày chỉnh sửa nếu có thể xác định
    val thumbnailBytes: ByteArray?,          // Dữ liệu thumbnail (cho ảnh/video)
    val recoveryProbability: RecoveryProbability, // Khả năng khôi phục
    val metadata: Map<String, Any>           // Metadata bổ sung
)
```

### FileType

```kotlin
package com.example.filerecovery.domain.model

enum class FileType {
    // Ảnh & Video
    JPEG, PNG, GIF, WEBP, BMP,
    MP4, MOV, AVI, MKV, WEBM,
    
    // Media âm thanh
    MP3, WAV, AAC, OGG, FLAC,
    
    // Tài liệu
    PDF, DOC, DOCX, XLS, XLSX,
    PPT, PPTX, TXT, CSV, RTF,
    
    // Khác
    ZIP, RAR, APK, EXE,
    UNKNOWN
}
```

### RecoveryProbability

```kotlin
package com.example.filerecovery.domain.model

enum class RecoveryProbability {
    HIGH,       // >80% khả năng khôi phục thành công
    MEDIUM,     // 40-80% khả năng khôi phục thành công
    LOW,        // <40% khả năng khôi phục thành công
    UNKNOWN     // Không thể đánh giá
}
```

### ScanOptions

```kotlin
package com.example.filerecovery.domain.model

data class ScanOptions(
    val fileTypes: Set<FileType>? = null,        // Loại file cần quét, null là tất cả
    val locations: List<ScanLocation>? = null,   // Vị trí quét, null là tất cả
    val minFileSize: Long? = null,               // Kích thước tối thiểu, null là không giới hạn
    val maxFileSize: Long? = null,               // Kích thước tối đa, null là không giới hạn
    val deletedAfter: Date? = null,              // Chỉ quét file bị xóa sau ngày này
    val deletedBefore: Date? = null,             // Chỉ quét file bị xóa trước ngày này
    val deepScan: Boolean = false                // Quét sâu hoặc quét nhanh
)
```

### ScanLocation

```kotlin
package com.example.filerecovery.domain.model

data class ScanLocation(
    val id: String,                  // ID duy nhất của vị trí
    val path: String,                // Đường dẫn đầy đủ
    val displayName: String,         // Tên hiển thị
    val type: LocationType,          // Loại vị trí
    val isAvailable: Boolean,        // Có thể truy cập không
    val totalSpace: Long?,           // Tổng dung lượng
    val freeSpace: Long?             // Dung lượng trống
)

enum class LocationType {
    INTERNAL_STORAGE,
    SD_CARD,
    USB_STORAGE,
    CLOUD_STORAGE
}
```

### ScanProgress

```kotlin
package com.example.filerecovery.domain.model

data class ScanProgress(
    val isComplete: Boolean,                 // Đã hoàn thành chưa
    val currentLocation: ScanLocation?,      // Vị trí đang quét
    val progress: Float,                     // Tiến độ từ 0.0 - 1.0
    val filesFound: Int,                     // Số file đã tìm thấy
    val currentFile: String?,                // File đang quét
    val elapsedTimeMs: Long,                 // Thời gian đã trôi qua (ms)
    val estimatedTimeRemainingMs: Long?,     // Ước tính thời gian còn lại (ms)
    val foundFiles: List<DeletedFile>        // Danh sách file đã tìm thấy
)
```

## Triển khai các thành phần

### 1. ScanEngineImpl

```kotlin
package com.example.filerecovery.data.engine

import com.example.filerecovery.domain.analyzer.FileAnalyzer
import com.example.filerecovery.domain.engine.ScanEngine
import com.example.filerecovery.domain.model.*
import com.example.filerecovery.domain.scanner.MediaScanner
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

class ScanEngineImpl @Inject constructor(
    private val mediaScanner: MediaScanner,
    private val fileAnalyzer: FileAnalyzer
) : ScanEngine {

    private var scanJob: Job? = null

    override suspend fun scan(options: ScanOptions): Flow<ScanProgress> = flow {
        var startTime = System.currentTimeMillis()
        var foundFiles = mutableListOf<DeletedFile>()
        var isComplete = false

        // 1. Xác định các vị trí cần quét
        val locationsToScan = options.locations 
            ?: mediaScanner.getAvailableLocations()
                .filter { it.isAvailable }

        // 2. Quét từng vị trí
        locationsToScan.forEachIndexed { index, location ->
            // Kiểm tra nếu quá trình quét bị hủy
            if (!isActive) {
                return@flow
            }

            // Theo dõi tiến trình quét cho mỗi vị trí
            mediaScanner.scanLocation(location)
                .collect { progress ->
                    // Tính toán tiến độ tổng thể
                    val overallProgress = (index + progress.progress) / locationsToScan.size
                    
                    // Phân tích và xử lý mỗi file được tìm thấy
                    val newFiles = progress.foundFiles.filter { 
                        it !in foundFiles && matchesOptions(it, options)
                    }
                    
                    foundFiles.addAll(newFiles)
                    
                    // Báo cáo tiến độ
                    emit(
                        ScanProgress(
                            isComplete = false,
                            currentLocation = location,
                            progress = overallProgress,
                            filesFound = foundFiles.size,
                            currentFile = progress.currentFile,
                            elapsedTimeMs = System.currentTimeMillis() - startTime,
                            estimatedTimeRemainingMs = estimateRemainingTime(
                                startTime, 
                                System.currentTimeMillis(), 
                                overallProgress
                            ),
                            foundFiles = foundFiles
                        )
                    )
                }
        }

        // 3. Thông báo hoàn thành
        isComplete = true
        emit(
            ScanProgress(
                isComplete = true,
                currentLocation = null,
                progress = 1.0f,
                filesFound = foundFiles.size,
                currentFile = null,
                elapsedTimeMs = System.currentTimeMillis() - startTime,
                estimatedTimeRemainingMs = 0,
                foundFiles = foundFiles
            )
        )
    }.cancellable().also { scanJob = currentCoroutineContext()[Job] }

    override suspend fun cancelScan() {
        scanJob?.cancel()
    }

    override suspend fun canScan(): Boolean {
        return mediaScanner.getAvailableLocations().any { it.isAvailable }
    }

    private fun matchesOptions(file: DeletedFile, options: ScanOptions): Boolean {
        // Kiểm tra các điều kiện lọc
        if (options.fileTypes != null && file.fileType !in options.fileTypes) {
            return false
        }
        
        if (options.minFileSize != null && file.size < options.minFileSize) {
            return false
        }
        
        if (options.maxFileSize != null && file.size > options.maxFileSize) {
            return false
        }
        
        if (options.deletedAfter != null && file.deletionDate != null && 
            file.deletionDate.before(options.deletedAfter)) {
            return false
        }
        
        if (options.deletedBefore != null && file.deletionDate != null && 
            file.deletionDate.after(options.deletedBefore)) {
            return false
        }
        
        return true
    }

    private fun estimateRemainingTime(startTime: Long, currentTime: Long, progress: Float): Long? {
        if (progress <= 0.0f) return null
        
        val elapsedTime = currentTime - startTime
        val estimatedTotalTime = elapsedTime / progress
        return (estimatedTotalTime - elapsedTime).toLong()
    }
}
```

### 2. MediaScannerImpl

```kotlin
package com.example.filerecovery.data.scanner

import android.content.Context
import android.os.Environment
import android.os.StatFs
import com.example.filerecovery.domain.analyzer.FileAnalyzer
import com.example.filerecovery.domain.model.*
import com.example.filerecovery.domain.scanner.MediaScanner
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class MediaScannerImpl @Inject constructor(
    private val context: Context,
    private val fileAnalyzer: FileAnalyzer
) : MediaScanner {

    override suspend fun scanLocation(location: ScanLocation): Flow<ScanProgress> = flow {
        val startTime = System.currentTimeMillis()
        val foundFiles = mutableListOf<DeletedFile>()
        
        // Mô phỏng quá trình quét - trong ứng dụng thực tế, đây sẽ là quá trình phức tạp
        // sử dụng native code hoặc thư viện khôi phục dữ liệu
        
        // Giả lập tìm kiếm file bị xóa
        val totalFiles = 100 // giả định 100 file để demo
        for (i in 1..totalFiles) {
            delay(50) // giả lập việc xử lý mỗi file
            
            if (i % 10 == 0) {
                // Tạo một file giả định đã tìm thấy
                val mockRawData = ByteArray(1024) // dữ liệu giả
                val deletedFile = fileAnalyzer.analyze(mockRawData, "${location.path}/file_$i.jpg")
                foundFiles.add(deletedFile)
                
                emit(
                    ScanProgress(
                        isComplete = false,
                        currentLocation = location,
                        progress = i.toFloat() / totalFiles,
                        filesFound = foundFiles.size,
                        currentFile = "file_$i.jpg",
                        elapsedTimeMs = System.currentTimeMillis() - startTime,
                        estimatedTimeRemainingMs = ((System.currentTimeMillis() - startTime) * (totalFiles - i) / i).toLong(),
                        foundFiles = foundFiles.toList() // Tạo bản sao để tránh race condition
                    )
                )
            }
        }
        
        // Báo hoàn thành
        emit(
            ScanProgress(
                isComplete = true,
                currentLocation = location,
                progress = 1.0f,
                filesFound = foundFiles.size,
                currentFile = null,
                elapsedTimeMs = System.currentTimeMillis() - startTime,
                estimatedTimeRemainingMs = 0,
                foundFiles = foundFiles
            )
        )
    }

    override suspend fun getAvailableLocations(): List<ScanLocation> {
        val locations = mutableListOf<ScanLocation>()
        
        // Thêm bộ nhớ trong
        val internalStorage = Environment.getExternalStorageDirectory()
        locations.add(createScanLocation(
            id = "internal",
            path = internalStorage.absolutePath,
            displayName = "Internal Storage",
            type = LocationType.INTERNAL_STORAGE,
            file = internalStorage
        ))
        
        // Kiểm tra thẻ SD
        val externalDirs = context.getExternalFilesDirs(null)
        for (dir in externalDirs) {
            if (dir != null && dir != internalStorage) {
                // Tìm thư mục gốc của thẻ SD
                var sdCardDir = dir
                while (sdCardDir.parentFile != null && !isExternalStorageRoot(sdCardDir)) {
                    sdCardDir = sdCardDir.parentFile!!
                }
                
                if (isExternalStorageRoot(sdCardDir)) {
                    locations.add(createScanLocation(
                        id = "sdcard",
                        path = sdCardDir.absolutePath,
                        displayName = "SD Card",
                        type = LocationType.SD_CARD,
                        file = sdCardDir
                    ))
                }
            }
        }
        
        return locations
    }
    
    private fun isExternalStorageRoot(file: File): Boolean {
        return Environment.isExternalStorageRemovable(file) && file.totalSpace > 0
    }
    
    private fun createScanLocation(
        id: String,
        path: String,
        displayName: String,
        type: LocationType,
        file: File
    ): ScanLocation {
        val isAvailable = file.exists() && file.canRead()
        val statFs = if (isAvailable) StatFs(path) else null
        
        return ScanLocation(
            id = id,
            path = path,
            displayName = displayName,
            type = type,
            isAvailable = isAvailable,
            totalSpace = statFs?.totalBytes,
            freeSpace = statFs?.freeBytes
        )
    }
}
```

### 3. FileAnalyzerImpl

```kotlin
package com.example.filerecovery.data.analyzer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import com.example.filerecovery.domain.analyzer.FileAnalyzer
import com.example.filerecovery.domain.model.DeletedFile
import com.example.filerecovery.domain.model.FileType
import com.example.filerecovery.domain.model.RecoveryProbability
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.inject.Inject

class FileAnalyzerImpl @Inject constructor() : FileAnalyzer {

    override suspend fun analyze(rawData: ByteArray, path: String?): DeletedFile {
        // Xác định loại file
        val fileType = detectFileType(rawData)
        
        // Tạo name từ path nếu có
        val name = path?.let { File(it).name }
        
        // Tạo thumbnail cho file media
        val thumbnail = generateThumbnail(rawData, fileType)
        
        // Đánh giá khả năng khôi phục
        val recoveryProbability = evaluateRecoveryProbability(rawData)
        
        // Tạo metadata
        val metadata = extractMetadata(rawData, fileType)
        
        // Trả về đối tượng DeletedFile
        return DeletedFile(
            id = UUID.randomUUID().toString(),
            originalPath = path,
            name = name,
            fileType = fileType,
            size = rawData.size.toLong(),
            deletionDate = calculateDeletionDate(),
            creationDate = metadata["creation_date"] as Date?,
            modificationDate = metadata["modification_date"] as Date?,
            thumbnailBytes = thumbnail,
            recoveryProbability = recoveryProbability,
            metadata = metadata
        )
    }

    override suspend fun detectFileType(rawData: ByteArray): FileType {
        if (rawData.size < 8) return FileType.UNKNOWN
        
        // Kiểm tra signature của file
        return when {
            // JPEG: FF D8 FF
            rawData[0] == 0xFF.toByte() && rawData[1] == 0xD8.toByte() && rawData[2] == 0xFF.toByte() -> 
                FileType.JPEG
                
            // PNG: 89 50 4E 47 0D 0A 1A 0A
            rawData[0] == 0x89.toByte() && rawData[1] == 0x50.toByte() && 
            rawData[2] == 0x4E.toByte() && rawData[3] == 0x47.toByte() -> 
                FileType.PNG
                
            // GIF: 47 49 46 38
            rawData[0] == 0x47.toByte() && rawData[1] == 0x49.toByte() && 
            rawData[2] == 0x46.toByte() && rawData[3] == 0x38.toByte() -> 
                FileType.GIF
                
            // MP4: 00 00 00 [14..24] 66 74 79 70
            rawData.size > 12 && isMP4Signature(rawData) -> 
                FileType.MP4
                
            // PDF: 25 50 44 46
            rawData[0] == 0x25.toByte() && rawData[1] == 0x50.toByte() && 
            rawData[2] == 0x44.toByte() && rawData[3] == 0x46.toByte() -> 
                FileType.PDF
                
            // MP3: ID3 or FF FB
            (rawData[0] == 0x49.toByte() && rawData[1] == 0x44.toByte() && rawData[2] == 0x33.toByte()) ||
            (rawData[0] == 0xFF.toByte() && (rawData[1].toInt() and 0xF0) == 0xF0) -> 
                FileType.MP3
                
            // WAV: 52 49 46 46 ?? ?? ?? ?? 57 41 56 45
            rawData[0] == 0x52.toByte() && rawData[1] == 0x49.toByte() && 
            rawData[2] == 0x46.toByte() && rawData[3] == 0x46.toByte() && 
            rawData[8] == 0x57.toByte() && rawData[9] == 0x41.toByte() && 
            rawData[10] == 0x56.toByte() && rawData[11] == 0x45.toByte() -> 
                FileType.WAV
                
            // Thêm các signature khác...
            
            else -> FileType.UNKNOWN
        }
    }
    
    private fun isMP4Signature(rawData: ByteArray): Boolean {
        // MP4 có signature phức tạp hơn
        for (i in 4..12) {
            if (i + 4 < rawData.size &&
                rawData[i] == 0x66.toByte() && // 'f'
                rawData[i+1] == 0x74.toByte() && // 't'
                rawData[i+2] == 0x79.toByte() && // 'y'
                rawData[i+3] == 0x70.toByte()    // 'p'
            ) {
                return true
            }
        }
        return false
    }

    override suspend fun evaluateRecoveryProbability(rawData: ByteArray): RecoveryProbability {
        // Phân tích dữ liệu để đánh giá khả năng phục hồi
        // Đây là logic giả định, trong thực tế sẽ phân tích sâu hơn
        
        // Kiểm tra độ nguyên vẹn của dữ liệu
        return when {
            rawData.size < 100 -> RecoveryProbability.LOW
            rawData.size < 1000 -> RecoveryProbability.MEDIUM
            else -> RecoveryProbability.HIGH
        }
    }

    override suspend fun generateThumbnail(rawData: ByteArray, fileType: FileType): ByteArray? {
        return try {
            when (fileType) {
                FileType.JPEG, FileType.PNG, FileType.GIF, FileType.BMP, FileType.WEBP -> {
                    // Tạo thumbnail cho ảnh
                    val bitmap = BitmapFactory.decodeByteArray(rawData, 0, rawData.size)
                    bitmap?.let {
                        val thumbBitmap = ThumbnailUtils.extractThumbnail(it, 200, 200)
                        val outputStream = ByteArrayOutputStream()
                        thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                        outputStream.toByteArray()
                    }
                }
                
                FileType.MP4, FileType.MOV, FileType.AVI, FileType.MKV, FileType.WEBM -> {
                    // Giả lập tạo thumbnail cho video
                    // Trong thực tế, sẽ sử dụng MediaMetadataRetriever để trích xuất frame
                    null
                }
                
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun extractMetadata(rawData: ByteArray, fileType: FileType): Map<String, Any> {
        val metadata = mutableMapOf<String, Any>()
        
        try {
            when (fileType) {
                FileType.JPEG -> {
                    // Extract EXIF data
                    // Trong thực tế sẽ sử dụng thư viện như ExifInterface
                }
                
                FileType.MP3 -> {
                    // Extract ID3 tags
                    // Trong thực tế sẽ sử dụng thư viện như JAudioTagger
                }
                
                FileType.MP4, FileType.MOV -> {
                    // Extract video metadata
                    // Trong thực tế sẽ sử dụng MediaMetadataRetriever
                }
                
                else -> {
                    // Generic metadata
                }
            }
        } catch (e: Exception) {
            // Log lỗi nhưng không làm crash quá trình phân tích
        }
        
        return metadata
    }
    
    private fun calculateDeletionDate(): Date? {
        // Trong thực tế, phải phân tích file system để xác định ngày xóa
        // Ở đây giả định là xóa trong vòng 7 ngày qua
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -Random().nextInt(7))
        return calendar.time
    }
}
```

## Repository

```kotlin
package com.example.filerecovery.domain.repository

import com.example.filerecovery.domain.model.DeletedFile
import com.example.filerecovery.domain.model.ScanOptions
import com.example.filerecovery.domain.model.ScanProgress
import kotlinx.coroutines.flow.Flow

interface ScanRepository {
    /**
     * Quét thiết bị để tìm file đã xóa
     */
    suspend fun scanForDeletedFiles(options: ScanOptions): Flow<ScanProgress>
    
    /**
     * Hủy quá trình quét
     */
    suspend fun cancelScan()
    
    /**
     * Lấy danh sách các file đã tìm thấy
     */
    suspend fun getFoundFiles(): List<DeletedFile>
    
    /**
     * Kiểm tra thiết bị có hỗ trợ quét không
     */
    suspend fun isDeviceScannable(): Boolean
}
```

## UseCase

```kotlin
package com.example.filerecovery.domain.usecase

import com.example.filerecovery.domain.model.DeletedFile
import com.example.filerecovery.domain.model.ScanOptions
import com.example.filerecovery.domain.model.ScanProgress
import com.example.filerecovery.domain.repository.ScanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScanForDeletedFilesUseCase @Inject constructor(
    private val scanRepository: ScanRepository
) {
    suspend operator fun invoke(options: ScanOptions): Flow<ScanProgress> {
        return scanRepository.scanForDeletedFiles(options)
    }
}

class CancelScanUseCase @Inject constructor(
    private val scanRepository: ScanRepository
) {
    suspend operator fun invoke() {
        scanRepository.cancelScan()
    }
}

class GetFoundFilesUseCase @Inject constructor(
    private val scanRepository: ScanRepository
) {
    suspend operator fun invoke(): List<DeletedFile> {
        return scanRepository.getFoundFiles()
    }
}

class CheckDeviceScannableUseCase @Inject constructor(
    private val scanRepository: ScanRepository
) {
    suspend operator fun invoke(): Boolean {
        return scanRepository.isDeviceScannable()
    }
}
```

## Module DI setup

```kotlin
package com.example.filerecovery.di

import android.content.Context
import com.example.filerecovery.data.analyzer.FileAnalyzerImpl
import com.example.filerecovery.data.engine.ScanEngineImpl
import com.example.filerecovery.data.repository.ScanRepositoryImpl
import com.example.filerecovery.data.scanner.MediaScannerImpl
import com.example.filerecovery.domain.analyzer.FileAnalyzer
import com.example.filerecovery.domain.engine.ScanEngine
import com.example.filerecovery.domain.repository.ScanRepository
import com.example.filerecovery.domain.scanner.MediaScanner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScanModule {
    
    @Provides
    @Singleton
    fun provideMediaScanner(
        @ApplicationContext context: Context,
        fileAnalyzer: FileAnalyzer
    ): MediaScanner {
        return MediaScannerImpl(context, fileAnalyzer)
    }
    
    @Provides
    @Singleton
    fun provideFileAnalyzer(): FileAnalyzer {
        return FileAnalyzerImpl()
    }
    
    @Provides
    @Singleton
    fun provideScanEngine(
        mediaScanner: MediaScanner,
        fileAnalyzer: FileAnalyzer
    ): ScanEngine {
        return ScanEngineImpl(mediaScanner, fileAnalyzer)
    }
    
    @Provides
    @Singleton
    fun provideScanRepository(
        scanEngine: ScanEngine
    ): ScanRepository {
        return ScanRepositoryImpl(scanEngine)
    }
}
```

## Yêu cầu quyền truy cập

Module này yêu cầu quyền truy cập bộ nhớ. Trong ứng dụng thực tế, cần thêm một PermissionManager để xử lý:

```kotlin
package com.example.filerecovery.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import javax.inject.Inject

class PermissionManager @Inject constructor(
    private val context: Context
) {
    fun hasStoragePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
}
```

## Hướng dẫn triển khai

1. Tạo các model class trước tiên
2. Triển khai interfaces (ScanEngine, FileAnalyzer, MediaScanner)
3. Triển khai các implementation class
4. Thiết lập Dependency Injection module
5. Tạo repository và use cases
6. Tích hợp với ViewModel

## Tiêu chí hoàn thành

- Phát hiện được >90% file đã xóa trong 7 ngày qua
- Thời gian quét < 2 phút cho 32GB
- Phân loại chính xác các loại file (Ảnh/Video, Âm thanh, Tài liệu)
- Hiển thị preview cho ảnh và video
- Đánh giá chính xác khả năng khôi phục

## Testing

Cần kiểm tra module này kỹ lưỡng vì đây là chức năng cốt lõi của ứng dụng:

1. Unit tests cho FileAnalyzer để đảm bảo phát hiện đúng loại file
2. Unit tests cho các model class
3. Integration tests cho ScanEngine kết hợp với MediaScanner
4. UI tests để kiểm tra hiển thị tiến trình quét

## Các bước tiếp theo

Sau khi hoàn thành module quét và phát hiện file, bạn có thể tiếp tục với:

1. [Module khôi phục file](recovery-module.md)
2. [Module giao diện người dùng](../ui/ui-module.md) 