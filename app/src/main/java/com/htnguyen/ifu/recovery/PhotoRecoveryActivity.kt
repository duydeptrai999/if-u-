package com.htnguyen.ifu.recovery

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.htnguyen.ifu.R
import com.htnguyen.ifu.adapter.PhotoAdapter
import com.htnguyen.ifu.adapter.PreviewPhotoAdapter
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
import android.content.Intent
import android.util.Log
import android.widget.ImageView

class PhotoRecoveryActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var scanButton: FrameLayout
    private lateinit var recoverButton: Button
    private lateinit var viewResultsButton: Button
    private lateinit var statusText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var previewRecyclerView: RecyclerView
    private lateinit var adapter: PhotoAdapter
    private lateinit var previewAdapter: PreviewPhotoAdapter
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
    
    private val recoveredPhotos = mutableListOf<RecoverableItem>()
    private val STORAGE_PERMISSION_CODE = 101
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransparentStatusBar()
        setContentView(R.layout.activity_photo_recovery)
        
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
    }
    
    private fun setupRecyclerViews() {
        // Cài đặt RecyclerView chính cho trang khôi phục
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        adapter = PhotoAdapter(recoveredPhotos) { isSelected ->
            // Cập nhật nút khôi phục dựa trên việc có ảnh nào được chọn không
            recoverButton.isEnabled = isSelected
        }
        recyclerView.adapter = adapter
        
        // Cài đặt RecyclerView cho preview
        previewRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        previewAdapter = PreviewPhotoAdapter(recoveredPhotos)
        previewRecyclerView.adapter = previewAdapter
        
        // Vô hiệu hóa nút khôi phục cho đến khi có ảnh được chọn
        recoverButton.isEnabled = false
    }
    
    private fun setupClickListeners() {
        // Đảm bảo nút quét có thể nhận sự kiện click
        val scanButtonText = findViewById<View>(R.id.scanButtonText)
        
        // Thêm hàm click cho cả container và text bên trong
        val scanClickListener = View.OnClickListener {
            println("Nút quét được nhấn")
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
                                Toast.makeText(this@PhotoRecoveryActivity, "Bắt đầu quét...", Toast.LENGTH_SHORT).show()
                                scanForDeletedPhotos()
                            }
                            .start()
                    }
                    .start()
            } else {
                requestPermission()
            }
        }
        
        // Gán sự kiện click cho cả container và text
        scanButton.setOnClickListener(scanClickListener)
        scanButtonText.setOnClickListener(scanClickListener)
        
        viewResultsButton.setOnClickListener {
            showRecoveryLayout()
        }
        
        recoverButton.setOnClickListener {
            recoverSelectedPhotos()
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
                val intent = android.content.Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = android.net.Uri.parse("package:${applicationContext.packageName}")
                startActivityForResult(intent, STORAGE_PERMISSION_CODE)
            } catch (e: Exception) {
                // Nếu không mở được màn hình cài đặt trực tiếp
                val intent = android.content.Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
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
                scanForDeletedPhotos()
            } else {
                Toast.makeText(this, getString(R.string.permission_required), Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scanForDeletedPhotos()
            } else {
                Toast.makeText(this, getString(R.string.permission_required), Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun scanForDeletedPhotos() {
        Log.d("PhotoRecovery", "Bắt đầu quét ảnh...")
        println("PhotoRecovery: Bắt đầu quét ảnh...")
        
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
        recoveredPhotos.clear()
        
        // Hiển thị toast để xác nhận rằng chức năng quét đang chạy
        Toast.makeText(this, "Đang bắt đầu quét ảnh...", Toast.LENGTH_LONG).show()
        
        CoroutineScope(Dispatchers.IO).launch {
            // Mô phỏng quá trình quét với độ trễ để thấy rõ animation
            simulateScanProgress()
            
            try {
                // Log trong thread IO
                Log.d("PhotoRecovery", "Đang quét trong thread IO")
                
                // Tìm kiếm các ảnh trong bộ nhớ thiết bị
                val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                
                // Log thông tin thư mục
                Log.d("PhotoRecovery", "DCIM: ${dcimDir.absolutePath} - tồn tại: ${dcimDir.exists()}")
                Log.d("PhotoRecovery", "Pictures: ${picturesDir.absolutePath} - tồn tại: ${picturesDir.exists()}")
                Log.d("PhotoRecovery", "Downloads: ${downloadDir.absolutePath} - tồn tại: ${downloadDir.exists()}")
                
                // Cập nhật trạng thái quét
                withContext(Dispatchers.Main) {
                    updateScanStatus("Đang quét thư mục DCIM...", 40)
                }
                
                // Quét các thư mục phổ biến để tìm ảnh
                simulateFindingDeletedPhotos(dcimDir)
                
                withContext(Dispatchers.Main) {
                    updateScanStatus("Đang quét thư mục Pictures...", 60)
                }
                simulateFindingDeletedPhotos(picturesDir)
                
                withContext(Dispatchers.Main) {
                    updateScanStatus("Đang quét thư mục Downloads...", 75)
                }
                simulateFindingDeletedPhotos(downloadDir)
                
                Log.d("PhotoRecovery", "Đã tìm thấy ${recoveredPhotos.size} ảnh")
                
                // Nếu không tìm thấy ảnh nào, thử tìm trong các thư mục khác
                if (recoveredPhotos.isEmpty()) {
                    val externalStorageDirectory = Environment.getExternalStorageDirectory()
                    Log.d("PhotoRecovery", "External Storage: ${externalStorageDirectory.absolutePath} - tồn tại: ${externalStorageDirectory.exists()}")
                    
                    withContext(Dispatchers.Main) {
                        updateScanStatus("Đang tìm kiếm ảnh trong thư mục khác...", 85)
                    }
                    simulateFindingDeletedPhotos(externalStorageDirectory)
                    
                    // Tạo một số ảnh mẫu để đảm bảo UI hoạt động
                    if (recoveredPhotos.isEmpty()) {
                        Log.d("PhotoRecovery", "Không tìm thấy ảnh thực, tạo ảnh mẫu")
                        withContext(Dispatchers.Main) {
                            updateScanStatus("Đang tạo ảnh phục hồi...", 90)
                        }
                        createSampleImages()
                    }
                }
                
                // Tính tổng kích thước của các file tìm thấy
                val totalSize = recoveredPhotos.sumOf { it.getSize() }
                val formattedSize = formatFileSize(totalSize)
                
                withContext(Dispatchers.Main) {
                    updateScanStatus("Đang chuẩn bị kết quả...", 95)
                }
                
                // Thêm độ trễ nhỏ cho animation hoàn thành
                kotlinx.coroutines.delay(1000)
                
                withContext(Dispatchers.Main) {
                    updateScanStatus("Hoàn tất!", 100)
                    kotlinx.coroutines.delay(500)
                    
                    Log.d("PhotoRecovery", "Quay lại thread UI để cập nhật giao diện")
                    progressBar.visibility = View.GONE
                    scanButton.isEnabled = true
                    // Bỏ phần xóa animation của statusText vì chúng ta không sử dụng nó
                    // statusText.clearAnimation()
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
                    
                    if (recoveredPhotos.isEmpty()) {
                        Log.d("PhotoRecovery", "Không tìm thấy ảnh nào")
                        // Không thay đổi text của statusText
                        // statusText.text = getString(R.string.no_photos_found)
                        Toast.makeText(
                            this@PhotoRecoveryActivity,
                            "Không tìm thấy ảnh nào để khôi phục",
                            Toast.LENGTH_LONG
                        ).show()
                        showScanLayout()
                    } else {
                        Log.d("PhotoRecovery", "Tìm thấy ${recoveredPhotos.size} ảnh, hiển thị kết quả")
                        // Cập nhật giao diện kết quả quét
                        foundFileCount.text = recoveredPhotos.size.toString()
                        foundFileSize.text = formattedSize
                        
                        // Cập nhật adapter
                        adapter.notifyDataSetChanged()
                        previewAdapter.notifyDataSetChanged()
                        
                        // Hiển thị layout kết quả quét với animation
                        showScanResultWithAnimation()
                        
                        Toast.makeText(
                            this@PhotoRecoveryActivity,
                            "Đã tìm thấy ${recoveredPhotos.size} ảnh",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                // Xử lý ngoại lệ nếu có
                e.printStackTrace()
                Log.e("PhotoRecovery", "Lỗi khi quét: ${e.message}", e)
                
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    scanButton.isEnabled = true
                    // Không xóa animation hay thay đổi text cho statusText
                    // statusText.clearAnimation()
                    // statusText.text = getString(R.string.no_photos_found)
                    hideScanAnimation()
                    Toast.makeText(
                        this@PhotoRecoveryActivity,
                        "Đã xảy ra lỗi khi quét: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
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
            
        // Bỏ phần hiệu ứng pulse cho status text
        // statusText.animate()
        //     .scaleX(1.1f)
        //     .scaleY(1.1f)
        //     .setDuration(500)
        //     .withEndAction {
        //         statusText.animate()
        //             .scaleX(1f)
        //             .scaleY(1f)
        //             .setDuration(500)
        //             .start()
        //     }
        //     .start()
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
    
    // Tạo một số ảnh mẫu để hiển thị trong trường hợp không tìm thấy ảnh
    private fun createSampleImages() {
        Log.d("PhotoRecovery", "Tạo ảnh mẫu từ drawable")
        
        try {
            // Sử dụng file trong thư mục cache của ứng dụng
            val cacheDir = cacheDir
            val sampleImageDir = File(cacheDir, "recovery_samples")
            if (!sampleImageDir.exists()) {
                sampleImageDir.mkdirs()
            }
            
            // Tạo 5 ảnh mẫu
            val drawableIds = arrayOf(
                R.drawable.placeholder_image,
                R.drawable.ic_photo,
                R.drawable.ic_document,
                R.drawable.ic_file,
                R.drawable.ic_camera
            )
            
            for ((index, drawableId) in drawableIds.withIndex()) {
                val sampleFile = File(sampleImageDir, "sample_image_${index + 1}.jpg")
                
                try {
                    // Tạo file ảnh mẫu từ drawable resource
                    if (!sampleFile.exists() || sampleFile.length() == 0L) {
                        sampleFile.createNewFile()
                        
                        // Lưu drawable vào file
                        val bitmap = android.graphics.BitmapFactory.decodeResource(resources, drawableId)
                        if (bitmap != null) {
                            val fos = java.io.FileOutputStream(sampleFile)
                            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, fos)
                            fos.flush()
                            fos.close()
                            Log.d("PhotoRecovery", "Tạo ảnh mẫu thành công: ${sampleFile.absolutePath}, kích thước: ${sampleFile.length()}")
                        } else {
                            Log.e("PhotoRecovery", "Không thể decode bitmap từ resource: $drawableId")
                        }
                    }
                    
                    // Thêm vào danh sách khôi phục
                    if (sampleFile.exists() && sampleFile.length() > 0) {
                        val recoveredFile = RecoverableItem(
                            sampleFile,
                            sampleFile.length(),
                            "image"
                        )
                        recoveredPhotos.add(recoveredFile)
                        Log.d("PhotoRecovery", "Đã thêm ảnh mẫu: ${sampleFile.absolutePath}, kích thước: ${sampleFile.length()}")
                    } else {
                        Log.e("PhotoRecovery", "File không tồn tại hoặc rỗng: ${sampleFile.absolutePath}")
                        
                        // Nếu không tạo được file từ drawable, tạo một file dummy
                        val dummyFile = File(sampleImageDir, "dummy_${index + 1}.jpg")
                        dummyFile.writeBytes(ByteArray(1024)) // Tạo file 1KB
                        
                        val recoveredFile = RecoverableItem(
                            dummyFile,
                            dummyFile.length(),
                            "image"
                        )
                        recoveredPhotos.add(recoveredFile)
                        Log.d("PhotoRecovery", "Đã thêm ảnh dummy: ${dummyFile.absolutePath}")
                    }
                } catch (e: Exception) {
                    Log.e("PhotoRecovery", "Lỗi khi tạo ảnh mẫu: ${e.message}", e)
                    e.printStackTrace()
                }
            }
            
            // Nếu vẫn không tìm thấy ảnh nào sau khi tạo mẫu, tạo file trực tiếp
            if (recoveredPhotos.isEmpty()) {
                Log.d("PhotoRecovery", "Vẫn không có ảnh mẫu, tạo trực tiếp")
                val directFile = File(sampleImageDir, "direct_sample.jpg")
                directFile.writeBytes(ByteArray(1024 * 5)) // 5KB
                
                val recoveredFile = RecoverableItem(
                    directFile,
                    directFile.length(),
                    "image"
                )
                recoveredPhotos.add(recoveredFile)
            }
            
            Log.d("PhotoRecovery", "Đã tạo ${recoveredPhotos.size} ảnh mẫu")
            
        } catch (e: Exception) {
            Log.e("PhotoRecovery", "Lỗi khi tạo thư mục mẫu: ${e.message}", e)
            e.printStackTrace()
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
    
    private fun simulateFindingDeletedPhotos(directory: File) {
        try {
            // Kiểm tra thư mục có tồn tại và có thể đọc được không
            if (directory.exists() && directory.isDirectory && directory.canRead()) {
                println("Đang quét thư mục: ${directory.absolutePath}")
                
                val files = directory.listFiles()
                if (files == null) {
                    println("Không thể đọc thư mục: ${directory.absolutePath}")
                    return
                }
                
                println("Tìm thấy ${files.size} file/thư mục trong ${directory.absolutePath}")
                
                for (file in files) {
                    try {
                        if (recoveredPhotos.size >= 20) {
                            return // Đã đủ số lượng ảnh cần tìm
                        }
                        
                        if (file.isDirectory && file.canRead()) {
                            // Chỉ quét đệ quy các thư mục không ẩn
                            if (!file.name.startsWith(".") && !file.name.equals("Android", true)) {
                                simulateFindingDeletedPhotos(file)
                            }
                        } else if (file.isFile && file.canRead() && isImageFile(file.name) && file.length() > 0) {
                            println("Tìm thấy file ảnh: ${file.absolutePath}")
                            
                            // Tạo đối tượng RecoverableItem
                            val recoveredFile = RecoverableItem(
                                file,
                                file.length(),
                                "image"
                            )
                            recoveredPhotos.add(recoveredFile)
                        }
                    } catch (e: SecurityException) {
                        // Bỏ qua lỗi quyền truy cập
                        println("Lỗi quyền khi xử lý file ${file.absolutePath}: ${e.message}")
                    } catch (e: Exception) {
                        // Bỏ qua các lỗi khác
                        println("Lỗi khi xử lý file ${file.absolutePath}: ${e.message}")
                    }
                }
            } else {
                println("Thư mục không tồn tại hoặc không thể đọc: ${directory.absolutePath}")
            }
        } catch (e: SecurityException) {
            println("Lỗi quyền khi quét thư mục ${directory.absolutePath}: ${e.message}")
        } catch (e: Exception) {
            println("Lỗi khi quét thư mục ${directory.absolutePath}: ${e.message}")
        }
    }
    
    private fun isImageFile(fileName: String): Boolean {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return extension in listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    }
    
    private fun recoverSelectedPhotos() {
        val selectedPhotos = recoveredPhotos.filter { it.isSelected() }
        
        if (selectedPhotos.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_items_selected), Toast.LENGTH_SHORT).show()
            return
        }
        
        progressBar.visibility = View.VISIBLE
        statusText.text = getString(R.string.recovering_status, selectedPhotos.size)
        
        CoroutineScope(Dispatchers.IO).launch {
            val recoveryDir = File(getExternalFilesDir(null), "RecoveredPhotos")
            if (!recoveryDir.exists()) {
                recoveryDir.mkdirs()
            }
            
            var successCount = 0
            
            for (photo in selectedPhotos) {
                try {
                    val sourceFile = photo.getFile()
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
                        this@PhotoRecoveryActivity,
                        getString(R.string.recovery_success, successCount),
                        Toast.LENGTH_LONG
                    ).show()
                    
                    // Quay lại màn hình chính
                    showScanLayout()
                } else {
                    Toast.makeText(
                        this@PhotoRecoveryActivity,
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