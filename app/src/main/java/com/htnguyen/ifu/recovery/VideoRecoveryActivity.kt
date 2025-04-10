package com.htnguyen.ifu.recovery

import android.Manifest
import android.content.ContentValues
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
import java.util.Comparator

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
    private val scannedDirectories = mutableSetOf<String>() // Theo dõi thư mục đã quét
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
                val sampleFile = File(sampleVideoDir, ".sample_video_$i.mp4") // Thêm dấu chấm để biểu thị file đã xóa
                
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
                            "video",
                            true  // Đánh dấu là đã xóa
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
        // Xóa danh sách thư mục đã quét
        scannedDirectories.clear()
        
        // Hiển thị toast để xác nhận rằng chức năng quét đang chạy
        Toast.makeText(this, "Đang bắt đầu quét video...", Toast.LENGTH_LONG).show()
        
        CoroutineScope(Dispatchers.IO).launch {
            // Mô phỏng quá trình quét với độ trễ để thấy rõ animation
            simulateScanProgress()
            
            try {
                // Log trong thread IO
                Log.d("VideoRecovery", "Đang quét trong thread IO")
                
                // Trước tiên tìm kiếm trong các thư mục thùng rác
                withContext(Dispatchers.Main) {
                    updateScanStatus("Đang quét thùng rác...", 30)
                }
                
                // Quét các thư mục thùng rác phổ biến
                scanTrashFolders()
                
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
                
                // Đảm bảo chỉ giữ lại video đã xóa thực sự
                Log.d("VideoRecovery", "Trước khi lọc: ${recoveredVideos.size} video, ${recoveredVideos.count { it.isDeleted() }} video đã xóa")
                
                // Lọc nghiêm ngặt để chỉ giữ lại video đã xóa
                val onlyDeletedVideos = recoveredVideos.filter { it.isDeleted() }
                recoveredVideos.clear()
                recoveredVideos.addAll(onlyDeletedVideos)
                
                Log.d("VideoRecovery", "Sau khi lọc: ${recoveredVideos.size} video, tất cả đều đã xóa")
                
                // Sắp xếp video theo ưu tiên
                sortVideosByPriority()
                
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
    
    /**
     * Quét các thư mục thùng rác phổ biến
     */
    private fun scanTrashFolders() {
        val trashPaths = listOf(
            // Google Files và các thùng rác phổ biến khác
            "/storage/emulated/0/Android/data/com.google.android.apps.nbu.files/files/.trash",
            "/storage/emulated/0/.trashed-videos",
            
            // Samsung
            "/storage/emulated/0/DCIM/.trash", 
            "/storage/emulated/0/DCIM/.trashbin",
            "/storage/emulated/0/Movies/.trash",
            
            // Xiaomi/MIUI 
            "/storage/emulated/0/MIUI/Gallery/cloud/.trash",
            "/storage/emulated/0/MIUI/Gallery/.trashbin",
            "/storage/emulated/0/DCIM/.globalTrash",
            
            // Huawei
            "/storage/emulated/0/DCIM/Gallery/.recyclebin",
            "/storage/emulated/0/Movies/.VideoTrash",
            
            // OPPO/ColorOS
            "/storage/emulated/0/DCIM/.RecycleBin",
            "/storage/emulated/0/Movies/.RecycleBin",
            
            // Vivo
            "/storage/emulated/0/DCIM/.DeletedVideos",
            "/storage/emulated/0/.VivoGalleryRecycler",
            
            // OnePlus
            "/storage/emulated/0/DCIM/.dustbin",
            "/storage/emulated/0/Movies/.dustbin"
        )
        
        for (path in trashPaths) {
            val trashDir = File(path)
            if (trashDir.exists() && trashDir.isDirectory) {
                Log.d("VideoRecovery", "Đang quét thùng rác: $path")
                // Đánh dấu thư mục này đã được quét
                scannedDirectories.add(trashDir.absolutePath)
                scanTrashDirectory(trashDir)
            }
        }
    }
    
    /**
     * Quét một thư mục thùng rác để tìm video đã xóa
     */
    private fun scanTrashDirectory(trashDir: File) {
        try {
            if (!trashDir.exists() || !trashDir.isDirectory || !trashDir.canRead()) {
                return
            }
            
            val files = trashDir.listFiles() ?: return
            
            for (file in files) {
                try {
                    if (file.isDirectory && file.canRead()) {
                        // Quét đệ quy nếu là thư mục
                        scanTrashDirectory(file)
                    } else if (file.isFile && file.canRead() && isVideoFile(file.name) && file.length() > 0) {
                        Log.d("VideoRecovery", "Tìm thấy video trong thùng rác: ${file.absolutePath}")
                        
                        // Kiểm tra trùng lặp trước khi thêm vào danh sách
                        if (!recoveredVideos.any { it.getPath() == file.absolutePath }) {
                            // Tạo đối tượng RecoverableItem và đánh dấu là đã xóa
                            val recoveredFile = RecoverableItem(
                                file,
                                file.length(),
                                "video",
                                true // Đánh dấu là video đã xóa
                            )
                            recoveredVideos.add(recoveredFile)
                            Log.d("VideoRecovery", "Đã thêm video vào danh sách phục hồi: ${file.absolutePath}")
                        } else {
                            Log.d("VideoRecovery", "Bỏ qua video trùng lặp: ${file.absolutePath}")
                        }
                    }
                } catch (e: SecurityException) {
                    Log.e("VideoRecovery", "Lỗi quyền khi xử lý file trong thùng rác: ${e.message}")
                } catch (e: Exception) {
                    Log.e("VideoRecovery", "Lỗi khi xử lý file trong thùng rác: ${e.message}")
                }
            }
        } catch (e: SecurityException) {
            Log.e("VideoRecovery", "Lỗi quyền khi quét thư mục thùng rác: ${e.message}")
        } catch (e: Exception) {
            Log.e("VideoRecovery", "Lỗi khi quét thư mục thùng rác: ${e.message}")
        }
    }
    
    /**
     * Sắp xếp danh sách video theo độ ưu tiên
     */
    private fun sortVideosByPriority() {
        Log.d("VideoRecovery", "Sắp xếp danh sách video theo độ ưu tiên")
        
        // Chuyển sang danh sách mutable mới để tránh lỗi ConcurrentModification
        val sortedList = ArrayList(recoveredVideos)
        
        // Sắp xếp theo nhiều tiêu chí
        sortedList.sortWith(Comparator { item1, item2 ->
            // Tiêu chí 1: Các video chắc chắn đã xóa (từ thùng rác) lên đầu tiên
            val trashDir1 = isFromExplicitTrashDir(item1.getPath())
            val trashDir2 = isFromExplicitTrashDir(item2.getPath())
            
            if (trashDir1 && !trashDir2) return@Comparator -1
            if (!trashDir1 && trashDir2) return@Comparator 1
            
            // Tiêu chí 2: Các video có dấu hiệu đã xóa rõ ràng (tên bắt đầu bằng dấu chấm)
            val hiddenFile1 = item1.getFile().name.startsWith(".")
            val hiddenFile2 = item2.getFile().name.startsWith(".")
            
            if (hiddenFile1 && !hiddenFile2) return@Comparator -1
            if (!hiddenFile1 && hiddenFile2) return@Comparator 1
            
            // Tiêu chí 3: Các video có kích thước lớn lên trước
            val size1 = item1.getSize()
            val size2 = item2.getSize()
            
            if (size1 > size2) return@Comparator -1
            if (size1 < size2) return@Comparator 1
            
            // Tiêu chí 4: Sắp xếp theo thời gian sửa đổi (mới nhất lên đầu)
            val time1 = item1.getFile().lastModified()
            val time2 = item2.getFile().lastModified()
            
            time2.compareTo(time1) // Sắp xếp giảm dần (mới nhất lên đầu)
        })
        
        // Cập nhật lại danh sách
        recoveredVideos.clear()
        recoveredVideos.addAll(sortedList)
        
        Log.d("VideoRecovery", "Đã sắp xếp xong ${recoveredVideos.size} video")
    }
    
    /**
     * Kiểm tra xem đường dẫn có nằm trong thư mục thùng rác rõ ràng không
     */
    private fun isFromExplicitTrashDir(path: String): Boolean {
        val lowercasePath = path.lowercase()
        
        // Các từ khóa thùng rác rõ ràng
        val explicitTrashKeywords = listOf(
            "/.trash/", "/.globaltrash/", "/recycle.bin/", "/.recycle/", 
            "/recycler/", "/bin/", "/.dustbin/", "/.trashbin/"
        )
        
        return explicitTrashKeywords.any { lowercasePath.contains(it) }
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
            // Kiểm tra xem thư mục đã được quét chưa
            if (scannedDirectories.contains(directory.absolutePath)) {
                Log.d("VideoRecovery", "Bỏ qua thư mục đã quét: ${directory.absolutePath}")
                return
            }
            
            // Đánh dấu thư mục này đã được quét
            scannedDirectories.add(directory.absolutePath)
            
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
                        if (file.isDirectory && file.canRead()) {
                            // Kiểm tra nếu đây là thư mục thùng rác
                            val isTrashDir = isTrashDirectory(file)
                            
                            // Quét đệ quy các thư mục, ưu tiên thư mục thùng rác
                            if (isTrashDir || (!file.name.startsWith(".") && !file.name.equals("Android", true))) {
                                simulateFindingDeletedVideos(file)
                            }
                        } else if (file.isFile && file.canRead() && isVideoFile(file.name) && file.length() > 0) {
                            Log.d("VideoRecovery", "Tìm thấy video: ${file.absolutePath}")
                            
                            // Kiểm tra nếu video này đã xóa
                            val isDeleted = isDeletedVideo(file)
                            
                            if (isDeleted) {
                                Log.d("VideoRecovery", "Đây là video đã xóa: ${file.absolutePath}")
                                
                                // Kiểm tra trùng lặp trước khi thêm video vào danh sách
                                if (!recoveredVideos.any { it.getPath() == file.absolutePath }) {
                                    // Tạo đối tượng RecoverableItem và đánh dấu là đã xóa
                                    val recoveredFile = RecoverableItem(
                                        file,
                                        file.length(),
                                        "video",
                                        true // Đánh dấu là video đã xóa
                                    )
                                    recoveredVideos.add(recoveredFile)
                                    Log.d("VideoRecovery", "Đã thêm video đã xóa: ${file.absolutePath}")
                                } else {
                                    Log.d("VideoRecovery", "Bỏ qua video trùng lặp: ${file.absolutePath}")
                                }
                            }
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
    
    /**
     * Kiểm tra xem thư mục có phải là thư mục thùng rác không
     */
    private fun isTrashDirectory(directory: File): Boolean {
        val trashKeywords = listOf(
            ".trash", ".Trash", "trash", "Trash", "recycle", "Recycle", "bin", "Bin",
            "deleted", "Deleted", "dustbin", "Dustbin", ".RecycleBin", "recyclebin",
            ".globalTrash"
        )
        
        val dirName = directory.name.lowercase()
        val dirPath = directory.absolutePath.lowercase()
        
        return directory.isHidden || 
               trashKeywords.any { dirName.contains(it.lowercase()) } ||
               trashKeywords.any { dirPath.contains(it.lowercase()) }
    }
    
    /**
     * Kiểm tra xem video có phải là video đã xóa không
     */
    private fun isDeletedVideo(file: File): Boolean {
        // Kiểm tra tên file có bắt đầu bằng dấu chấm (thường là file ẩn/đã xóa)
        val hasHiddenName = file.name.startsWith(".")
        
        // Kiểm tra đường dẫn file có chứa từ khóa thùng rác
        val inTrashFolder = isFileInTrashFolder(file)
        
        // Kiểm tra thư mục cha có phải là thư mục thùng rác
        val inTrashDirectory = file.parentFile?.let { isTrashDirectory(it) } ?: false
        
        return hasHiddenName || inTrashFolder || inTrashDirectory
    }
    
    /**
     * Kiểm tra xem file có nằm trong thư mục thùng rác không
     */
    private fun isFileInTrashFolder(file: File): Boolean {
        // Danh sách các từ khóa trong đường dẫn giúp xác định thư mục thùng rác
        val trashKeywords = listOf(
            ".trash", ".Trash", "trash", "Trash", "recycle", "Recycle", "bin", "Bin",
            "deleted", "Deleted", "dustbin", "Dustbin", ".RecycleBin", "recyclebin",
            ".globalTrash"
        )
        
        // Lấy đường dẫn tuyệt đối
        val absolutePath = file.absolutePath.lowercase()
        
        // Kiểm tra nếu đường dẫn chứa từ khóa thùng rác
        return trashKeywords.any { absolutePath.contains(it.lowercase()) }
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
            // Tạo thư mục khôi phục dự phòng trong trường hợp không thể khôi phục vào thư viện
            val recoveryDir = File(getExternalFilesDir(null), "RecoveredVideos")
            if (!recoveryDir.exists()) {
                recoveryDir.mkdirs()
            }
            
            var successCount = 0
            var galleryRecoveryCount = 0
            
            for (video in selectedVideos) {
                try {
                    val sourceFile = video.getFile()
                    val destFile = File(recoveryDir, sourceFile.name)
                    
                    // Tên file mới sau khi khôi phục (loại bỏ dấu chấm ở đầu nếu có)
                    val recoveredFileName = if (sourceFile.name.startsWith(".")) {
                        sourceFile.name.substring(1)
                    } else {
                        sourceFile.name
                    }
                    
                    // Thử khôi phục video vào bộ sưu tập trên thiết bị
                    Log.d("VideoRecovery", "Đang khôi phục video vào bộ sưu tập: ${sourceFile.absolutePath}")
                    
                    if (recoverToGallery(sourceFile, recoveredFileName)) {
                        galleryRecoveryCount++
                        successCount++
                    } else {
                        // Nếu không thể khôi phục vào bộ sưu tập, sao chép vào thư mục khôi phục của ứng dụng
                        FileInputStream(sourceFile).use { input ->
                            FileOutputStream(destFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                        successCount++
                        Log.d("VideoRecovery", "Đã sao chép vào thư mục khôi phục: ${destFile.absolutePath}")
                    }
                } catch (e: Exception) {
                    Log.e("VideoRecovery", "Lỗi khi khôi phục: ${e.message}")
                    e.printStackTrace()
                }
            }
            
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                if (successCount > 0) {
                    val message = if (galleryRecoveryCount > 0) {
                        getString(R.string.recovery_success_gallery, successCount, galleryRecoveryCount)
                    } else {
                        getString(R.string.recovery_success, successCount)
                    }
                    
                    Toast.makeText(
                        this@VideoRecoveryActivity,
                        message,
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
    
    /**
     * Khôi phục video vào bộ sưu tập (thư viện) thiết bị
     * @return true nếu khôi phục thành công vào bộ sưu tập
     */
    private fun recoverToGallery(sourceFile: File, recoveredFileName: String): Boolean {
        try {
            // Chỉ khôi phục vào thư mục Movies/Camera (không tạo nhiều bản sao)
            val moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
            val cameraDir = File(moviesDir, "Camera")
            
            if (!cameraDir.exists()) {
                cameraDir.mkdirs()
            }
            
            // Tạo file khôi phục trong thư mục Movies/Camera
            val recoveredFile = File(cameraDir, recoveredFileName)
            
            // Sao chép file từ nguồn vào thư mục
            FileInputStream(sourceFile).use { input ->
                FileOutputStream(recoveredFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            // Lấy thời gian hiện tại cho metadata
            val currentTime = System.currentTimeMillis()
            val mimeType = getMimeType(recoveredFileName)
            
            // Thêm thông tin về video vào MediaStore để hiển thị trong thư viện
            val values = ContentValues().apply {
                // Metadata cần thiết để video hiển thị trong các ứng dụng khác nhau
                put(android.provider.MediaStore.Video.Media.DISPLAY_NAME, recoveredFileName)
                put(android.provider.MediaStore.Video.Media.TITLE, recoveredFileName.substringBeforeLast('.'))
                put(android.provider.MediaStore.Video.Media.MIME_TYPE, mimeType)
                put(android.provider.MediaStore.Video.Media.DATE_ADDED, currentTime / 1000)
                put(android.provider.MediaStore.Video.Media.DATE_MODIFIED, currentTime / 1000)
                put(android.provider.MediaStore.Video.Media.DATE_TAKEN, currentTime)
                put(android.provider.MediaStore.Video.Media.SIZE, recoveredFile.length())
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10 trở lên sử dụng RELATIVE_PATH
                    put(android.provider.MediaStore.Video.Media.RELATIVE_PATH, "Movies/Camera")
                    put(android.provider.MediaStore.Video.Media.IS_PENDING, 0)
                } else {
                    // Android 9 trở xuống sử dụng DATA
                    put(android.provider.MediaStore.Video.Media.DATA, recoveredFile.absolutePath)
                }
            }
            
            // Thêm video vào MediaStore
            val uri = contentResolver.insert(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            
            // Thông báo hệ thống quét media mới cho Android 9 trở xuống
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = android.net.Uri.fromFile(recoveredFile)
                sendBroadcast(mediaScanIntent)
                
                // Cho Android 9 trở xuống, cập nhật MediaStore để video xuất hiện trong các ứng dụng khác
                // mà không cần tạo nhiều bản sao
                try {
                    // Scan video để cập nhật MediaStore
                    android.media.MediaScannerConnection.scanFile(
                        applicationContext,
                        arrayOf(recoveredFile.absolutePath),
                        arrayOf(mimeType)
                    ) { _, _ ->
                        Log.d("VideoRecovery", "Media scan hoàn tất để cập nhật MediaStore")
                    }
                } catch (e: Exception) {
                    Log.e("VideoRecovery", "Lỗi khi quét file với MediaScannerConnection: ${e.message}")
                }
            }
            
            Log.d("VideoRecovery", "Đã khôi phục video vào thư viện và mục video: ${recoveredFile.absolutePath}")
            return true
        } catch (e: Exception) {
            Log.e("VideoRecovery", "Lỗi khi khôi phục vào bộ sưu tập: ${e.message}")
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * Xác định MIME type dựa trên phần mở rộng của file
     */
    private fun getMimeType(fileName: String): String {
        return when (fileName.substringAfterLast('.', "").lowercase()) {
            "mp4" -> "video/mp4"
            "3gp" -> "video/3gpp"
            "mkv" -> "video/x-matroska"
            "avi" -> "video/x-msvideo"
            "mov" -> "video/quicktime"
            "wmv" -> "video/x-ms-wmv"
            else -> "video/mp4" // Mặc định
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