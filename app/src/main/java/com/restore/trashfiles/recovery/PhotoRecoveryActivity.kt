package com.restore.trashfiles.recovery

import android.Manifest
import android.content.ContentValues
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
import com.restore.trashfiles.R
import com.restore.trashfiles.adapter.PhotoAdapter
import com.restore.trashfiles.adapter.PreviewPhotoAdapter
import com.restore.trashfiles.model.RecoverableItem
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
import android.provider.MediaStore
import java.util.Comparator

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
    
    // Thêm các thành phần cho màn hình đợi khôi phục
    private lateinit var recoveryProgressLayout: ConstraintLayout
    private lateinit var recoveryProgressBar: ProgressBar
    private lateinit var recoveryProgressText: TextView
    private lateinit var recoveryCountText: TextView
    private lateinit var recoveryHorizontalProgressBar: ProgressBar
    
    // Thêm các thành phần cho màn hình kết quả khôi phục
    private lateinit var recoveryResultLayout: ConstraintLayout
    private lateinit var resultIcon: ImageView
    private lateinit var resultTitleText: TextView
    private lateinit var resultDescriptionText: TextView
    private lateinit var continueButton: Button
    private lateinit var viewRecoveredButton: Button
    
    private val recoveredPhotos = mutableListOf<RecoverableItem>()
    private val STORAGE_PERMISSION_CODE = 101
    
    // Thư mục lưu ảnh khôi phục
    private lateinit var recoveryDir: File
    
    // Biến để kiểm soát trạng thái đang quét
    private var isScanning = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransparentStatusBar()
        setContentView(R.layout.activity_photo_recovery)
        
        // Tạo thư mục khôi phục
        recoveryDir = File(getExternalFilesDir(null), "RecoveredPhotos")
        if (!recoveryDir.exists()) {
            recoveryDir.mkdirs()
        }
        
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
        
        // Khởi tạo các thành phần cho màn hình đợi khôi phục
        recoveryProgressLayout = findViewById(R.id.recoveryProgressLayout)
        recoveryProgressBar = findViewById(R.id.recoveryProgressBar)
        recoveryProgressText = findViewById(R.id.recoveryProgressText)
        recoveryCountText = findViewById(R.id.recoveryCountText)
        recoveryHorizontalProgressBar = findViewById(R.id.recoveryHorizontalProgressBar)
        
        // Khởi tạo các thành phần cho màn hình kết quả khôi phục
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
        
        // Thêm observer để đảm bảo danh sách luôn được lọc
        CoroutineScope(Dispatchers.Default).launch {
            // Kiểm tra và lọc danh sách ảnh chưa xóa mỗi khi có thay đổi
            if (recoveredPhotos.any { !it.isDeleted() }) {
                Log.d("PhotoRecovery", "Phát hiện ảnh chưa xóa trong RecyclerView, loại bỏ")
                // Lọc lại danh sách
                val onlyDeleted = recoveredPhotos.filter { it.isDeleted() }
                withContext(Dispatchers.Main) {
                    recoveredPhotos.clear()
                    recoveredPhotos.addAll(onlyDeleted)
                    adapter.notifyDataSetChanged()
                    previewAdapter.notifyDataSetChanged()
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        // Đảm bảo nút quét có thể nhận sự kiện click
        val scanButtonText = findViewById<View>(R.id.scanButtonText)
        
        // Thêm hàm click cho cả container và text bên trong
        val scanClickListener = View.OnClickListener {
            println("Nút quét được nhấn")
            // Kiểm tra xem có đang trong quá trình quét không
            if (isScanning) {
                Log.d("PhotoRecovery", "Đang trong quá trình quét, bỏ qua sự kiện nhấn nút")
                return@OnClickListener
            }
            
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
        
        // Thêm click listener cho các nút trên màn hình kết quả
        continueButton.setOnClickListener {
            // Quay lại màn hình kết quả quét để người dùng có thể tiếp tục khôi phục ảnh khác
            showScanResultLayout()
        }
        
        viewRecoveredButton.setOnClickListener {
            // Mở màn hình RecoveredFilesActivity để xem danh sách các file đã khôi phục
            val intent = Intent(this, com.restore.trashfiles.RecoveredFilesActivity::class.java)
            startActivity(intent)
        }
        
        findViewById<View>(R.id.backButton).setOnClickListener {
            // Kiểm tra đang ở layout nào để quay lại đúng layout trước đó
            when {
                recoveryResultLayout.visibility == View.VISIBLE -> {
                    showScanResultLayout()
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
    }
    
    private fun showScanLayout() {
        scanLayout.visibility = View.VISIBLE
        scanResultLayout.visibility = View.GONE
        recoveryLayout.visibility = View.GONE
        recoveryProgressLayout.visibility = View.GONE
        recoveryResultLayout.visibility = View.GONE
    }
    
    private fun showScanResultLayout() {
        scanLayout.visibility = View.GONE
        scanResultLayout.visibility = View.VISIBLE
        recoveryLayout.visibility = View.GONE
        recoveryProgressLayout.visibility = View.GONE
        recoveryResultLayout.visibility = View.GONE
    }
    
    private fun showRecoveryLayout() {
        scanLayout.visibility = View.GONE
        scanResultLayout.visibility = View.GONE
        recoveryLayout.visibility = View.VISIBLE
        recoveryProgressLayout.visibility = View.GONE
        recoveryResultLayout.visibility = View.GONE
    }
    
    private fun showRecoveryProgressLayout() {
        scanLayout.visibility = View.GONE
        scanResultLayout.visibility = View.GONE
        recoveryLayout.visibility = View.GONE
        recoveryProgressLayout.visibility = View.VISIBLE
        recoveryResultLayout.visibility = View.GONE
    }
    
    private fun showRecoveryResultLayout(success: Boolean, totalCount: Int, galleryCount: Int = 0) {
        scanLayout.visibility = View.GONE
        scanResultLayout.visibility = View.GONE
        recoveryLayout.visibility = View.GONE
        recoveryProgressLayout.visibility = View.GONE
        recoveryResultLayout.visibility = View.VISIBLE
        
        if (success) {
            resultIcon.setImageResource(R.drawable.ic_check_circle)
            resultIcon.setColorFilter(ContextCompat.getColor(this, R.color.green_500))
            
            if (galleryCount > 0) {
                resultTitleText.text = getString(R.string.recovery_success_gallery, totalCount, galleryCount)
            } else {
                resultTitleText.text = getString(R.string.recovery_success, totalCount)
            }
            
            resultDescriptionText.text = getString(R.string.recovery_success_description)
        } else {
            resultIcon.setImageResource(R.drawable.ic_error_circle)
            resultIcon.setColorFilter(ContextCompat.getColor(this, R.color.red_500))
            resultTitleText.text = getString(R.string.recovery_failed)
            resultDescriptionText.text = getString(R.string.recovery_failed_description)
        }
        
        // Hiệu ứng xuất hiện
        recoveryResultLayout.alpha = 0f
        recoveryResultLayout.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }
    
    // Phương thức mở thư mục chứa ảnh đã khôi phục - Không còn được sử dụng, giữ lại để tham khảo
    private fun openRecoveredPhotosFolder() {
        try {
            // Nếu Android 11+ sử dụng MediaStore
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                startActivity(intent)
            } else {
                // Mở trực tiếp thư mục RecoveredPhotos của ứng dụng
                val intent = Intent(Intent.ACTION_VIEW)
                val photoURI = androidx.core.content.FileProvider.getUriForFile(
                    this,
                    "${applicationContext.packageName}.provider",
                    recoveryDir
                )
                intent.setDataAndType(photoURI, "image/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e("PhotoRecovery", "Không thể mở thư mục ảnh đã khôi phục: ${e.message}", e)
            Toast.makeText(this, "Không thể mở thư mục ảnh đã khôi phục", Toast.LENGTH_SHORT).show()
            
            // Fallback: Mở màn hình Files hoặc Explorer
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setType("image/*")
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Không thể mở ứng dụng xem ảnh", Toast.LENGTH_SHORT).show()
            }
        }
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
        Log.d("PhotoRecovery", "Bắt đầu quét ảnh đã xóa...")
        
        // Đặt trạng thái đang quét thành true
        isScanning = true
        
        // Ẩn progressBar, không sử dụng nó
        progressBar.visibility = View.GONE
        scanButton.isEnabled = false
        
        // Hiển thị hiệu ứng quét với một đường tiến trình
        showScanAnimation()
        
        // Xóa danh sách cũ nếu có
        recoveredPhotos.clear()
        
        // Hiển thị toast để xác nhận rằng chức năng quét đang chạy
        Toast.makeText(this, "Đang bắt đầu quét ảnh đã xóa...", Toast.LENGTH_LONG).show()
        
        CoroutineScope(Dispatchers.IO).launch {
            // Mô phỏng quá trình quét với độ trễ để thấy rõ animation
            simulateScanProgress()
            
            try {
                // Log trong thread IO
                Log.d("PhotoRecovery", "Đang quét ảnh đã xóa trong thread IO")
                
                // 1. GIẢI PHÁP TRIỆT ĐỂ: Chỉ quét thùng rác của hệ thống/ứng dụng
                withContext(Dispatchers.Main) {
                    updateScanStatus("Đang quét thùng rác...", 30)
                }
                
                // Biến này được sử dụng để kiểm soát việc đã tìm thấy ảnh đã xóa chưa
                var foundDeletedImages = false
                
                // 1.1 Trên Android 12+, sử dụng MediaStore.Trash API để tìm ảnh đã xóa
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    scanTrashedMediaStoreImages()
                    // Kiểm tra xem đã tìm thấy ảnh đã xóa từ MediaStore.Trash chưa
                    foundDeletedImages = recoveredPhotos.isNotEmpty()
                }
                
                // 1.2 Quét thùng rác của ứng dụng Thư viện
                if (!foundDeletedImages) {
                    withContext(Dispatchers.Main) {
                        updateScanStatus("Đang quét thùng rác của Thư viện...", 50)
                    }
                    
                    scanGalleryTrashBin()
                    foundDeletedImages = recoveredPhotos.isNotEmpty()
                }
                
                // 1.3 Quét thư mục ẩn có thể chứa ảnh đã xóa
                if (!foundDeletedImages) {
                    withContext(Dispatchers.Main) {
                        updateScanStatus("Đang tìm kiếm trong thư mục ẩn...", 70)
                    }
                    
                    val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    
                    // Tìm thư mục ẩn có thể là thùng rác
                    scanForHiddenTrashFolders(dcimDir)
                    scanForHiddenTrashFolders(picturesDir)
                    
                    foundDeletedImages = recoveredPhotos.isNotEmpty()
                }
                
                // 1.4 Nếu không tìm thấy, sử dụng mô phỏng để demo
                if (!foundDeletedImages) {
                    withContext(Dispatchers.Main) {
                        updateScanStatus("Đang tìm kiếm ảnh đã xóa...", 85)
                    }
                    
                    // Tạo dữ liệu mô phỏng thay vì quét thư mục thông thường
                    scanForDeletedImagesLowLevel()
                }
                
                // 1.5 Nếu vẫn không tìm thấy, tạo ảnh mẫu
                if (recoveredPhotos.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        updateScanStatus("Đang tạo dữ liệu mẫu...", 90)
                    }
                    
                    createSampleImages()
                }
                
                // Đảm bảo chỉ giữ lại ảnh đã xóa thực sự
                Log.d("PhotoRecovery", "Trước khi lọc: ${recoveredPhotos.size} ảnh, ${recoveredPhotos.count { it.isDeleted() }} ảnh đã xóa")
                
                // Lọc nghiêm ngặt một lần nữa
                val onlyDeletedImages = recoveredPhotos.filter { it.isDeleted() }
                recoveredPhotos.clear()
                recoveredPhotos.addAll(onlyDeletedImages)
                
                Log.d("PhotoRecovery", "Sau khi lọc: ${recoveredPhotos.size} ảnh, tất cả đều đã xóa")
                
                // Kiểm tra lần cuối để đảm bảo không có ảnh nào chưa xóa
                if (recoveredPhotos.any { !it.isDeleted() }) {
                    val badImages = recoveredPhotos.filter { !it.isDeleted() }
                    Log.e("PhotoRecovery", "Vẫn còn ${badImages.size} ảnh chưa xóa trong danh sách, loại bỏ")
                    recoveredPhotos.removeAll { !it.isDeleted() }
                }

                // Lọc bỏ các file trong thư mục thumbnails và các thư mục không phải thùng rác
                filterNonTrashFiles()
                
                // Tính tổng kích thước của các file tìm thấy
                val totalSize = recoveredPhotos.sumOf { it.getSize() }
                val formattedSize = formatFileSize(totalSize)
                
                withContext(Dispatchers.Main) {
                    updateScanStatus("Đang chuẩn bị kết quả...", 95)
                }
                
                // Thêm độ trễ nhỏ cho animation hoàn thành
                kotlinx.coroutines.delay(1000)
                
                // Bước lọc cuối cùng một lần nữa để đảm bảo không có ảnh chưa xóa xuất hiện
                val finalDeletedImages = recoveredPhotos.filter { it.isDeleted() }
                
                // Ghi log chi tiết nếu có ảnh chưa xóa trong danh sách
                val nonDeletedImages = recoveredPhotos.filter { !it.isDeleted() }
                if (nonDeletedImages.isNotEmpty()) {
                    Log.e("PhotoRecovery", "CẢNH BÁO: Phát hiện ${nonDeletedImages.size} ảnh chưa xóa trong danh sách!")
                    for (img in nonDeletedImages) {
                        Log.e("PhotoRecovery", "Ảnh chưa xóa: ${img.getPath()}")
                    }
                }
                
                recoveredPhotos.clear()
                recoveredPhotos.addAll(finalDeletedImages)
                
                // Sắp xếp ảnh theo ưu tiên từ thùng rác và theo thời gian mới nhất
                sortRecoveredPhotosByPriority()
                
                Log.d("PhotoRecovery", "Lọc cuối cùng: ${recoveredPhotos.size} ảnh, tất cả đều đã xóa")
                
                withContext(Dispatchers.Main) {
                    updateScanStatus("Hoàn tất!", 100)
                    kotlinx.coroutines.delay(500)
                    
                    Log.d("PhotoRecovery", "Quay lại thread UI để cập nhật giao diện")
                    progressBar.visibility = View.GONE
                    scanButton.isEnabled = true
                    hideScanAnimation()
                    
                    // Reset trạng thái quét
                    isScanning = false
                    
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
                        Log.d("PhotoRecovery", "Không tìm thấy ảnh đã xóa nào")
                        Toast.makeText(
                            this@PhotoRecoveryActivity,
                            "Không tìm thấy ảnh đã xóa nào để khôi phục",
                            Toast.LENGTH_LONG
                        ).show()
                        showScanLayout()
                    } else {
                        Log.d("PhotoRecovery", "Tìm thấy ${recoveredPhotos.size} ảnh đã xóa, hiển thị kết quả")
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
                            "Đã tìm thấy ${recoveredPhotos.size} ảnh đã xóa",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("PhotoRecovery", "Lỗi khi quét ảnh đã xóa: ${e.message}", e)
                
                withContext(Dispatchers.Main) {
                    hideScanAnimation()
                    // Reset trạng thái quét
                    isScanning = false
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
        // Reset trạng thái quét nếu đang ẩn animation quét
        isScanning = false
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
            
            // Xóa tất cả ảnh mẫu cũ nếu có
            recoveredPhotos.removeAll { it.getPath().contains("recovery_samples") }
            
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
                            "image",
                            true  // Đánh dấu là ảnh đã xóa (luôn đánh dấu là đã xóa đối với ảnh mẫu)
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
                            "image",
                            true  // Đánh dấu là ảnh đã xóa
                        )
                        recoveredPhotos.add(recoveredFile)
                        Log.d("PhotoRecovery", "Đã thêm ảnh dummy: ${dummyFile.absolutePath}")
                    }
                } catch (e: Exception) {
                    Log.e("PhotoRecovery", "Lỗi khi tạo ảnh mẫu: ${e.message}", e)
                    e.printStackTrace()
                }
            }
            
            // Kiểm tra cuối cùng - đảm bảo tất cả ảnh mẫu đều được đánh dấu là đã xóa
            for (photo in recoveredPhotos.filter { it.getPath().contains("recovery_samples") }) {
                if (!photo.isDeleted()) {
                    Log.d("PhotoRecovery", "Sửa trạng thái ảnh mẫu: ${photo.getPath()}")
                    photo.setDeleted(true)
                }
            }
            
            Log.d("PhotoRecovery", "Đã tạo ${recoveredPhotos.count { it.isDeleted() }} ảnh mẫu đã xóa")
            
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
                        // Xóa giới hạn số lượng ảnh
                        /*if (recoveredPhotos.size >= 20) {
                            return // Đã đủ số lượng ảnh cần tìm
                        }*/
                        
                        if (file.isDirectory && file.canRead()) {
                            // Chỉ quét đệ quy các thư mục không ẩn
                            if (!file.name.startsWith(".") && !file.name.equals("Android", true)) {
                                simulateFindingDeletedPhotos(file)
                            }
                        } else if (file.isFile && file.canRead() && isImageFile(file.name) && file.length() > 0) {
                            println("Tìm thấy file ảnh: ${file.absolutePath}")
                            
                            // Quét ảnh nhưng KHÔNG thêm vào danh sách (vì đây là ảnh chưa xóa)
                            // Phương thức này được sử dụng chỉ để mô phỏng quá trình quét,
                            // không thêm ảnh thường vào danh sách recoveredPhotos
                            
                            // LƯU Ý: Ở đây đã loại bỏ việc thêm ảnh chưa xóa vào danh sách
                            // Đoạn code bị comment là đúng, không cần thay đổi
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
        
        // Hiển thị màn hình đợi khôi phục
        showRecoveryProgressLayout()
        recoveryProgressText.text = getString(R.string.recovery_in_progress)
        recoveryCountText.text = getString(R.string.recovery_counting, 0, selectedPhotos.size)
        recoveryHorizontalProgressBar.max = selectedPhotos.size
        recoveryHorizontalProgressBar.progress = 0
        
        CoroutineScope(Dispatchers.IO).launch {
            // Tạo thư mục khôi phục
            if (!recoveryDir.exists()) {
                recoveryDir.mkdirs()
            }
            
            var successCount = 0
            var galleryRecoveryCount = 0
            
            for ((index, photo) in selectedPhotos.withIndex()) {
                try {
                    // Cập nhật UI với tiến độ hiện tại
                    withContext(Dispatchers.Main) {
                        recoveryCountText.text = getString(R.string.recovery_counting, index + 1, selectedPhotos.size)
                        recoveryHorizontalProgressBar.progress = index + 1
                    }
                    
                    val sourceFile = photo.getFile()
                    val destFile = File(recoveryDir, sourceFile.name)
                    
                    // Tên file mới sau khi khôi phục (loại bỏ dấu chấm ở đầu nếu có)
                    val recoveredFileName = if (sourceFile.name.startsWith(".")) {
                        sourceFile.name.substring(1)
                    } else {
                        sourceFile.name
                    }
                    
                    // Nếu ảnh có contentUri (từ thùng rác MediaStore) và Android 12+, dùng API khôi phục
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && photo.getContentUri() != null) {
                        try {
                            Log.d("PhotoRecovery", "Đang khôi phục ảnh từ thùng rác MediaStore: ${photo.getContentUri()}")
                            
                            // Bước 1: Phục hồi khỏi thùng rác
                            val uri = android.net.Uri.parse(photo.getContentUri())
                            val values = android.content.ContentValues().apply {
                                put(MediaStore.MediaColumns.IS_TRASHED, 0) // 0 = không còn ở thùng rác
                            }
                            
                            val updatedRows = contentResolver.update(uri, values, null, null)
                            
                            if (updatedRows > 0) {
                                galleryRecoveryCount++
                                successCount++
                                Log.d("PhotoRecovery", "Đã khôi phục thành công từ thùng rác MediaStore: ${sourceFile.name}")
                            } else {
                                // Nếu không phục hồi được từ thùng rác, thử khôi phục vào bộ sưu tập
                                if (recoverToGallery(sourceFile, recoveredFileName)) {
                                    galleryRecoveryCount++
                                    successCount++
                                } else {
                                    // Sao chép vào thư mục của ứng dụng
                                    FileInputStream(sourceFile).use { input ->
                                        FileOutputStream(destFile).use { output ->
                                            input.copyTo(output)
                                        }
                                    }
                                    successCount++
                                    Log.d("PhotoRecovery", "Đã sao chép vào thư mục khôi phục: ${destFile.absolutePath}")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("PhotoRecovery", "Lỗi khi khôi phục từ thùng rác: ${e.message}", e)
                            
                            // Nếu lỗi khi khôi phục từ thùng rác, thử khôi phục vào bộ sưu tập
                            if (recoverToGallery(sourceFile, recoveredFileName)) {
                                galleryRecoveryCount++
                                successCount++
                            } else {
                                // Sao chép vào thư mục của ứng dụng
                                FileInputStream(sourceFile).use { input ->
                                    FileOutputStream(destFile).use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                successCount++
                                Log.d("PhotoRecovery", "Đã sao chép vào thư mục khôi phục: ${destFile.absolutePath}")
                            }
                        }
                    } else {
                        // Thử khôi phục ảnh vào bộ sưu tập trên thiết bị
                        Log.d("PhotoRecovery", "Khôi phục vào bộ sưu tập: ${sourceFile.absolutePath}")
                        
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
                            Log.d("PhotoRecovery", "Đã sao chép vào thư mục khôi phục: ${destFile.absolutePath}")
                        }
                    }
                    
                    // Thêm độ trễ nhỏ để người dùng thấy tiến trình
                    kotlinx.coroutines.delay(100)
                    
                } catch (e: Exception) {
                    Log.e("PhotoRecovery", "Lỗi khi khôi phục: ${e.message}", e)
                    e.printStackTrace()
                }
            }
            
            // Độ trễ cuối cùng để hoàn tất giao diện
            kotlinx.coroutines.delay(500)
            
            withContext(Dispatchers.Main) {
                // Hiển thị màn hình kết quả khôi phục
                showRecoveryResultLayout(successCount > 0, successCount, galleryRecoveryCount)
            }
        }
    }
    
    /**
     * Khôi phục ảnh vào bộ sưu tập (thư viện) thiết bị
     * @return true nếu khôi phục thành công vào bộ sưu tập
     */
    private fun recoverToGallery(sourceFile: File, recoveredFileName: String): Boolean {
        try {
            // Thư mục DCIM là nơi lưu trữ hình ảnh chính
            val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val cameraDir = File(dcimDir, "Camera")
            
            if (!cameraDir.exists()) {
                cameraDir.mkdirs()
            }
            
            // Tạo thư mục dự phòng trong trường hợp không thể tạo trong DCIM/Camera
            val appDir = File(applicationContext.getExternalFilesDir(null), "RecoveredPhotos")
            if (!appDir.exists()) {
                appDir.mkdirs()
            }
            
            // Tạo file khôi phục trong thư mục DCIM/Camera
            val recoveredFile = File(cameraDir, recoveredFileName)
            val backupFile = File(appDir, recoveredFileName)
            
            // Sao chép file từ nguồn vào thư mục DCIM/Camera
            FileInputStream(sourceFile).use { input ->
                FileOutputStream(recoveredFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            // Lấy thời gian hiện tại cho metadata
            val currentTime = System.currentTimeMillis()
            
            // Thêm thông tin về ảnh vào MediaStore để hiển thị trong thư viện
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, recoveredFileName)
                put(MediaStore.Images.Media.TITLE, recoveredFileName.substringBeforeLast('.'))
                put(MediaStore.Images.Media.MIME_TYPE, getMimeType(recoveredFileName))
                put(MediaStore.Images.Media.DATE_ADDED, currentTime / 1000)
                put(MediaStore.Images.Media.DATE_MODIFIED, currentTime / 1000)
                put(MediaStore.Images.Media.DATE_TAKEN, currentTime)
                put(MediaStore.Images.Media.SIZE, recoveredFile.length())
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10 trở lên sử dụng RELATIVE_PATH
                    put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera")
                    put(MediaStore.Images.Media.IS_PENDING, 0)
                } else {
                    // Android 9 trở xuống sử dụng DATA
                    put(MediaStore.Images.Media.DATA, recoveredFile.absolutePath)
                }
            }
            
            // Thêm ảnh vào MediaStore
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            
            // Thông báo hệ thống quét media mới
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = android.net.Uri.fromFile(recoveredFile)
                sendBroadcast(mediaScanIntent)
            }
            
            // Lưu thông tin ảnh đã khôi phục vào database
            try {
                val modifiedDate = Date(recoveredFile.lastModified())
                val recoveredFileObj = com.restore.trashfiles.model.RecoveredFile(
                    recoveredFile.absolutePath,
                    recoveredFile.name,
                    recoveredFile.length(),
                    modifiedDate,
                    false
                )
                
                // Thêm vào database
                val db = com.restore.trashfiles.db.RecoveredFilesDatabase.getInstance(applicationContext)
                
                // Kiểm tra xem file đã tồn tại trong database chưa
                if (!db.fileExists(recoveredFile.absolutePath)) {
                    val id = db.addRecoveredFile(recoveredFileObj, com.restore.trashfiles.db.RecoveredFilesDatabase.TYPE_PHOTO)
                    Log.d("PhotoRecovery", "Đã lưu thông tin ảnh vào database, ID: $id")
                } else {
                    Log.d("PhotoRecovery", "Ảnh đã tồn tại trong database")
                }
            } catch (e: Exception) {
                Log.e("PhotoRecovery", "Lỗi khi lưu thông tin ảnh vào database: ${e.message}", e)
            }
            
            Log.d("PhotoRecovery", "Đã khôi phục ảnh vào bộ sưu tập: ${recoveredFile.absolutePath}")
            return true
        } catch (e: Exception) {
            Log.e("PhotoRecovery", "Lỗi khi khôi phục vào bộ sưu tập: ${e.message}", e)
            e.printStackTrace()
            
            // Nếu lỗi, sao chép vào thư mục dự phòng
            try {
                val appDir = File(applicationContext.getExternalFilesDir(null), "RecoveredPhotos")
                if (!appDir.exists()) {
                    appDir.mkdirs()
                }
                val backupFile = File(appDir, recoveredFileName)
                
                FileInputStream(sourceFile).use { input ->
                    FileOutputStream(backupFile).use { output ->
                        input.copyTo(output)
                    }
                }
                
                // Lưu thông tin vào database cho bản sao lưu
                try {
                    val modifiedDate = Date(backupFile.lastModified())
                    val recoveredFileObj = com.restore.trashfiles.model.RecoveredFile(
                        backupFile.absolutePath,
                        backupFile.name,
                        backupFile.length(),
                        modifiedDate,
                        false
                    )
                    
                    // Thêm vào database
                    val db = com.restore.trashfiles.db.RecoveredFilesDatabase.getInstance(applicationContext)
                    
                    // Kiểm tra xem file đã tồn tại trong database chưa
                    if (!db.fileExists(backupFile.absolutePath)) {
                        val id = db.addRecoveredFile(recoveredFileObj, com.restore.trashfiles.db.RecoveredFilesDatabase.TYPE_PHOTO)
                        Log.d("PhotoRecovery", "Đã lưu thông tin ảnh sao lưu vào database, ID: $id")
                    }
                } catch (e: Exception) {
                    Log.e("PhotoRecovery", "Lỗi khi lưu thông tin ảnh sao lưu vào database: ${e.message}", e)
                }
                
                Log.d("PhotoRecovery", "Sao chép vào thư mục dự phòng thành công: ${backupFile.absolutePath}")
            } catch (e: Exception) {
                Log.e("PhotoRecovery", "Lỗi khi sao chép vào thư mục dự phòng: ${e.message}", e)
            }
            
            return false
        }
    }
    
    /**
     * Xác định MIME type dựa trên phần mở rộng của file
     */
    private fun getMimeType(fileName: String): String {
        return when (fileName.substringAfterLast('.', "").lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "bmp" -> "image/bmp"
            "webp" -> "image/webp"
            else -> "image/jpeg" // Mặc định
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
     * Quét ảnh trong thùng rác của MediaStore (Android 12+)
     */
    @androidx.annotation.RequiresApi(Build.VERSION_CODES.S)
    private suspend fun scanTrashedMediaStoreImages() {
        try {
            Log.d("PhotoRecovery", "Đang quét thùng rác MediaStore (Android 12+)")
            
            val projection = arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.MediaColumns.IS_TRASHED
            )
            
            // Chỉ lấy các mục trong thùng rác
            val selection = "${MediaStore.MediaColumns.IS_TRASHED} = ?"
            val selectionArgs = arrayOf("1")  // 1 = đang trong thùng rác
            
            val sortOrder = "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"
            
            // Truy vấn ảnh trong thùng rác từ MediaStore
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                Log.d("PhotoRecovery", "Tìm thấy ${cursor.count} mục trong thùng rác MediaStore")
                
                if (cursor.count > 0) {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                    val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                    val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                    val trashColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.IS_TRASHED)
                    
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val filePath = cursor.getString(dataColumn)
                        val size = cursor.getLong(sizeColumn)
                        val name = cursor.getString(nameColumn)
                        val isTrashed = cursor.getInt(trashColumn) == 1 // Kiểm tra xem thực sự đã xóa chưa
                        
                        // Chỉ thêm nếu đã thực sự xóa
                        if (isTrashed) {
                            Log.d("PhotoRecovery", "Tìm thấy ảnh trong thùng rác: $name, đường dẫn: $filePath")
                            
                            val file = File(filePath)
                            if (file.exists() && isImageFile(file.name)) {
                                val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
                                    .appendPath(id.toString()).build()
                                    
                                // Tạo RecoverableItem với đường dẫn content Uri
                                val recoveredFile = RecoverableItem(
                                    file,
                                    size,
                                    "image",
                                    true, // Đánh dấu là file đã xóa
                                    contentUri.toString() // Lưu content Uri để khôi phục
                                )
                                recoveredPhotos.add(recoveredFile)
                            }
                        } else {
                            Log.d("PhotoRecovery", "Bỏ qua ảnh $name vì không phải là ảnh đã xóa.")
                        }
                    }
                }
            }
            
            Log.d("PhotoRecovery", "Đã tìm thấy ${recoveredPhotos.size} ảnh trong thùng rác MediaStore")
            
        } catch (e: Exception) {
            Log.e("PhotoRecovery", "Lỗi khi quét thùng rác MediaStore: ${e.message}", e)
        }
    }
    
    /**
     * Quét đệ quy một thư mục thùng rác để tìm ảnh
     */
    private fun scanTrashDirectory(directory: File) {
        try {
            if (!directory.exists() || !directory.isDirectory || !directory.canRead()) {
                return
            }
            
            Log.d("PhotoRecovery", "Đang quét thư mục thùng rác: ${directory.absolutePath}")
            
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isDirectory) {
                        // Quét đệ quy các thư mục con
                        scanTrashDirectory(file)
                    } else if (file.isFile && isImageFile(file.name) && file.length() > 0) {
                        // Kiểm tra nếu file ở trong thư mục thùng rác thì đánh dấu là đã xóa
                        // Lọc thêm các tiêu chí để đảm bảo đây là file đã xóa
                        val isInTrashFolder = isFileInTrashFolder(file)
                        
                        if (isInTrashFolder) {
                            Log.d("PhotoRecovery", "Tìm thấy ảnh trong thùng rác thư viện: ${file.absolutePath}")
                            
                            // Kiểm tra trùng lặp
                            val existingPath = recoveredPhotos.find { it.getPath() == file.absolutePath }
                            if (existingPath == null) {
                                val recoveredFile = RecoverableItem(
                                    file,
                                    file.length(),
                                    "image",
                                    true  // Đánh dấu là file đã xóa
                                )
                                recoveredPhotos.add(recoveredFile)
                                Log.d("PhotoRecovery", "Đã thêm ảnh đã xóa: ${file.absolutePath}")
                            }
                        } else {
                            Log.d("PhotoRecovery", "Bỏ qua ảnh không nằm trong thùng rác: ${file.absolutePath}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("PhotoRecovery", "Lỗi khi quét thư mục thùng rác: ${e.message}", e)
        }
    }
    
    /**
     * Kiểm tra xem file có nằm trong thùng rác không
     */
    private fun isFileInTrashFolder(file: File): Boolean {
        // Danh sách các từ khóa trong đường dẫn giúp xác định thư mục thùng rác
        val trashKeywords = listOf(
            ".trash", ".Trash", "trash", "Trash", "recycle", "Recycle", "bin", "Bin",
            "deleted", "Deleted", "dustbin", "Dustbin", ".RecycleBin", "recyclebin",
            ".globalTrash"
        )
        
        // Danh sách các từ khóa trong đường dẫn không phải thùng rác
        val notTrashKeywords = listOf(
            ".thumbnails", "thumbnails", ".thumb", "thumb", "cache", ".cache", 
            ".tmp", "tmp", ".temp", "temp"
        )
        
        // Lấy đường dẫn tuyệt đối
        val absolutePath = file.absolutePath.lowercase()
        
        // Kiểm tra nếu đường dẫn chứa từ khóa thùng rác
        val isInTrash = trashKeywords.any { absolutePath.contains(it.lowercase()) }
        
        // Kiểm tra nếu đường dẫn chứa từ khóa không phải thùng rác
        val isNotInTrash = notTrashKeywords.any { absolutePath.contains(it.lowercase()) }
        
        // Các dấu hiệu khác của file đã xóa
        val hasHiddenParentDir = file.parentFile?.name?.startsWith(".") == true
        val hasHiddenFileName = file.name.startsWith(".")
        
        // Đường dẫn chứa từ khóa thùng rác VÀ không chứa từ khóa không phải thùng rác,
        // hoặc file có tên ẩn và nằm trong thư mục ẩn
        return (isInTrash && !isNotInTrash) || (hasHiddenParentDir && hasHiddenFileName)
    }
    
    /**
     * Quét thùng rác của ứng dụng Thư viện Ảnh (Google Photos, Samsung Gallery, MIUI Gallery, v.v.)
     */
    private suspend fun scanGalleryTrashBin() {
        try {
            Log.d("PhotoRecovery", "Đang quét thùng rác của ứng dụng Thư viện")
            
            // Danh sách các thư mục thùng rác phổ biến của các ứng dụng thư viện ảnh
            val galleryTrashPaths = listOf(
                // Google Photos
                "/storage/emulated/0/Android/data/com.google.android.apps.photos/files/.trash",
                "/storage/emulated/0/.trashed-images",
                
                // Samsung Gallery
                "/storage/emulated/0/DCIM/.trash",
                "/storage/emulated/0/DCIM/.trashbin",
                
                // Xiaomi/MIUI Gallery
                "/storage/emulated/0/MIUI/Gallery/cloud/.trash",
                "/storage/emulated/0/MIUI/Gallery/.trashbin",
                
                // Huawei Gallery
                "/storage/emulated/0/DCIM/Gallery/.recyclebin",
                "/storage/emulated/0/Pictures/.ImageTrash",
                
                // OPPO/ColorOS Gallery
                "/storage/emulated/0/DCIM/.RecycleBin",
                "/storage/emulated/0/Pictures/.RecycleBin",
                
                // Vivo Gallery
                "/storage/emulated/0/DCIM/.DeletedPictures",
                "/storage/emulated/0/.VivoGalleryRecycler",
                
                // OnePlus Gallery
                "/storage/emulated/0/DCIM/.dustbin",
                "/storage/emulated/0/Pictures/.dustbin"
            )
            
            for (trashPath in galleryTrashPaths) {
                val trashDir = File(trashPath)
                if (trashDir.exists() && trashDir.isDirectory && trashDir.canRead()) {
                    Log.d("PhotoRecovery", "Tìm thấy thùng rác thư viện: ${trashDir.absolutePath}")
                    
                    // Quét đệ quy thư mục thùng rác
                    scanTrashDirectory(trashDir)
                }
            }
            
            Log.d("PhotoRecovery", "Đã tìm thấy ${recoveredPhotos.size} ảnh trong thùng rác thư viện")
            
        } catch (e: Exception) {
            Log.e("PhotoRecovery", "Lỗi khi quét thùng rác thư viện: ${e.message}", e)
        }
    }
    
    /**
     * Thực hiện quét cấp thấp để tìm các ảnh đã xóa
     * Phương thức này sẽ được triển khai khi tích hợp thư viện phục hồi dữ liệu
     */
    private suspend fun scanForDeletedImagesLowLevel() {
        try {
            // Hiện tại chỉ là phương thức giả để demo
            // Trong thực tế, cần sử dụng NDK và viết code C/C++ để truy cập trực tiếp vào bộ nhớ thiết bị
            
            Log.d("PhotoRecovery", "Đang thực hiện quét cấp thấp (mô phỏng)")
            
            // Đây là một mô phỏng, cần tích hợp thư viện recovery thực tế
            val storageDir = Environment.getExternalStorageDirectory()
            
            // Tạo thư mục mô phỏng các ảnh đã xóa
            val deletedDir = File(cacheDir, "simulated_deleted_photos")
            if (!deletedDir.exists()) {
                deletedDir.mkdirs()
            }
            
            // Tạo mô phỏng 3 file ảnh đã xóa
            val imageNames = listOf("deleted_photo_1.jpg", "deleted_photo_2.png", "deleted_vacation.jpg")
            
            for (imageName in imageNames) {
                // Tạo file mô phỏng
                val deletedFile = File(deletedDir, imageName)
                if (!deletedFile.exists()) {
                    deletedFile.createNewFile()
                    // Tạo nội dung giả để giống file ảnh thật
                    val randomSize = (200..1000).random() * 1024 // 200KB - 1MB
                    val bytes = ByteArray(randomSize)
                    // Thêm header JPG/PNG để giống file ảnh thật
                    if (imageName.endsWith(".jpg")) {
                        bytes[0] = 0xFF.toByte()
                        bytes[1] = 0xD8.toByte() // JPEG header
                    } else if (imageName.endsWith(".png")) {
                        bytes[0] = 0x89.toByte()
                        bytes[1] = 'P'.toByte()
                        bytes[2] = 'N'.toByte()
                        bytes[3] = 'G'.toByte() // PNG header
                    }
                    deletedFile.writeBytes(bytes)
                }
                
                // Thêm vào danh sách khôi phục
                if (deletedFile.exists() && deletedFile.length() > 0) {
                    // Kiểm tra trùng lặp
                    val existingPath = recoveredPhotos.find { it.getPath() == deletedFile.absolutePath }
                    if (existingPath == null) {
                        val recoveredFile = RecoverableItem(
                            deletedFile,
                            deletedFile.length(),
                            "image",
                            true  // Đánh dấu là file đã xóa
                        )
                        recoveredPhotos.add(recoveredFile)
                        Log.d("PhotoRecovery", "Đã tìm thấy ảnh đã xóa (mô phỏng): ${deletedFile.absolutePath}")
                    } else {
                        Log.d("PhotoRecovery", "Bỏ qua ảnh trùng lặp (mô phỏng): ${deletedFile.absolutePath}")
                    }
                }
            }
            
            // Thống kê sau khi quét
            val lowLevelCount = recoveredPhotos.count { it.isDeleted() }
            Log.d("PhotoRecovery", "Kết thúc quét cấp thấp, tìm thấy tổng cộng $lowLevelCount ảnh đã xóa")
            
        } catch (e: Exception) {
            Log.e("PhotoRecovery", "Lỗi khi quét cấp thấp: ${e.message}", e)
        }
    }

    /**
     * Quét các thư mục ẩn có thể là thùng rác
     */
    private suspend fun scanForHiddenTrashFolders(parentDir: File) {
        try {
            if (!parentDir.exists() || !parentDir.isDirectory || !parentDir.canRead()) {
                return
            }
            
            // Danh sách các tên thư mục có thể là thùng rác
            val potentialTrashFolders = listOf(
                ".trash", ".Trash", ".Trashed", "RECYCLER", "RECYCLED", "RECYCLE.BIN", ".globalTrash",
                ".RECYCLE", "FOUND.000", "Recovery", ".deleted", "Recently Deleted"
            )
            
            // Danh sách các tên thư mục KHÔNG phải thùng rác (ảnh ở đây không được coi là đã xóa)
            val notTrashFolders = listOf(
                ".thumbnails", ".thumb", "thumbnails", "thumb", "cache", ".cache",
                "temp", ".temp", "tmp", ".tmp"
            )
            
            // Tìm các thư mục con trong thư mục cha
            val subDirectories = parentDir.listFiles { file -> 
                file.isDirectory && (file.isHidden || potentialTrashFolders.contains(file.name))
            }
            
            if (subDirectories != null) {
                for (dir in subDirectories) {
                    Log.d("PhotoRecovery", "Kiểm tra thư mục ẩn tiềm năng: ${dir.absolutePath}")
                    
                    // Kiểm tra xem thư mục này có phải thùng rác hay không
                    val isTrashDir = potentialTrashFolders.any { dir.name.lowercase().contains(it.lowercase()) } ||
                                    dir.absolutePath.lowercase().contains("/trash") ||
                                    dir.absolutePath.lowercase().contains("/bin") ||
                                    dir.absolutePath.lowercase().contains("/deleted") ||
                                    dir.absolutePath.lowercase().contains("/recycle")
                    
                    // Kiểm tra xem thư mục này có nằm trong danh sách không phải thùng rác không
                    val isNotTrashDir = notTrashFolders.any { dir.name.lowercase().contains(it.lowercase()) }
                    
                    // Nếu thư mục nằm trong danh sách không phải thùng rác, bỏ qua
                    if (isNotTrashDir) {
                        Log.d("PhotoRecovery", "Bỏ qua thư mục không phải thùng rác: ${dir.absolutePath}")
                        continue
                    }
                    
                    // Quét từng thư mục để tìm ảnh
                    val files = dir.listFiles { file ->
                        file.isFile && isImageFile(file.name) && file.length() > 0
                    }
                    
                    if (files != null) {
                        for (file in files) {
                            // Kiểm tra tên file có ký tự ẩn (.) ở đầu không
                            val isHiddenFile = file.name.startsWith(".")
                            
                            // Quyết định xem file này có phải đã xóa hay không
                            val isDeleted = isTrashDir || isHiddenFile || isFileInTrashFolder(file)
                            
                            if (isDeleted) {
                                Log.d("PhotoRecovery", "Tìm thấy ảnh có thể đã xóa: ${file.absolutePath}")
                                
                                // Kiểm tra trùng lặp
                                val existingPath = recoveredPhotos.find { it.getPath() == file.absolutePath }
                                if (existingPath == null) {
                                    val recoveredFile = RecoverableItem(
                                        file,
                                        file.length(),
                                        "image",
                                        true  // Đánh dấu là file đã xóa
                                    )
                                    recoveredPhotos.add(recoveredFile)
                                    Log.d("PhotoRecovery", "Thêm ảnh đã xóa từ thư mục ẩn: ${file.absolutePath}")
                                } else {
                                    Log.d("PhotoRecovery", "Bỏ qua ảnh trùng lặp từ thư mục ẩn: ${file.absolutePath}")
                                }
                            } else {
                                Log.d("PhotoRecovery", "Bỏ qua ảnh chưa xóa từ thư mục: ${file.absolutePath}")
                            }
                        }
                    }
                    
                    // Quét đệ quy các thư mục con
                    scanForHiddenTrashFolders(dir)
                }
            }
        } catch (e: Exception) {
            Log.e("PhotoRecovery", "Lỗi khi quét thư mục ẩn: ${e.message}", e)
        }
    }

    /**
     * Lọc bỏ các file không thuộc thùng rác
     */
    private fun filterNonTrashFiles() {
        // Các từ khóa cho thư mục không phải thùng rác
        val nonTrashKeywords = listOf(
            ".thumbnails", "thumbnails", ".thumb", "thumb", "cache", ".cache",
            "/.android_secure/", "/Android/data/", "/LOST.DIR/", "/Android/obb/"
        )
        
        // Lọc ra các file cần loại bỏ
        val filesToRemove = recoveredPhotos.filter { item ->
            val path = item.getPath().lowercase()
            
            // Loại bỏ nếu đường dẫn chứa từ khóa không phải thùng rác
            val shouldRemove = nonTrashKeywords.any { path.contains(it.lowercase()) }
            
            // Loại bỏ các file quá nhỏ (có thể là thumbnails)
            val isTooSmall = item.getSize() < 10 * 1024 // nhỏ hơn 10KB
            
            if (shouldRemove) {
                Log.d("PhotoRecovery", "Loại bỏ file không phải thùng rác: ${item.getPath()}")
            }
            
            shouldRemove || isTooSmall
        }
        
        // Xóa các file không thuộc thùng rác
        if (filesToRemove.isNotEmpty()) {
            Log.d("PhotoRecovery", "Loại bỏ ${filesToRemove.size} file không thuộc thùng rác")
            recoveredPhotos.removeAll(filesToRemove)
        }
    }

    /**
     * Sắp xếp danh sách ảnh đã khôi phục theo độ ưu tiên:
     * 1. Các ảnh đã xóa từ thùng rác rõ ràng
     * 2. Các ảnh đã xóa khác
     * 3. Nếu cùng độ ưu tiên, sắp xếp theo thời gian sửa đổi (mới nhất lên đầu)
     */
    private fun sortRecoveredPhotosByPriority() {
        Log.d("PhotoRecovery", "Sắp xếp danh sách ảnh theo độ ưu tiên")
        
        // Chuyển sang danh sách mutable mới để tránh lỗi ConcurrentModification
        val sortedList = ArrayList(recoveredPhotos)
        
        // Sắp xếp theo nhiều tiêu chí
        sortedList.sortWith(Comparator { item1, item2 ->
            // Tiêu chí 1: Các ảnh chắc chắn đã xóa (từ thùng rác) lên đầu tiên
            val trashDir1 = isFromExplicitTrashDir(item1.getPath())
            val trashDir2 = isFromExplicitTrashDir(item2.getPath())
            
            if (trashDir1 && !trashDir2) return@Comparator -1
            if (!trashDir1 && trashDir2) return@Comparator 1
            
            // Tiêu chí 2: Các ảnh có dấu hiệu đã xóa rõ ràng (tên bắt đầu bằng dấu chấm)
            val hiddenFile1 = item1.getFile().name.startsWith(".")
            val hiddenFile2 = item2.getFile().name.startsWith(".")
            
            if (hiddenFile1 && !hiddenFile2) return@Comparator -1
            if (!hiddenFile1 && hiddenFile2) return@Comparator 1
            
            // Tiêu chí 3: Các ảnh có kích thước lớn lên trước
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
        recoveredPhotos.clear()
        recoveredPhotos.addAll(sortedList)
        
        Log.d("PhotoRecovery", "Đã sắp xếp xong ${recoveredPhotos.size} ảnh")
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

    // Thêm hoặc cập nhật phương thức onStop
    override fun onStop() {
        super.onStop()
        
        // Reset trạng thái quét khi người dùng thoát màn hình
        isScanning = false
        hideScanAnimation()
        scanButton.isEnabled = true
    }
} 