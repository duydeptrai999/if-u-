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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.htnguyen.ifu.R
import com.htnguyen.ifu.adapter.FileAdapter
import com.htnguyen.ifu.model.RecoverableItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class FileRecoveryActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var scanButton: Button
    private lateinit var recoverButton: Button
    private lateinit var statusText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FileAdapter
    
    private val recoveredFiles = mutableListOf<RecoverableItem>()
    private val STORAGE_PERMISSION_CODE = 101
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_recovery)
        
        progressBar = findViewById(R.id.progressBar)
        scanButton = findViewById(R.id.scanButton)
        recoverButton = findViewById(R.id.recoverButton)
        statusText = findViewById(R.id.statusText)
        recyclerView = findViewById(R.id.recyclerView)
        
        // Cài đặt RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FileAdapter(recoveredFiles) { isSelected ->
            // Cập nhật nút khôi phục dựa trên việc có file nào được chọn không
            recoverButton.isEnabled = isSelected
        }
        recyclerView.adapter = adapter
        
        scanButton.setOnClickListener {
            if (checkPermission()) {
                scanForDeletedFiles()
            } else {
                requestPermission()
            }
        }
        
        recoverButton.setOnClickListener {
            recoverSelectedFiles()
        }
        
        // Vô hiệu hóa nút khôi phục cho đến khi có file được chọn
        recoverButton.isEnabled = false
        
        findViewById<View>(R.id.backButton).setOnClickListener {
            finish()
        }
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
                scanForDeletedFiles()
            } else {
                Toast.makeText(this, getString(R.string.permission_required), Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun scanForDeletedFiles() {
        progressBar.visibility = View.VISIBLE
        statusText.text = getString(R.string.scanning_status)
        scanButton.isEnabled = false
        
        // Xóa danh sách cũ nếu có
        recoveredFiles.clear()
        adapter.notifyDataSetChanged()
        
        CoroutineScope(Dispatchers.IO).launch {
            // Tìm kiếm các tệp tin đã xóa trong bộ nhớ thiết bị
            // Trong thực tế, cần thuật toán phức tạp hơn để quét tất cả sectors
            // Đây chỉ là mô phỏng bằng cách tìm các tệp tin trong thư mục Download và Documents
            
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            
            // Giả lập tìm thấy một số tệp tin
            simulateFindingDeletedFiles(downloadDir)
            simulateFindingDeletedFiles(documentsDir)
            
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                scanButton.isEnabled = true
                
                if (recoveredFiles.isEmpty()) {
                    statusText.text = getString(R.string.no_files_found)
                } else {
                    statusText.text = getString(R.string.files_found, recoveredFiles.size)
                }
                
                adapter.notifyDataSetChanged()
            }
        }
    }
    
    private fun simulateFindingDeletedFiles(directory: File) {
        // Đây chỉ là mô phỏng - trong thực tế cần quét các sectors chưa bị ghi đè
        // Chúng ta sẽ sử dụng các tệp tin hiện có để giả lập tệp tin đã xóa được tìm thấy
        if (directory.exists()) {
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    simulateFindingDeletedFiles(file)
                } else if (isDocumentFile(file.name) && file.length() > 0) {
                    // Giả lập tìm thấy tệp tin đã xóa
                    val recoveredFile = RecoverableItem(
                        file,
                        file.length(),
                        getFileType(file.name)
                    )
                    recoveredFiles.add(recoveredFile)
                    
                    // Giới hạn số lượng tệp tin để demo
                    if (recoveredFiles.size >= 20) {
                        return
                    }
                }
            }
        }
    }
    
    private fun isDocumentFile(fileName: String): Boolean {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return extension in listOf("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "zip", "rar")
    }
    
    private fun getFileType(fileName: String): String {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return when (extension) {
            in listOf("pdf") -> "pdf"
            in listOf("doc", "docx") -> "word"
            in listOf("xls", "xlsx") -> "excel"
            in listOf("ppt", "pptx") -> "powerpoint"
            in listOf("zip", "rar") -> "archive"
            else -> "document"
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
            val recoveryDir = File(getExternalFilesDir(null), "RecoveredFiles")
            if (!recoveryDir.exists()) {
                recoveryDir.mkdirs()
            }
            
            var successCount = 0
            
            for (file in selectedFiles) {
                try {
                    val sourceFile = file.getFile()
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
                    statusText.text = getString(R.string.recovery_success, successCount)
                    Toast.makeText(
                        this@FileRecoveryActivity,
                        getString(R.string.recovery_success, successCount),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    statusText.text = getString(R.string.recovery_failed)
                    Toast.makeText(
                        this@FileRecoveryActivity,
                        getString(R.string.recovery_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
} 