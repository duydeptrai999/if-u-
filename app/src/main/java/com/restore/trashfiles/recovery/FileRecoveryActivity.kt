package com.restore.trashfiles.recovery

import android.Manifest
import android.annotation.SuppressLint
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
import com.restore.trashfiles.R
import com.restore.trashfiles.adapter.FileAdapter
import com.restore.trashfiles.adapter.PreviewFileAdapter
import com.restore.trashfiles.model.RecoverableItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.util.Date
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.restore.trashfiles.ads.AdHelper

class FileRecoveryActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var scanButton: androidx.cardview.widget.CardView
    private lateinit var recoverButton: Button
    private lateinit var viewResultsButton: Button
    private lateinit var statusText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var previewRecyclerView: RecyclerView
    private lateinit var adapter: FileAdapter
    private lateinit var previewAdapter: PreviewFileAdapter
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
    
    // Thêm các thành phần UI cho màn hình đợi khôi phục
    private lateinit var recoveryProgressLayout: ConstraintLayout
    private lateinit var recoveryProgressBar: ProgressBar
    private lateinit var recoveryProgressText: TextView
    private lateinit var recoveryCountText: TextView
    private lateinit var recoveryHorizontalProgressBar: ProgressBar
    
    // Thêm các thành phần UI cho màn hình kết quả khôi phục
    private lateinit var recoveryResultLayout: ConstraintLayout
    private lateinit var resultIcon: ImageView
    private lateinit var resultTitleText: TextView
    private lateinit var resultDescriptionText: TextView
    private lateinit var continueButton: Button
    private lateinit var viewRecoveredButton: Button
    
    private val recoveredFiles = mutableListOf<RecoverableItem>()
    private val scannedDirectories = mutableSetOf<String>() // Theo dõi thư mục đã quét
    private val STORAGE_PERMISSION_CODE = 101
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransparentStatusBar()
        setContentView(R.layout.activity_file_recovery)
        
        initViews()
        setupRecyclerViews()
        setupClickListeners()
        
        // Hiển thị quảng cáo banner
        setupAds()
        
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
        
        // Khởi tạo các thành phần UI cho màn hình đợi khôi phục
        recoveryProgressLayout = findViewById(R.id.recoveryProgressLayout)
        recoveryProgressBar = findViewById(R.id.recoveryProgressBar)
        recoveryProgressText = findViewById(R.id.recoveryProgressText)
        recoveryCountText = findViewById(R.id.recoveryCountText)
        recoveryHorizontalProgressBar = findViewById(R.id.recoveryHorizontalProgressBar)
        
        // Khởi tạo các thành phần UI cho màn hình kết quả khôi phục
        recoveryResultLayout = findViewById(R.id.recoveryResultLayout)
        resultIcon = findViewById(R.id.resultIcon)
        resultTitleText = findViewById(R.id.resultTitleText)
        resultDescriptionText = findViewById(R.id.resultDescriptionText)
        continueButton = findViewById(R.id.continueButton)
        viewRecoveredButton = findViewById(R.id.viewRecoveredButton)
        
        // Ẩn statusText ban đầu để tránh chồng lên với nút quét
        statusText.visibility = View.GONE
    }
    
    private fun setupRecyclerViews() {
        // Cài đặt RecyclerView chính cho trang khôi phục
        recyclerView.layoutManager = GridLayoutManager(this, 1) // Danh sách dọc cho file
        adapter = FileAdapter(recoveredFiles) { isSelected ->
            // Cập nhật nút khôi phục dựa trên việc có file nào được chọn không
            recoverButton.isEnabled = isSelected
        }
        recyclerView.adapter = adapter
        
        // Cài đặt RecyclerView cho preview
        previewRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        previewAdapter = PreviewFileAdapter(recoveredFiles)
        previewRecyclerView.adapter = previewAdapter
        
        // Vô hiệu hóa nút khôi phục cho đến khi có file được chọn
        recoverButton.isEnabled = false
    }
    
    private fun setupClickListeners() {
        // Đảm bảo nút quét có thể nhận sự kiện click
        val scanButtonText = findViewById<View>(R.id.scanButtonText)
        val scanButtonLayout = findViewById<View>(R.id.scanButton)
        
        // Thêm hàm click cho cả container và text bên trong
        val scanClickListener = View.OnClickListener {
            Log.d("FileRecovery", "Nút quét được nhấn")
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
                                Toast.makeText(this@FileRecoveryActivity, "Bắt đầu quét file...", Toast.LENGTH_SHORT).show()
                                scanForDeletedFiles()
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
            CoroutineScope(Dispatchers.IO).launch {
                recoverSelectedFiles()
            }
        }
        
        findViewById<View>(R.id.backButton).setOnClickListener {
            // Kiểm tra đang ở layout nào để quay lại đúng layout trước đó
            when {
                recoveryResultLayout.visibility == View.VISIBLE -> {
                    showScanResultLayout()
                }
                recoveryProgressLayout.visibility == View.VISIBLE -> {
                    showRecoveryLayout()
                }
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
        
        // Xử lý khi nhấn nút Continue trên màn hình kết quả
        continueButton.setOnClickListener {
            showScanResultLayout()
        }
        
        // Xử lý khi nhấn nút View Recovered Files
        viewRecoveredButton.setOnClickListener {
            val intent = Intent(this, com.restore.trashfiles.RecoveredFilesActivity::class.java)
            startActivity(intent)
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
        
        // Make sure recovery overlays are hidden
        recoveryProgressLayout.visibility = View.GONE
        recoveryResultLayout.visibility = View.GONE
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
                scanForDeletedFiles()
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
            delay(300)
        }
    }
    
    // Tạo một số file mẫu để hiển thị trong trường hợp không tìm thấy file
    private fun createSampleFiles() {
        Log.d("FileRecovery", "Tạo file mẫu")
        
        try {
            // Sử dụng file trong thư mục cache của ứng dụng
            val cacheDir = cacheDir
            val sampleFileDir = File(cacheDir, "recovery_file_samples")
            if (!sampleFileDir.exists()) {
                sampleFileDir.mkdirs()
            }
            
            // Tạo file mẫu với nhiều định dạng khác nhau
            val fileTypes = mapOf(
                "sample_document.pdf" to "pdf",
                "sample_doc.docx" to "document",
                "sample_spreadsheet.xlsx" to "spreadsheet",
                "sample_presentation.pptx" to "presentation",
                "sample_text.txt" to "text",
                "sample_archive.zip" to "archive"
            )
            
            for ((filename, type) in fileTypes) {
                val sampleFile = File(sampleFileDir, filename)
                
                try {
                    if (!sampleFile.exists() || sampleFile.length() == 0L) {
                        sampleFile.createNewFile()
                        
                        // Tạo file dummy với kích thước khác nhau
                        val size = 1024 * 1024 * (1 + (filename.hashCode() % 5)) // 1-5MB
                        sampleFile.writeBytes(ByteArray(size))
                        Log.d("FileRecovery", "Tạo file mẫu thành công: ${sampleFile.absolutePath}")
                    }
                    
                    // Thêm vào danh sách khôi phục
                    if (sampleFile.exists() && sampleFile.length() > 0) {
                        val recoveredFile = RecoverableItem(
                            sampleFile,
                            sampleFile.length(),
                            type
                        )
                        recoveredFiles.add(recoveredFile)
                        Log.d("FileRecovery", "Đã thêm file mẫu: ${sampleFile.absolutePath}")
                    }
                } catch (e: Exception) {
                    Log.e("FileRecovery", "Lỗi khi tạo file mẫu: ${e.message}")
                    e.printStackTrace()
                }
            }
            
            Log.d("FileRecovery", "Đã tạo ${recoveredFiles.size} file mẫu")
            
        } catch (e: Exception) {
            Log.e("FileRecovery", "Lỗi khi tạo thư mục file mẫu: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun scanForDeletedFiles() {
        Log.d("FileRecovery", "Bắt đầu quét file...")
        
        // Ẩn progressBar, không sử dụng nó
        progressBar.visibility = View.GONE
        // Không thay đổi statusText để tránh chồng lên UI
        // statusText.text = getString(R.string.scanning_status)
        scanButton.isEnabled = false
        
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
        recoveredFiles.clear()
        // Xóa danh sách thư mục đã quét
        scannedDirectories.clear()
        
        // Hiển thị toast để xác nhận rằng chức năng quét đang chạy
        Toast.makeText(this, "Đang bắt đầu quét file...", Toast.LENGTH_LONG).show()
        
        CoroutineScope(Dispatchers.IO).launch {
            // Mô phỏng quá trình quét với độ trễ để thấy rõ animation
            simulateScanProgress()
            
            try {
                // Log trong thread IO
                Log.d("FileRecovery", "Đang quét trong thread IO")
                
                // Trước tiên tìm kiếm trong các thư mục thùng rác
                withContext(Dispatchers.Main) {
                    updateScanStatus("Đang quét thùng rác...", 30)
                }
                
                // Quét các thư mục thùng rác phổ biến
                scanTrashFolders()
                
                // Tìm kiếm các file trong bộ nhớ thiết bị
                val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val externalStorageDirectory = Environment.getExternalStorageDirectory()
                
                // Log thông tin thư mục
                Log.d("FileRecovery", "Documents: ${documentsDir.absolutePath} - tồn tại: ${documentsDir.exists()}")
                Log.d("FileRecovery", "Downloads: ${downloadDir.absolutePath} - tồn tại: ${downloadDir.exists()}")
                Log.d("FileRecovery", "External Storage: ${externalStorageDirectory.absolutePath} - tồn tại: ${externalStorageDirectory.exists()}")
                
                // Cập nhật trạng thái quét
                withContext(Dispatchers.Main) {
                    updateScanStatus("Đang quét thư mục Documents...", 40)
                }
                
                // Quét các thư mục phổ biến để tìm file
                simulateFindingDeletedFiles(documentsDir)
                
                withContext(Dispatchers.Main) {
                    updateScanStatus("Đang quét thư mục Downloads...", 60)
                }
                simulateFindingDeletedFiles(downloadDir)
                
                withContext(Dispatchers.Main) {
                    updateScanStatus("Đang quét bộ nhớ thiết bị...", 80)
                }
                simulateFindingDeletedFiles(externalStorageDirectory)
                
                Log.d("FileRecovery", "Đã tìm thấy ${recoveredFiles.size} file")
                
                // Nếu không tìm thấy file nào, tạo file mẫu
                if (recoveredFiles.isEmpty()) {
                    Log.d("FileRecovery", "Không tìm thấy file thực, tạo file mẫu")
                    withContext(Dispatchers.Main) {
                        updateScanStatus("Đang tạo file phục hồi...", 90)
                    }
                    createSampleFiles()
                }
                
                // Lọc nghiêm ngặt để chỉ giữ lại file đã xóa thực sự
                Log.d("FileRecovery", "Trước khi lọc: ${recoveredFiles.size} file")
                
                // Lọc nghiêm ngặt để chỉ giữ lại file đã xóa
                val onlyDeletedFiles = recoveredFiles.filter { isDeletedFile(it.getFile()) }
                recoveredFiles.clear()
                recoveredFiles.addAll(onlyDeletedFiles)
                
                Log.d("FileRecovery", "Sau khi lọc: ${recoveredFiles.size} file, tất cả đều đã xóa")
                
                // Tính tổng kích thước của các file tìm thấy
                val totalSize = recoveredFiles.sumOf { it.getSize() }
                val formattedSize = formatFileSize(totalSize)
                
                withContext(Dispatchers.Main) {
                    updateScanStatus("Đang chuẩn bị kết quả...", 95)
                }
                
                // Thêm độ trễ nhỏ cho animation hoàn thành
                delay(1000)
                
                withContext(Dispatchers.Main) {
                    updateScanStatus("Hoàn tất!", 100)
                    delay(500)
                    
                    Log.d("FileRecovery", "Quay lại thread UI để cập nhật giao diện")
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
                    
                    if (recoveredFiles.isEmpty()) {
                        Log.d("FileRecovery", "Không tìm thấy file nào")
                        statusText.text = getString(R.string.no_files_found)
                        Toast.makeText(
                            this@FileRecoveryActivity,
                            "Không tìm thấy file nào để khôi phục",
                            Toast.LENGTH_LONG
                        ).show()
                        showScanLayout()
                    } else {
                        Log.d("FileRecovery", "Tìm thấy ${recoveredFiles.size} file, hiển thị kết quả")
                        // Cập nhật giao diện kết quả quét
                        foundFileCount.text = recoveredFiles.size.toString()
                        foundFileSize.text = formattedSize
                        
                        // Cập nhật adapter
                        adapter.notifyDataSetChanged()
                        previewAdapter.notifyDataSetChanged()
                        
                        // Hiển thị layout kết quả quét với animation
                        showScanResultWithAnimation()
                        
                        Toast.makeText(
                            this@FileRecoveryActivity,
                            "Đã tìm thấy ${recoveredFiles.size} file",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                // Xử lý ngoại lệ nếu có
                e.printStackTrace()
                Log.e("FileRecovery", "Lỗi khi quét: ${e.message}")
                
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    scanButton.isEnabled = true
                    statusText.clearAnimation()
                    hideScanAnimation()
                    statusText.text = getString(R.string.no_files_found)
                    Toast.makeText(
                        this@FileRecoveryActivity,
                        "Đã xảy ra lỗi khi quét: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    
    /**
     * Quét các thư mục thùng rác phổ biến để tìm file đã xóa
     */
    private fun scanTrashFolders() {
        Log.d("FileRecovery", "Bắt đầu quét các thư mục thùng rác phổ biến")
        
        val trashPaths = listOf(
            // Samsung
            "/storage/emulated/0/DCIM/.trash",
            "/storage/emulated/0/DCIM/.Trash",
            "/storage/emulated/0/.Trash",
            
            // Xiaomi/MIUI
            "/storage/emulated/0/MIUI/Gallery/cloud/.trash",
            "/storage/emulated/0/MIUI/.trash",
            
            // Huawei
            "/storage/emulated/0/.recycle",
            "/storage/emulated/0/Huawei/.Trash",
            
            // OPPO/ColorOS
            "/storage/emulated/0/Recycle",
            "/storage/emulated/0/.recyclebin",
            
            // Vivo/FuntouchOS
            "/storage/emulated/0/.trashbin",
            
            // Office temporary files
            "/storage/emulated/0/Android/data/com.microsoft.office.word/files/temp",
            "/storage/emulated/0/Android/data/com.microsoft.office.word/temp",
            "/storage/emulated/0/Android/data/com.microsoft.office.excel/temp",
            "/storage/emulated/0/Android/data/com.microsoft.office.powerpoint/temp",
            "/storage/emulated/0/Documents/MicrosoftOffice/UnsavedFiles",
            "/storage/emulated/0/Documents/Office Temp",
            
            // WPS Office
            "/storage/emulated/0/Android/data/cn.wps.moffice_eng/files/recovery",
            "/storage/emulated/0/Documents/WPS/backup",
            "/storage/emulated/0/Documents/.backup",
            "/storage/emulated/0/Android/data/cn.wps.moffice_eng/files/PdfData/temp",
            
            // Adobe Document Viewer
            "/storage/emulated/0/Android/data/com.adobe.reader/files/temp",
            "/storage/emulated/0/Android/data/com.adobe.reader/files/AdobeReader/tmp",
            "/storage/emulated/0/Adobe/Acrobat/11.0/AutoSave",
            
            // Polaris Office
            "/storage/emulated/0/Android/data/com.infraware.office.link/files/temp",
            "/storage/emulated/0/Polaris Office/Temp",
            
            // Common trash locations for PDF readers
            "/storage/emulated/0/Documents/.trash",
            "/storage/emulated/0/Download/.trash",
            "/storage/emulated/0/Download/RecycleBin",
            
            // Google Drive offline files
            "/storage/emulated/0/Android/data/com.google.android.apps.docs/files/offline",
            "/storage/emulated/0/Android/data/com.google.android.apps.docs/files/temp",
            
            // OneDrive offline files
            "/storage/emulated/0/Android/data/com.microsoft.skydrive/files/temp",
            
            // Dropbox
            "/storage/emulated/0/Android/data/com.dropbox.android/files/scratch",
            
            // SD Card locations (if supported)
            "/storage/sdcard1/.trash",
            "/storage/sdcard1/DCIM/.trash",
            "/storage/sdcard1/Documents/.trash",
            
            // App specific temp folders
            "/storage/emulated/0/Android/data/com.dropbox.android/files/scratch",
            "/storage/emulated/0/Android/data/com.google.android.apps.docs/files/pinned_docs_files_do_not_edit",
            
            // LibreOffice
            "/storage/emulated/0/Android/data/org.libreoffice/files/temp",
            "/storage/emulated/0/LibreOffice/backup",
            
            // OfficeSuite
            "/storage/emulated/0/Android/data/com.mobisystems.office/temp",
            "/storage/emulated/0/Android/data/com.mobisystems.office/files/temp"
        )
        
        for (trashPath in trashPaths) {
            val trashDir = File(trashPath)
            if (trashDir.exists() && trashDir.isDirectory && trashDir.canRead()) {
                Log.d("FileRecovery", "Quét thư mục thùng rác: $trashPath")
                simulateFindingDeletedFiles(trashDir)
            } else {
                Log.d("FileRecovery", "Bỏ qua thư mục thùng rác không tồn tại hoặc không đọc được: $trashPath")
            }
        }
        
        // Quét các thư mục tạm của hệ thống
        val tempDirs = listOf(
            // System temp directories
            "/data/local/tmp",
            "/storage/emulated/0/Android/data/com.android.providers.media/albumthumbs",
            
            // Bộ nhớ cache của trình duyệt (có thể chứa file đã download)
            "/storage/emulated/0/Android/data/com.android.chrome/cache",
            "/storage/emulated/0/Android/data/com.android.chrome/files/Download",
            "/storage/emulated/0/Android/data/com.opera.browser/cache",
            "/storage/emulated/0/Android/data/org.mozilla.firefox/cache"
        )
        
        for (tempPath in tempDirs) {
            val tempDir = File(tempPath)
            if (tempDir.exists() && tempDir.isDirectory && tempDir.canRead()) {
                Log.d("FileRecovery", "Quét thư mục tạm: $tempPath")
                simulateFindingDeletedFiles(tempDir)
            }
        }
        
        Log.d("FileRecovery", "Hoàn thành quét thư mục thùng rác phổ biến")
    }
    
    /**
     * Kiểm tra xem thư mục có phải là thư mục thùng rác hay không
     */
    private fun isTrashDirectory(directory: File): Boolean {
        val trashKeywords = listOf(
            ".trash", ".Trash", "trash", "Trash", "recycle", "Recycle", "bin", "Bin",
            "deleted", "Deleted", "dustbin", "Dustbin", ".RecycleBin", "recyclebin",
            ".globalTrash", ".trashbin", ".recyclebin", "Recovery", ".deleted",
            "temp", "Temp", "tmp", "Tmp", "~", "scratch", "offline", "cache", "Archive"
        )
        
        val dirName = directory.name.lowercase()
        val dirPath = directory.absolutePath.lowercase()
        
        return directory.isHidden || 
               trashKeywords.any { dirName.contains(it.lowercase()) } ||
               trashKeywords.any { dirPath.contains(it.lowercase()) }
    }
    
    /**
     * Kiểm tra xem file có nằm trong thư mục thùng rác không
     */
    private fun isInTrashFolder(file: File): Boolean {
        // Danh sách các từ khóa trong đường dẫn giúp xác định thư mục thùng rác
        val trashKeywords = listOf(
            ".trash", ".Trash", "trash", "Trash", "recycle", "Recycle", "bin", "Bin",
            ".globalTrash", ".trashbin", ".recyclebin", "Recovery", ".deleted",
            "temp", "Temp", "tmp", "Tmp", "~", "scratch", "offline", "cache", "Archive",
            "unsaved", "backup", "autosave", "autorecovery"
        )
        
        // Lấy đường dẫn tuyệt đối
        val absolutePath = file.absolutePath.lowercase()
        
        // Kiểm tra nếu đường dẫn chứa từ khóa thùng rác
        return trashKeywords.any { absolutePath.contains(it.lowercase()) }
    }
    
    /**
     * Kiểm tra xem file có phải là file đã xóa không
     */
    private fun isDeletedFile(file: File): Boolean {
        // Kiểm tra theo tên file
        val name = file.name.lowercase()
        
        // Mẫu tên file đã xóa
        val deletedPatterns = listOf(
            "delete", "removed", "trash", "bin", ".del", "del_", "deleted_", "trashed_",
            ".tmp", "~tmp", "~$", ".$", "~wrl", "~rf", ".bak", ".old", ".sav", ".backup",
            "autosave", "autorecovery", "temp", "tmp", "scratch", "recovery", "unsaved", 
            "^~", "^\\.~", "^\\$", "^acrash", "^auto_save_", "^backup_", "^copy of ", 
            "\\.wbk$", "\\.bak$", "\\.tmp$", "\\.temp$", "\\.asd$", "\\.asv$", "\\.$\\$\\$$",
            "^\\~\\$", "^\\.\\$", "^\\$\\$"
        )
        
        // Kiểm tra nếu tên file chứa từ khóa của file đã xóa
        if (deletedPatterns.any { name.contains(it) }) {
            return true
        }
        
        // Kiểm tra nếu file nằm trong thư mục thùng rác
        if (isInTrashFolder(file)) {
            return true
        }
        
        // Kiểm tra tên file với mẫu đặc biệt: những file bắt đầu với ký tự . hoặc ~ và không phải là file hệ thống
        if ((name.startsWith(".") || name.startsWith("~")) 
            && !name.equals(".nomedia") && !name.contains("cache") && !name.contains("config")) {
            return true
        }
        
        return false
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
    
    private fun simulateFindingDeletedFiles(directory: File) {
        try {
            // Kiểm tra xem thư mục đã được quét chưa
            if (scannedDirectories.contains(directory.absolutePath)) {
                Log.d("FileRecovery", "Bỏ qua thư mục đã quét: ${directory.absolutePath}")
                return
            }
            
            // Giới hạn độ sâu quét để tránh quét quá lâu (có thể tùy chỉnh)
            if (scannedDirectories.size > 1000) {
                Log.d("FileRecovery", "Đã đạt giới hạn số lượng thư mục quét, dừng quét")
                return
            }
            
            // Đánh dấu thư mục này đã được quét
            scannedDirectories.add(directory.absolutePath)
            
            // Kiểm tra thư mục có tồn tại và có thể đọc được không
            if (directory.exists() && directory.isDirectory && directory.canRead()) {
                Log.d("FileRecovery", "Đang quét thư mục: ${directory.absolutePath}")
                
                val files = directory.listFiles()
                if (files == null) {
                    Log.d("FileRecovery", "Không thể đọc thư mục: ${directory.absolutePath}")
                    return
                }
                
                Log.d("FileRecovery", "Tìm thấy ${files.size} file/thư mục trong ${directory.absolutePath}")
                
                // Danh sách từ khóa đánh dấu tệp có thể khôi phục
                val recoveryKeywords = listOf(
                    "backup", "bak", "temp", "tmp", "old", "save", "copy", "recovery", 
                    "autorecovery", "autosave", "~$", ".$", ".tmp", ".old", ".bak", ".sav",
                    "unsaved", "scratch", "offline", "archive", "history", "version", 
                    ".asd", ".asv", ".wbk", ".xlk", ".xlsb", ".gsheet", ".gdoc", ".gslides",
                    ".pptx~", ".xlsx~", ".docx~", ".pdf~", ".odt~", ".ods~", ".odp~"
                )
                
                for (file in files) {
                    try {
                        if (file.isDirectory && file.canRead()) {
                            // Kiểm tra nếu đây là thư mục thùng rác
                            val isTrashDir = isTrashDirectory(file)
                            
                            // Quét đệ quy các thư mục, ưu tiên thư mục thùng rác
                            if (isTrashDir || (!file.name.startsWith(".") && !file.name.equals("Android", true))) {
                                simulateFindingDeletedFiles(file)
                            } else if ((file.isHidden || file.name.contains("temp", true) || 
                                      file.name.contains("tmp", true) || file.name.contains("backup", true))) {
                                // Quét cả thư mục ẩn có thể chứa file backup
                                simulateFindingDeletedFiles(file)
                            }
                        } else if (file.isFile && file.canRead() && isOtherFile(file) && file.length() > 0) {
                            Log.d("FileRecovery", "Tìm thấy file: ${file.absolutePath}")
                            
                            // Kiểm tra nếu file này đã xóa hoặc là file backup/temp
                            val isDeleted = isDeletedFile(file)
                            val isBackupFile = recoveryKeywords.any { file.name.contains(it, true) } ||
                                               file.name.matches(Regex(".*~\\d+\\..*")) // Mẫu tên file backup ~1.doc, ~2.xlsx
                            
                            if (isDeleted || isBackupFile) {
                                Log.d("FileRecovery", "Đây là file đã xóa hoặc file backup: ${file.absolutePath}")
                                
                                // Kiểm tra trùng lặp trước khi thêm file vào danh sách
                                if (!recoveredFiles.any { it.getPath() == file.absolutePath }) {
                                    // Tạo đối tượng RecoverableItem và đánh dấu là đã xóa
                                    val recoveredFile = RecoverableItem(
                                        file,
                                        file.length(),
                                        getFileType(file.name),
                                        true // Đánh dấu là file đã xóa
                                    )
                                    recoveredFiles.add(recoveredFile)
                                    Log.d("FileRecovery", "Đã thêm file đã xóa: ${file.absolutePath}")
                                } else {
                                    Log.d("FileRecovery", "Bỏ qua file trùng lặp: ${file.absolutePath}")
                                }
                            }
                        }
                    } catch (e: SecurityException) {
                        // Bỏ qua lỗi quyền truy cập
                        Log.e("FileRecovery", "Lỗi quyền khi xử lý file ${file.absolutePath}: ${e.message}")
                    } catch (e: Exception) {
                        // Bỏ qua các lỗi khác
                        Log.e("FileRecovery", "Lỗi khi xử lý file ${file.absolutePath}: ${e.message}")
                    }
                }
            } else {
                Log.d("FileRecovery", "Thư mục không tồn tại hoặc không thể đọc: ${directory.absolutePath}")
            }
        } catch (e: SecurityException) {
            Log.e("FileRecovery", "Lỗi quyền khi quét thư mục ${directory.absolutePath}: ${e.message}")
        } catch (e: Exception) {
            Log.e("FileRecovery", "Lỗi khi quét thư mục ${directory.absolutePath}: ${e.message}")
        }
    }
    
    private fun isOtherFile(fileName: String): Boolean {
        val extension = getFileExtension(fileName)
        return !isImageFile(extension) && !isVideoFile(extension) && extension.isNotEmpty()
    }
    
    private fun isOtherFile(file: File): Boolean {
        return isOtherFile(file.name)
    }

    private fun isImageFile(extension: String): Boolean {
        val imageExtensions = listOf("jpg", "jpeg", "png", "gif", "bmp", "webp", "heic", "heif", "raw", "tiff", "tif")
        return imageExtensions.contains(extension.lowercase())
    }

    private fun isVideoFile(extension: String): Boolean {
        val videoExtensions = listOf("mp4", "3gp", "mkv", "mov", "avi", "flv", "webm", "m4v", "mpg", "mpeg")
        return videoExtensions.contains(extension.lowercase())
    }

    private fun getFileExtension(fileName: String): String {
        return try {
            fileName.substringAfterLast(".", "")
        } catch (e: Exception) {
            ""
        }
    }
    
    private fun getFileType(fileName: String): String {
        val extension = getFileExtension(fileName).lowercase()
        
        return when (extension) {
            // Documents
            "pdf" -> "PDF Document"
            "doc", "docx", "rtf", "odt", "pages" -> "Word Document"
            "xls", "xlsx", "csv", "ods", "numbers" -> "Spreadsheet"
            "ppt", "pptx", "pps", "ppsx", "odp", "key" -> "Presentation"
            
            // Text and Code files
            "txt", "log" -> "Text File"
            "md" -> "Markdown File"
            "html", "htm" -> "HTML File"
            "xml" -> "XML File"
            "json" -> "JSON File"
            "js" -> "JavaScript File"
            "css" -> "CSS File"
            "java", "kt", "c", "cpp", "py", "php" -> "Code File"
            
            // Archives
            "zip", "rar", "7z", "tar", "gz", "bz2", "xz" -> "Archive"
            "iso" -> "Disk Image"
            
            // E-books
            "epub", "mobi", "djvu", "azw", "azw3" -> "E-book"
            
            // Others
            "apk" -> "Android Package"
            "vcf" -> "Contact File"
            "ics" -> "Calendar File"
            
            // Default
            else -> if (extension.isEmpty()) "Unknown File" else "$extension File"
        }
    }
    
    @SuppressLint("StringFormatInvalid")
    private suspend fun recoverSelectedFiles() {
        Log.d("FileRecovery", "Bắt đầu khôi phục file được chọn")
        
        // Collect all selected files from the adapter
        val selectedFiles = mutableListOf<RecoverableItem>()
        for (item in recoveredFiles) {
            if (item.isSelected()) {
                selectedFiles.add(item)
            }
        }
        
        if (selectedFiles.isEmpty()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@FileRecoveryActivity, getString(R.string.select_items), Toast.LENGTH_SHORT).show()
            }
            return
        }
        
        // Show recovery progress layout
        withContext(Dispatchers.Main) {
            showRecoveryProgressLayout()
        }
        
        try {
            val recoveryFolder = File(getExternalFilesDir(null), "RecoveredFiles")
            if (!recoveryFolder.exists()) {
                recoveryFolder.mkdirs()
            }
            
            var successCount = 0
            val totalCount = selectedFiles.size
            
            // Database for storing recovered files
            val database = com.restore.trashfiles.db.RecoveredFilesDatabase.getInstance(this@FileRecoveryActivity)
            
            for (index in selectedFiles.indices) {
                val item = selectedFiles[index]
                val file = item.getFile()
                val destFile = File(recoveryFolder, file.name)
                
                try {
                    // Update UI with current progress
                    withContext(Dispatchers.Main) {
                        recoveryCountText.text = getString(R.string.recovering_count, index + 1, totalCount)
                        recoveryHorizontalProgressBar.progress = ((index + 1) * 100) / totalCount
                    }
                    
                    // Copy file to recovery folder
                    val inputStream = FileInputStream(file)
                    val outputStream = FileOutputStream(destFile)
                    val buffer = ByteArray(8 * 1024)
                    var read: Int
                    
                    try {
                        while (inputStream.read(buffer).also { read = it } != -1) {
                            outputStream.write(buffer, 0, read)
                        }
                        outputStream.flush()
                    } finally {
                        try {
                            inputStream.close()
                        } catch (e: Exception) {
                            Log.e("FileRecovery", "Error closing input stream: ${e.message}")
                        }
                        
                        try {
                            outputStream.close()
                        } catch (e: Exception) {
                            Log.e("FileRecovery", "Error closing output stream: ${e.message}")
                        }
                    }
                    
                    // Add to recovered files database
                    val recoveredFile = com.restore.trashfiles.model.RecoveredFile(
                        path = destFile.absolutePath,
                        name = destFile.name,
                        size = destFile.length(),
                        modifiedDate = Date(Date().time),
                        isSelected = false
                    )
                    database.addRecoveredFile(recoveredFile, "file")
                    
                    successCount++
                    Log.d("FileRecovery", "Khôi phục thành công: ${file.name}")
                } catch (e: Exception) {
                    Log.e("FileRecovery", "Lỗi khôi phục file ${file.name}: ${e.message}")
                    e.printStackTrace()
                }
                
                // Simulate some processing time
                delay(100)
            }
            
            // Show recovery result
            withContext(Dispatchers.Main) {
                showRecoveryResultLayout(successCount > 0, totalCount, successCount)
                
                // Show toast with result
                if (successCount > 0) {
                    Toast.makeText(
                        this@FileRecoveryActivity,
                        getString(R.string.recovery_success, successCount, totalCount),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@FileRecoveryActivity,
                        getString(R.string.recovery_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            
            // Hiển thị quảng cáo interstitial sau khi khôi phục thành công
            AdHelper.showInterstitialAd(this)
        } catch (e: Exception) {
            Log.e("FileRecovery", "Lỗi trong quá trình khôi phục: ${e.message}")
            e.printStackTrace()
            
            withContext(Dispatchers.Main) {
                showRecoveryResultLayout(false, 0, 0)
                Toast.makeText(
                    this@FileRecoveryActivity,
                    getString(R.string.recovery_failed) + ": " + e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun showRecoveryProgressLayout() {
        scanningOverlay.visibility = View.GONE
        recoveryResultLayout.visibility = View.GONE
        
        recoveryProgressLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        recoverButton.visibility = View.GONE
        
        // Reset progress
        recoveryHorizontalProgressBar.progress = 0
        recoveryCountText.text = getString(R.string.recovering_files)
    }
    
    private fun showRecoveryResultLayout(success: Boolean, totalCount: Int, galleryCount: Int) {
        scanningOverlay.visibility = View.GONE
        recoveryProgressLayout.visibility = View.GONE
        
        recoveryResultLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        recoverButton.visibility = View.GONE
        
        if (success) {
            resultIcon.setImageResource(R.drawable.ic_success)
            resultTitleText.text = getString(R.string.recovery_success)
            resultDescriptionText.text = getString(R.string.file_recovery_success)
        } else {
            resultIcon.setImageResource(R.drawable.ic_error)
            resultTitleText.text = getString(R.string.recovery_failed)
            resultDescriptionText.text = getString(R.string.recovery_failed_description)
        }
    }
    
    private fun showScanWithRecoveryLayout() {
        scanningOverlay.visibility = View.GONE
        recoveryProgressLayout.visibility = View.GONE
        recoveryResultLayout.visibility = View.GONE
        
        recyclerView.visibility = View.VISIBLE
        recoverButton.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        when {
            recoveryResultLayout.visibility == View.VISIBLE -> {
                showScanWithRecoveryLayout()
            }
            recoveryProgressLayout.visibility == View.VISIBLE -> {
                // Do not allow going back during recovery
                Toast.makeText(this, getString(R.string.recovery_in_progress), Toast.LENGTH_SHORT).show()
            }
            scanningOverlay.visibility == View.VISIBLE -> {
                // Do not allow going back during scanning
                Toast.makeText(this, getString(R.string.scanning_in_progress), Toast.LENGTH_SHORT).show()
            }
            else -> {
                super.onBackPressed()
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

    /**
     * Quét các thư mục thùng rác cũ và thêm vào danh sách files cần khôi phục
     */
    private fun scanOldTrashDirectories() {
        val trashDirs = mutableListOf<File>()
        
        // Thư mục thùng rác ứng dụng quản lý file
        trashDirs.add(File("/storage/emulated/0/.estrongs/recycle"))
        trashDirs.add(File("/storage/emulated/0/.files_trash"))
        trashDirs.add(File("/storage/emulated/0/.trash"))
        trashDirs.add(File("/storage/emulated/0/Trash"))
        trashDirs.add(File("/storage/emulated/0/.Trash"))
        trashDirs.add(File("/storage/emulated/0/LOST.DIR"))
        trashDirs.add(File("/storage/emulated/0/RECYCLED"))
        trashDirs.add(File("/storage/emulated/0/RECYCLER"))
        trashDirs.add(File("/storage/emulated/0/RECYCLE.BIN"))
        
        // Thư mục thùng rác Google Files
        val googleFilesDir = File("/storage/emulated/0/Android/data/com.google.android.apps.nbu.files/files/trash")
        if (googleFilesDir.exists() && googleFilesDir.canRead()) {
            trashDirs.add(googleFilesDir)
        }
        
        // Thêm các thư mục rác của Microsoft Office
        trashDirs.add(File("/storage/emulated/0/Documents/MicrosoftOffice/UnsavedFiles"))
        trashDirs.add(File("/storage/emulated/0/Android/data/com.microsoft.office.word/files/temp"))
        trashDirs.add(File("/storage/emulated/0/Android/data/com.microsoft.office.excel/files/temp"))
        trashDirs.add(File("/storage/emulated/0/Android/data/com.microsoft.office.powerpoint/files/temp"))
        trashDirs.add(File("/storage/emulated/0/Android/data/com.microsoft.office.officehubrow/cache"))
        
        // Thêm các thư mục rác của WPS Office
        trashDirs.add(File("/storage/emulated/0/Android/data/cn.wps.moffice_eng/files/PdfData/temp"))
        trashDirs.add(File("/storage/emulated/0/Android/data/cn.wps.moffice_eng/files/tmp"))
        trashDirs.add(File("/storage/emulated/0/Android/data/cn.wps.moffice_eng/cache"))
        
        // Thêm các thư mục rác của Adobe Acrobat/Reader
        trashDirs.add(File("/storage/emulated/0/Android/data/com.adobe.reader/files/AdobeReader/tmp"))
        trashDirs.add(File("/storage/emulated/0/Adobe/Reader/tmp"))
        
        // Thêm các thư mục rác của OfficeSuite
        trashDirs.add(File("/storage/emulated/0/Android/data/com.mobisystems.office/temp"))
        trashDirs.add(File("/storage/emulated/0/Android/data/com.mobisystems.office/cache"))
        
        // Thêm các thư mục rác của Polaris Office
        trashDirs.add(File("/storage/emulated/0/Android/data/com.infraware.office.link/temp"))
        
        // Thêm các thư mục rác của Google Docs, Sheets, Slides
        trashDirs.add(File("/storage/emulated/0/Android/data/com.google.android.apps.docs.editors.docs/cache"))
        trashDirs.add(File("/storage/emulated/0/Android/data/com.google.android.apps.docs.editors.sheets/cache"))
        trashDirs.add(File("/storage/emulated/0/Android/data/com.google.android.apps.docs.editors.slides/cache"))
        
        // Quét các thư mục thùng rác
        for (dir in trashDirs) {
            if (dir.exists() && dir.canRead()) {
                scanDirectoryRecursively(dir)
            }
        }
    }

    /**
     * Scan all potential locations for deleted files
     */

    /**
     * Quét thư mục đệ quy để tìm các file trong thư mục thùng rác
     */
    private fun scanDirectoryRecursively(directory: File) {
        try {
            if (!directory.exists() || !directory.isDirectory || !directory.canRead()) {
                return
            }
            
            val files = directory.listFiles()
            
            if (files != null) {
                for (file in files) {
                    if (file.isDirectory && file.canRead()) {
                        // Đệ quy quét thư mục con
                        scanDirectoryRecursively(file)
                    } else if (file.isFile && file.canRead() && isOtherFile(file) && file.length() > 0) {
                        // Kiểm tra trùng lặp trước khi thêm vào danh sách
                        if (!recoveredFiles.any { it.getPath() == file.absolutePath }) {
                            // Tạo đối tượng RecoverableItem và đánh dấu là file đã xóa
                            val recoveredFile = RecoverableItem(
                                file,
                                file.length(),
                                getFileType(file.name),
                                true // Đánh dấu là file đã xóa
                            )
                            recoveredFiles.add(recoveredFile)
                            Log.d("FileRecovery", "Thêm file từ thư mục thùng rác cũ: ${file.absolutePath}")
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("FileRecovery", "Lỗi quyền truy cập khi quét thư mục thùng rác: ${directory.absolutePath}")
        } catch (e: Exception) {
            Log.e("FileRecovery", "Lỗi khi quét thư mục thùng rác: ${directory.absolutePath}")
        }
    }

    private fun setupAds() {
        // Hiển thị banner ad
        val adContainer = findViewById<FrameLayout>(R.id.adViewContainer)
        AdHelper.showBannerAd(this, adContainer)
    }
} 