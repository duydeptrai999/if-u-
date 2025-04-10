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
import com.htnguyen.ifu.adapter.FileAdapter
import com.htnguyen.ifu.adapter.PreviewFileAdapter
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

class FileRecoveryActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var scanButton: CardView
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
            recoverSelectedFiles()
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
            kotlinx.coroutines.delay(300)
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
                kotlinx.coroutines.delay(1000)
                
                withContext(Dispatchers.Main) {
                    updateScanStatus("Hoàn tất!", 100)
                    kotlinx.coroutines.delay(500)
                    
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
        val trashPaths = listOf(
            // Google Files và các thùng rác phổ biến khác
            "/storage/emulated/0/Android/data/com.google.android.apps.nbu.files/files/.trash",
            "/storage/emulated/0/.trashed-files",
            
            // Samsung
            "/storage/emulated/0/DCIM/.trash", 
            "/storage/emulated/0/DCIM/.trashbin",
            "/storage/emulated/0/Documents/.trash",
            
            // Xiaomi/MIUI 
            "/storage/emulated/0/MIUI/Gallery/cloud/.trash",
            "/storage/emulated/0/MIUI/Files/.trashbin",
            "/storage/emulated/0/DCIM/.globalTrash",
            
            // Huawei
            "/storage/emulated/0/Documents/.recyclebin",
            "/storage/emulated/0/Download/.FileTrash",
            
            // OPPO/ColorOS
            "/storage/emulated/0/Documents/.RecycleBin",
            "/storage/emulated/0/Download/.RecycleBin",
            
            // Vivo
            "/storage/emulated/0/Documents/.DeletedFiles",
            "/storage/emulated/0/.VivoFilesRecycler",
            
            // OnePlus
            "/storage/emulated/0/Documents/.dustbin",
            "/storage/emulated/0/Download/.dustbin"
        )
        
        for (path in trashPaths) {
            val trashDir = File(path)
            if (trashDir.exists() && trashDir.isDirectory) {
                Log.d("FileRecovery", "Đang quét thùng rác: $path")
                // Đánh dấu thư mục này đã được quét
                scannedDirectories.add(trashDir.absolutePath)
                scanTrashDirectory(trashDir)
            }
        }
    }
    
    /**
     * Quét một thư mục thùng rác để tìm file đã xóa
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
                    } else if (file.isFile && file.canRead() && isOtherFile(file.name) && file.length() > 0) {
                        Log.d("FileRecovery", "Tìm thấy file trong thùng rác: ${file.absolutePath}")
                        
                        // Kiểm tra trùng lặp trước khi thêm vào danh sách
                        if (!recoveredFiles.any { it.getPath() == file.absolutePath }) {
                            // Tạo đối tượng RecoverableItem và đánh dấu là đã xóa
                            val recoveredFile = RecoverableItem(
                                file,
                                file.length(),
                                getFileType(file.name),
                                true // Đánh dấu là file đã xóa
                            )
                            recoveredFiles.add(recoveredFile)
                            Log.d("FileRecovery", "Đã thêm file vào danh sách phục hồi: ${file.absolutePath}")
                        } else {
                            Log.d("FileRecovery", "Bỏ qua file trùng lặp: ${file.absolutePath}")
                        }
                    }
                } catch (e: SecurityException) {
                    Log.e("FileRecovery", "Lỗi quyền khi xử lý file trong thùng rác: ${e.message}")
                } catch (e: Exception) {
                    Log.e("FileRecovery", "Lỗi khi xử lý file trong thùng rác: ${e.message}")
                }
            }
        } catch (e: SecurityException) {
            Log.e("FileRecovery", "Lỗi quyền khi quét thư mục thùng rác: ${e.message}")
        } catch (e: Exception) {
            Log.e("FileRecovery", "Lỗi khi quét thư mục thùng rác: ${e.message}")
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
    
    /**
     * Kiểm tra xem file có phải là file đã xóa không
     */
    private fun isDeletedFile(file: File): Boolean {
        // Kiểm tra tên file có bắt đầu bằng dấu chấm (thường là file ẩn/đã xóa)
        val hasHiddenName = file.name.startsWith(".")
        
        // Kiểm tra đường dẫn file có chứa từ khóa thùng rác
        val inTrashFolder = isFileInTrashFolder(file)
        
        // Kiểm tra thư mục cha có phải là thư mục thùng rác
        val inTrashDirectory = file.parentFile?.let { isTrashDirectory(it) } ?: false
        
        return hasHiddenName || inTrashFolder || inTrashDirectory
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
                
                for (file in files) {
                    try {
                        if (file.isDirectory && file.canRead()) {
                            // Kiểm tra nếu đây là thư mục thùng rác
                            val isTrashDir = isTrashDirectory(file)
                            
                            // Quét đệ quy các thư mục, ưu tiên thư mục thùng rác
                            if (isTrashDir || (!file.name.startsWith(".") && !file.name.equals("Android", true))) {
                                simulateFindingDeletedFiles(file)
                            }
                        } else if (file.isFile && file.canRead() && isOtherFile(file.name) && file.length() > 0) {
                            Log.d("FileRecovery", "Tìm thấy file: ${file.absolutePath}")
                            
                            // Kiểm tra nếu file này đã xóa
                            val isDeleted = isDeletedFile(file)
                            
                            if (isDeleted) {
                                Log.d("FileRecovery", "Đây là file đã xóa: ${file.absolutePath}")
                                
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
        val extension = fileName.substringAfterLast('.', "").lowercase()
        // Lấy file không phải ảnh hoặc video
        return !isImageFile(extension) && !isVideoFile(extension) && extension.isNotEmpty()
    }
    
    private fun isImageFile(extension: String): Boolean {
        return extension in listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    }
    
    private fun isVideoFile(extension: String): Boolean {
        return extension in listOf("mp4", "3gp", "mkv", "avi", "mov", "wmv")
    }
    
    private fun getFileType(fileName: String): String {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return when (extension) {
            "pdf" -> "pdf"
            "doc", "docx" -> "document"
            "xls", "xlsx" -> "spreadsheet" 
            "ppt", "pptx" -> "presentation"
            "txt" -> "text"
            "zip", "rar", "7z" -> "archive"
            else -> "other"
        }
    }
    
    private fun recoverSelectedFiles() {
        val selectedFiles = recoveredFiles.filter { it.isSelected() }
        
        if (selectedFiles.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_items_selected), Toast.LENGTH_SHORT).show()
            return
        }
        
        progressBar.visibility = View.VISIBLE
        statusText.text = getString(R.string.recovering_status, selectedFiles.size)
        
        CoroutineScope(Dispatchers.IO).launch {
            // Tạo thư mục khôi phục dự phòng trong trường hợp không thể khôi phục vào thư mục hệ thống
            val recoveryDir = File(getExternalFilesDir(null), "RecoveredFiles")
            if (!recoveryDir.exists()) {
                recoveryDir.mkdirs()
            }
            
            var successCount = 0
            var documentsRecoveryCount = 0
            var downloadsRecoveryCount = 0
            
            for (file in selectedFiles) {
                try {
                    val sourceFile = file.getFile()
                    
                    // Tên file mới sau khi khôi phục (loại bỏ dấu chấm ở đầu nếu có)
                    val recoveredFileName = if (sourceFile.name.startsWith(".")) {
                        sourceFile.name.substring(1)
                    } else {
                        sourceFile.name
                    }
                    
                    // Xác định thư mục phù hợp dựa trên loại file
                    val fileType = file.getType()
                    val destDir = when {
                        fileType in listOf("document", "pdf", "text", "spreadsheet", "presentation") -> {
                            // File văn bản -> Documents
                            val docDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                            if (!docDir.exists()) {
                                docDir.mkdirs()
                            }
                            documentsRecoveryCount++
                            docDir
                        }
                        else -> {
                            // Các file khác -> Downloads
                            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            if (!downloadDir.exists()) {
                                downloadDir.mkdirs()
                            }
                            downloadsRecoveryCount++
                            downloadDir
                        }
                    }
                    
                    // File đích sau khi khôi phục
                    val destFile = File(destDir, recoveredFileName)
                    
                    // Kiểm tra nếu file đích đã tồn tại, thêm hậu tố thời gian
                    val finalDestFile = if (destFile.exists()) {
                        val timestamp = System.currentTimeMillis()
                        val extension = recoveredFileName.substringAfterLast('.', "")
                        val baseName = recoveredFileName.substringBeforeLast('.', recoveredFileName)
                        val newFileName = if (extension.isNotEmpty()) {
                            "${baseName}_$timestamp.$extension"
                        } else {
                            "${baseName}_$timestamp"
                        }
                        File(destDir, newFileName)
                    } else {
                        destFile
                    }
                    
                    // Sao chép file gốc vào thư mục đích
                    FileInputStream(sourceFile).use { input ->
                        FileOutputStream(finalDestFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    
                    // Thêm file mới vào MediaStore để các ứng dụng khác có thể nhìn thấy
                    try {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            // Thông báo hệ thống quét media mới
                            val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                            scanIntent.data = android.net.Uri.fromFile(finalDestFile)
                            sendBroadcast(scanIntent)
                        }
                    } catch (e: Exception) {
                        Log.e("FileRecovery", "Lỗi khi thông báo MediaStore: ${e.message}")
                    }
                    
                    successCount++
                    Log.d("FileRecovery", "Đã khôi phục file: ${finalDestFile.absolutePath}")
                    
                } catch (e: Exception) {
                    Log.e("FileRecovery", "Lỗi khi khôi phục file: ${e.message}")
                    e.printStackTrace()
                }
            }
            
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                if (successCount > 0) {
                    val message = getString(
                        R.string.file_recovery_success,
                        successCount,
                        documentsRecoveryCount,
                        downloadsRecoveryCount
                    )
                    
                    Toast.makeText(
                        this@FileRecoveryActivity,
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                    
                    // Quay lại màn hình chính
                    showScanLayout()
                } else {
                    Toast.makeText(
                        this@FileRecoveryActivity,
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