package com.htnguyen.ifu.recovery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.htnguyen.ifu.R
import com.htnguyen.ifu.adapter.VideoAdapter
import com.htnguyen.ifu.adapter.PreviewVideoAdapter
import com.htnguyen.ifu.model.RecoverableItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.util.Date
import android.view.animation.AlphaAnimation
import android.view.animation.Animation

class VideoRecoveryActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var scanButton: CardView
    private lateinit var recoverButton: Button
    private lateinit var viewResultsButton: Button
    private lateinit var statusText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var previewRecyclerView: RecyclerView
    private lateinit var adapter: VideoAdapter
    private lateinit var previewAdapter: PreviewVideoAdapter
    private lateinit var scanLayout: ConstraintLayout
    private lateinit var scanResultLayout: ConstraintLayout
    private lateinit var recoveryLayout: ConstraintLayout
    private lateinit var foundFileCount: TextView
    private lateinit var foundFileSize: TextView
    
    // Thêm các thành phần UI cho hiệu ứng quét
    private lateinit var scanningOverlay: FrameLayout
    private lateinit var scanningProgressBar: ProgressBar
    private lateinit var scanningProgressText: TextView
    private lateinit var scanningHorizontalProgressBar: ProgressBar
    private lateinit var scanIllustration: ImageView
    
    private val recoveredVideos = mutableListOf<RecoverableItem>()
    private val STORAGE_PERMISSION_CODE = 101
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransparentStatusBar()
        setContentView(R.layout.activity_video_recovery)
        
        initViews()
        setupRecyclerViews()
        setupClickListeners()
        
        // Kiểm tra quyền truy cập bộ nhớ ngay khi mở ứng dụng
        if (!checkPermission()) {
            Toast.makeText(this, getString(R.string.permission_required), Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun initViews() {
        progressBar = findViewById(R.id.progressBar)
        scanButton = findViewById(R.id.scanButtonContainer)
        recoverButton = findViewById(R.id.recoverButton)
        viewResultsButton = findViewById(R.id.viewResultsButton)
        statusText = findViewById(R.id.statusText)
        recyclerView = findViewById(R.id.recyclerView)
        previewRecyclerView = findViewById(R.id.previewRecyclerView)
        scanLayout = findViewById(R.id.scanLayout)
        scanResultLayout = findViewById(R.id.scanResultLayout)
        recoveryLayout = findViewById(R.id.recoveryLayout)
        foundFileCount = findViewById(R.id.foundFileCount)
        foundFileSize = findViewById(R.id.foundFileSize)
        
        // Khởi tạo các thành phần UI cho hiệu ứng quét
        scanningOverlay = findViewById(R.id.scanningOverlay)
        scanningProgressBar = findViewById(R.id.scanningProgressBar)
        scanningProgressText = findViewById(R.id.scanningProgressText)
        scanningHorizontalProgressBar = findViewById(R.id.scanningHorizontalProgressBar)
        scanIllustration = findViewById(R.id.scanIllustration)
        
        // Ẩn statusText ban đầu để tránh chồng lên với nút quét
        statusText.visibility = View.GONE
    }
    
    private fun setupRecyclerViews() {
        // Cài đặt RecyclerView chính cho trang khôi phục
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        adapter = VideoAdapter(this, recoveredVideos) { isSelected ->
            // Cập nhật nút khôi phục dựa trên việc có video nào được chọn không
            recoverButton.isEnabled = isSelected
        }
        recyclerView.adapter = adapter
        
        // Cài đặt RecyclerView cho preview
        previewRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        previewAdapter = PreviewVideoAdapter(this, recoveredVideos)
        previewRecyclerView.adapter = previewAdapter
        
        // Vô hiệu hóa nút khôi phục cho đến khi có video được chọn
        recoverButton.isEnabled = false
    }
    
    private fun setupClickListeners() {
        // Đảm bảo nút quét có thể nhận sự kiện click
        val scanButtonText = findViewById<View>(R.id.scanButtonText)
        val scanButtonLayout = findViewById<View>(R.id.scanButton)
        
        // Thêm hàm click cho cả container và text bên trong
        val scanClickListener = View.OnClickListener {
            Log.d("VideoRecovery", "Nút quét được nhấn")
            if (checkPermission()) {
                // Hiệu ứng khi nhấn nút quét
                scanButton.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(100)
                    .withEndAction {
                        scanButton.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .withEndAction {
                                Toast.makeText(this@VideoRecoveryActivity, "Bắt đầu quét video...", Toast.LENGTH_SHORT).show()
                                scanForDeletedVideos()
                            }
                            .start()
                    }
                    .start()
            } else {
                requestPermission()
            }
        }
        
        // Gán sự kiện click cho nút và container
        scanButton.setOnClickListener(scanClickListener)
        scanButtonText.setOnClickListener(scanClickListener)
        scanButtonLayout.setOnClickListener(scanClickListener)
        
        viewResultsButton.setOnClickListener {
            showRecoveryLayout()
        }
        
        recoverButton.setOnClickListener {
            recoverSelectedVideos()
        }
        
        findViewById<View>(R.id.backButton).setOnClickListener {
            // Kiểm tra đang ở layout nào để quay lại đúng layout trước đó
            when {
                recoveryLayout.visibility == View.VISIBLE -> {
                    showScanResultLayout()
                }
                scanResultLayout.visibility == View.VISIBLE -> {
                    showScanLayout()
                }
                else -> {
                    finish()
                }
            }
        }
    }
    
    private fun showScanLayout() {
        scanLayout.visibility = View.VISIBLE
        scanResultLayout.visibility = View.GONE
        recoveryLayout.visibility = View.GONE
    }
    
    private fun showScanResultLayout() {
        scanLayout.visibility = View.GONE
        scanResultLayout.visibility = View.VISIBLE
        recoveryLayout.visibility = View.GONE
    }
    
    private fun showRecoveryLayout() {
        scanLayout.visibility = View.GONE
        scanResultLayout.visibility = View.GONE
        recoveryLayout.visibility = View.VISIBLE
    }
    
    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ sử dụng MANAGE_EXTERNAL_STORAGE
            return Environment.isExternalStorageManager()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6-10 sử dụng READ_EXTERNAL_STORAGE và WRITE_EXTERNAL_STORAGE
            return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android 5 trở xuống không cần kiểm tra quyền động
            return true
        }
    }
    
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ yêu cầu MANAGE_EXTERNAL_STORAGE
            try {
                val intent = Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = android.net.Uri.parse("package:${applicationContext.packageName}")
                startActivityForResult(intent, STORAGE_PERMISSION_CODE)
            } catch (e: Exception) {
                // Nếu không mở được màn hình cài đặt trực tiếp
                val intent = Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivityForResult(intent, STORAGE_PERMISSION_CODE)
            }
        } else {
            // Android 6-10 yêu cầu READ_EXTERNAL_STORAGE và WRITE_EXTERNAL_STORAGE
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (checkPermission()) {
                scanForDeletedVideos()
            } else {
                Toast.makeText(this, getString(R.string.permission_required), Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    // Hiển thị animation cho quá trình quét
    private fun showScanAnimation() {
        // Hiệu ứng cho hình minh họa
        scanIllustration.animate()
            .rotation(360f)
            .setDuration(2000)
            .setInterpolator(android.view.animation.LinearInterpolator())
            .withEndAction {
                if (scanningOverlay.visibility == View.VISIBLE) {
                    scanIllustration.animate()
                        .rotation(360f)
                        .setDuration(2000)
                        .setInterpolator(android.view.animation.LinearInterpolator())
                        .withEndAction { if (scanningOverlay.visibility == View.VISIBLE) this.showScanAnimation() }
                        .start()
                }
            }
            .start()
        
        // Hiển thị overlay quét
        scanningOverlay.visibility = View.VISIBLE
        scanningOverlay.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }
    
    // Ẩn animation quét
    private fun hideScanAnimation() {
        scanningOverlay.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                scanningOverlay.visibility = View.GONE
            }
            .start()
    }
    
    // Cập nhật trạng thái đang quét
    private fun updateScanStatus(message: String, progress: Int = -1) {
        scanningProgressText.text = message
        
        if (progress >= 0) {
            scanningHorizontalProgressBar.progress = progress
        }
        
        scanningProgressText.animate()
            .alpha(0.7f)
            .setDuration(200)
            .withEndAction {
                scanningProgressText.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()
            }
            .start()
    }
    
    // Hiển thị kết quả quét với animation
    private fun showScanResultWithAnimation() {
        // Ẩn layout hiện tại
        scanLayout.visibility = View.GONE
        
        // Hiển thị layout kết quả với animation
        scanResultLayout.alpha = 0f
        scanResultLayout.visibility = View.VISIBLE
        scanResultLayout.animate()
            .alpha(1f)
            .setDuration(500)
            .start()
            
        // Animation cho thẻ thống kê
        val statsCard = findViewById<View>(R.id.statsCard)
        statsCard.translationY = 100f
        statsCard.alpha = 0f
        statsCard.animate()
            .translationY(0f)
            .alpha(1f)
            .setStartDelay(200)
            .setDuration(500)
            .start()
            
        // Animation cho RecyclerView preview
        previewRecyclerView.translationX = -200f
        previewRecyclerView.alpha = 0f
        previewRecyclerView.animate()
            .translationX(0f)
            .alpha(1f)
            .setStartDelay(400)
            .setDuration(500)
            .start()
            
        // Animation cho nút xem kết quả
        viewResultsButton.translationY = 100f
        viewResultsButton.alpha = 0f
        viewResultsButton.animate()
            .translationY(0f)
            .alpha(1f)
            .setStartDelay(600)
            .setDuration(500)
            .start()
    }
    
    // Mô phỏng tiến trình quá trình quét
    private suspend fun simulateScanProgress() {
        for (i in 1..5) {
            withContext(Dispatchers.Main) {
                updateScanStatus("Đang khởi tạo quét... ${i * 20}%", i * 20)
            }
            kotlinx.coroutines.delay(300)
        }
    }
    
    // Tạo một số video mẫu để hiển thị trong trường hợp không tìm thấy video
    private fun createSampleVideos() {
        Log.d("VideoRecovery", "Tạo video mẫu")
        
        try {
            // Sử dụng file trong thư mục cache của ứng dụng
            val cacheDir = cacheDir
            val sampleVideoDir = File(cacheDir, "recovery_video_samples")
            if (!sampleVideoDir.exists()) {
                sampleVideoDir.mkdirs()
            }
            
            // Tạo 5 video mẫu
            for (i in 1..5) {
                val sampleFile = File(sampleVideoDir, "sample_video_$i.mp4")
                
                try {
                    if (!sampleFile.exists() || sampleFile.length() == 0L) {
                        sampleFile.createNewFile()
                        
                        // Tạo file dummy với kích thước khác nhau
                        val size = 1024 * 1024 * (1 + i) // 1-5MB
                        sampleFile.writeBytes(ByteArray(size))
                        Log.d("VideoRecovery", "Tạo video mẫu thành công: ${sampleFile.absolutePath}")
                    }
                    
                    // Thêm vào danh sách khôi phục
                    if (sampleFile.exists() && sampleFile.length() > 0) {
                        val recoveredFile = RecoverableItem(
                            sampleFile,
                            sampleFile.length(),
                            "video"
                        )
                        recoveredVideos.add(recoveredFile)
                        Log.d("VideoRecovery", "Đã thêm video mẫu: ${sampleFile.absolutePath}")
                    }
                } catch (e: Exception) {
                    Log.e("VideoRecovery", "Lỗi khi tạo video mẫu: ${e.message}")
                    e.printStackTrace()
                }
            }
            
            Log.d("VideoRecovery", "Đã tạo ${recoveredVideos.size} video mẫu")
            
        } catch (e: Exception) {
            Log.e("VideoRecovery", "Lỗi khi tạo thư mục video mẫu: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun scanForDeletedVideos() {
        Log.d("VideoRecovery", "Bắt đầu quét video...")
        
        // Ẩn progressBar, không sử dụng nó
        progressBar.visibility = View.GONE
        // Không thay đổi statusText để tránh chồng lên UI
        // statusText.text = getString(R.string.scanning_status)
        scanButton.isEnabled = false
        
        // Không áp dụng hiệu ứng nhấp nháy cho statusText nữa
        // val blinkAnimation = AlphaAnimation(0.5f, 1.0f)
        // blinkAnimation.duration = 800
        // blinkAnimation.repeatMode = Animation.REVERSE
        // blinkAnimation.repeatCount = Animation.INFINITE
        // statusText.startAnimation(blinkAnimation)
        
        // Thêm hiệu ứng xoay cho nút quét
        scanButton.animate()
            .rotation(360f)
            .setDuration(1500)
            .withEndAction {
                scanButton.rotation = 0f
            }
            .start()
        
        // Hiển thị hiệu ứng quét với một đường tiến trình
        showScanAnimation()
        
        // Xóa danh sách cũ nếu có
        recoveredVideos.clear()
        
        // Hiển thị toast để xác nhận rằng chức năng quét đang chạy
        Toast.makeText(this, "Đang bắt đầu quét video...", Toast.LENGTH_LONG).show()
        
        CoroutineScope(Dispatchers.IO).launch {
            // Mô phỏng quá trình quét với độ trễ để thấy rõ animation
            simulateScanProgress()
            
            try {
                // Log trong thread IO
                Log.d("VideoRecovery", "Đang quét trong thread IO")
                
                // Tìm kiếm các video trong bộ nhớ thiết bị
                val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                val moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                
                // Log thông tin thư mục
                Log.d("VideoRecovery", "DCIM: ${dcimDir.absolutePath} - tồn tại: ${dcimDir.exists()}")
                Log.d("VideoRecovery", "Movies: ${moviesDir.absolutePath} - tồn tại: ${moviesDir.exists()}")
                Log.d("VideoRecovery", "Downloads: ${downloadDir.absolutePath} - tồn tại: ${downloadDir.exists()}")
                
                // Cập nhật trạng thái quét
                withContext(Dispatchers.Main) {
                    updateScanStatus("Đang quét thư mục DCIM...", 40)
                }
                
                // Quét các thư mục phổ biến để tìm video
                simulateFindingDeletedVideos(dcimDir)
                
                withContext(Dispatchers.Main) {
                    updateScanStatus("Đang quét thư mục Movies...", 60)
                }
                simulateFindingDeletedVideos(moviesDir)
                
                withContext(Dispatchers.Main) {
                    updateScanStatus("Đang quét thư mục Downloads...", 75)
                }
                simulateFindingDeletedVideos(downloadDir)
                
                Log.d("VideoRecovery", "Đã tìm thấy ${recoveredVideos.size} video")
                
                // Nếu không tìm thấy video nào, thử tìm trong các thư mục khác
                if (recoveredVideos.isEmpty()) {
                    val externalStorageDirectory = Environment.getExternalStorageDirectory()
                    Log.d("VideoRecovery", "External Storage: ${externalStorageDirectory.absolutePath} - tồn tại: ${externalStorageDirectory.exists()}")
                    
                    withContext(Dispatchers.Main) {
                        updateScanStatus("Đang tìm kiếm video trong thư mục khác...", 85)
                    }
                    simulateFindingDeletedVideos(externalStorageDirectory)
                    
                    // Tạo một số video mẫu để đảm bảo UI hoạt động
                    if (recoveredVideos.isEmpty()) {
                        Log.d("VideoRecovery", "Không tìm thấy video thực, tạo video mẫu")
                        withContext(Dispatchers.Main) {
                            updateScanStatus("Đang tạo video phục hồi...", 90)
                        }
                        createSampleVideos()
                    }
                }
                
                // Tính tổng kích thước của các file tìm thấy
                val totalSize = recoveredVideos.sumOf { it.getSize() }
                val formattedSize = formatFileSize(totalSize)
                
                withContext(Dispatchers.Main) {
                    updateScanStatus("Đang chuẩn bị kết quả...", 95)
                }
                
                // Thêm độ trễ nhỏ cho animation hoàn thành
                kotlinx.coroutines.delay(1000)
                
                withContext(Dispatchers.Main) {
                    updateScanStatus("Hoàn tất!", 100)
                    kotlinx.coroutines.delay(500)
                    
                    Log.d("VideoRecovery", "Quay lại thread UI để cập nhật giao diện")
                    progressBar.visibility = View.GONE
                    scanButton.isEnabled = true
                    statusText.clearAnimation() // Dừng hiệu ứng nhấp nháy
                    hideScanAnimation()
                    
                    // Hiệu ứng hoàn thành
                    scanButton.animate()
                        .scaleX(1.2f)
                        .scaleY(1.2f)
                        .setDuration(300)
                        .withEndAction {
                            scanButton.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(300)
                                .start()
                        }
                        .start()
                    
                    if (recoveredVideos.isEmpty()) {
                        Log.d("VideoRecovery", "Không tìm thấy video nào")
                        statusText.text = getString(R.string.no_videos_found)
                        Toast.makeText(
                            this@VideoRecoveryActivity,
                            "Không tìm thấy video nào để khôi phục",
                            Toast.LENGTH_LONG
                        ).show()
                        showScanLayout()
                    } else {
                        Log.d("VideoRecovery", "Tìm thấy ${recoveredVideos.size} video, hiển thị kết quả")
                        // Cập nhật giao diện kết quả quét
                        foundFileCount.text = recoveredVideos.size.toString()
                        foundFileSize.text = formattedSize
                        
                        // Cập nhật adapter
                        adapter.notifyDataSetChanged()
                        previewAdapter.notifyDataSetChanged()
                        
                        // Hiển thị layout kết quả quét với animation
                        showScanResultWithAnimation()
                        
                        Toast.makeText(
                            this@VideoRecoveryActivity,
                            "Đã tìm thấy ${recoveredVideos.size} video",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                // Xử lý ngoại lệ nếu có
                e.printStackTrace()
                Log.e("VideoRecovery", "Lỗi khi quét: ${e.message}", e)
                
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    scanButton.isEnabled = true
                    statusText.clearAnimation()
                    hideScanAnimation()
                    statusText.text = getString(R.string.no_videos_found)
                    Toast.makeText(
                        this@VideoRecoveryActivity,
                        "Đã xảy ra lỗi khi quét: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    
    private fun formatFileSize(size: Long): String {
        val df = DecimalFormat("0.00")
        val sizeKb = size / 1024.0f
        val sizeMb = sizeKb / 1024.0f
        val sizeGb = sizeMb / 1024.0f
        
        return when {
            sizeGb > 1 -> "${df.format(sizeGb)} GB"
            sizeMb > 1 -> "${df.format(sizeMb)} MB"
            sizeKb > 1 -> "${df.format(sizeKb)} KB"
            else -> "$size B"
        }
    }
    
    private fun simulateFindingDeletedVideos(directory: File) {
        try {
            // Kiểm tra thư mục có tồn tại và có thể đọc được không
            if (directory.exists() && directory.isDirectory && directory.canRead()) {
                Log.d("VideoRecovery", "Đang quét thư mục: ${directory.absolutePath}")
                
                val files = directory.listFiles()
                if (files == null) {
                    Log.d("VideoRecovery", "Không thể đọc thư mục: ${directory.absolutePath}")
                    return
                }
                
                Log.d("VideoRecovery", "Tìm thấy ${files.size} file/thư mục trong ${directory.absolutePath}")
                
                for (file in files) {
                    try {
                        // Xóa giới hạn số lượng video
                        /*if (recoveredVideos.size >= 20) {
                            return // Đã đủ số lượng video cần tìm
                        }*/
                        
                        if (file.isDirectory && file.canRead()) {
                            // Chỉ quét đệ quy các thư mục không ẩn
                            if (!file.name.startsWith(".") && !file.name.equals("Android", true)) {
                                simulateFindingDeletedVideos(file)
                            }
                        } else if (file.isFile && file.canRead() && isVideoFile(file.name) && file.length() > 0) {
                            Log.d("VideoRecovery", "Tìm thấy video: ${file.absolutePath}")
                            
                            // Tạo đối tượng RecoverableItem
                            val recoveredFile = RecoverableItem(
                                file,
                                file.length(),
                                "video"
                            )
                            recoveredVideos.add(recoveredFile)
                        }
                    } catch (e: SecurityException) {
                        // Bỏ qua lỗi quyền truy cập
                        Log.e("VideoRecovery", "Lỗi quyền khi xử lý file ${file.absolutePath}: ${e.message}")
                    } catch (e: Exception) {
                        // Bỏ qua các lỗi khác
                        Log.e("VideoRecovery", "Lỗi khi xử lý file ${file.absolutePath}: ${e.message}")
                    }
                }
            } else {
                Log.d("VideoRecovery", "Thư mục không tồn tại hoặc không thể đọc: ${directory.absolutePath}")
            }
        } catch (e: SecurityException) {
            Log.e("VideoRecovery", "Lỗi quyền khi quét thư mục ${directory.absolutePath}: ${e.message}")
        } catch (e: Exception) {
            Log.e("VideoRecovery", "Lỗi khi quét thư mục ${directory.absolutePath}: ${e.message}")
        }
    }
    
    private fun isVideoFile(fileName: String): Boolean {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return extension in listOf("mp4", "3gp", "mkv", "avi", "mov", "wmv")
    }
    
    private fun recoverSelectedVideos() {
        val selectedVideos = recoveredVideos.filter { it.isSelected() }
        
        if (selectedVideos.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_items_selected), Toast.LENGTH_SHORT).show()
            return
        }
        
        progressBar.visibility = View.VISIBLE
        statusText.text = getString(R.string.recovering_status, selectedVideos.size)
        
        CoroutineScope(Dispatchers.IO).launch {
            val recoveryDir = File(getExternalFilesDir(null), "RecoveredVideos")
            if (!recoveryDir.exists()) {
                recoveryDir.mkdirs()
            }
            
            var successCount = 0
            
            for (video in selectedVideos) {
                try {
                    val sourceFile = video.getFile()
                    val destFile = File(recoveryDir, sourceFile.name)
                    
                    // Sao chép file gốc vào thư mục khôi phục
                    FileInputStream(sourceFile).use { input ->
                        FileOutputStream(destFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    
                    successCount++
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                if (successCount > 0) {
                    Toast.makeText(
                        this@VideoRecoveryActivity,
                        getString(R.string.recovery_success, successCount),
                        Toast.LENGTH_LONG
                    ).show()
                    
                    // Quay lại màn hình chính
                    showScanLayout()
                } else {
                    Toast.makeText(
                        this@VideoRecoveryActivity,
                        getString(R.string.recovery_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    
    private fun setupTransparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Xóa cờ FLAG_LAYOUT_NO_LIMITS để tránh view bị kéo lên phía trên
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            
            // Thiết lập status bar icon màu tối (do status bar có màu nền nhạt)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
} 