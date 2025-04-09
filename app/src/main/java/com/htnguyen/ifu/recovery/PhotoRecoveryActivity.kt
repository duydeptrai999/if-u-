package com.htnguyen.ifu.recovery

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
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

class PhotoRecoveryActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var scanButton: Button
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
    
    private val recoveredPhotos = mutableListOf<RecoverableItem>()
    private val STORAGE_PERMISSION_CODE = 101
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_recovery)
        
        initViews()
        setupRecyclerViews()
        setupClickListeners()
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
        scanButton.setOnClickListener {
            if (checkPermission()) {
                scanForDeletedPhotos()
            } else {
                requestPermission()
            }
        }
        
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
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            STORAGE_PERMISSION_CODE
        )
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
        progressBar.visibility = View.VISIBLE
        statusText.text = getString(R.string.scanning_status)
        scanButton.isEnabled = false
        
        // Xóa danh sách cũ nếu có
        recoveredPhotos.clear()
        
        CoroutineScope(Dispatchers.IO).launch {
            // Tìm kiếm các ảnh đã xóa trong bộ nhớ thiết bị
            // Trong thực tế, cần thuật toán phức tạp hơn để quét tất cả sectors
            // Đây chỉ là mô phỏng bằng cách tìm các file ảnh trong thư mục DCIM và Pictures
            
            val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            
            // Giả lập tìm thấy một số ảnh
            // Trong thực tế, cần quét toàn bộ bộ nhớ để tìm ảnh đã bị xóa
            simulateFindingDeletedPhotos(dcimDir)
            simulateFindingDeletedPhotos(picturesDir)
            
            // Tính tổng kích thước của các file tìm thấy
            val totalSize = recoveredPhotos.sumOf { it.getSize() }
            val formattedSize = formatFileSize(totalSize)
            
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                scanButton.isEnabled = true
                
                if (recoveredPhotos.isEmpty()) {
                    statusText.text = getString(R.string.no_photos_found)
                    showScanLayout()
                } else {
                    // Cập nhật giao diện kết quả quét
                    foundFileCount.text = recoveredPhotos.size.toString()
                    foundFileSize.text = formattedSize
                    
                    // Cập nhật adapter
                    adapter.notifyDataSetChanged()
                    previewAdapter.notifyDataSetChanged()
                    
                    // Hiển thị layout kết quả quét
                    showScanResultLayout()
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
    
    private fun simulateFindingDeletedPhotos(directory: File) {
        // Đây chỉ là mô phỏng - trong thực tế cần quét các sectors chưa bị ghi đè
        // Chúng ta sẽ sử dụng các file ảnh hiện có để giả lập ảnh đã xóa được tìm thấy
        if (directory.exists()) {
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    simulateFindingDeletedPhotos(file)
                } else if (isImageFile(file.name) && file.length() > 0) {
                    // Giả lập tìm thấy ảnh đã xóa
                    val recoveredFile = RecoverableItem(
                        file,
                        file.length(),
                        "image"
                    )
                    recoveredPhotos.add(recoveredFile)
                    
                    // Giới hạn số lượng ảnh để demo
                    if (recoveredPhotos.size >= 20) {
                        return
                    }
                }
            }
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
} 