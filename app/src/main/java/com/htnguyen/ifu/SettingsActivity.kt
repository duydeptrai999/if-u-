package com.htnguyen.ifu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupUI()
    }

    private fun setupUI() {
        // Nút quay lại
        val backButton = findViewById<ImageView>(R.id.ivBack)
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Phần premium
        val upgradeButton = findViewById<Button>(R.id.btnUpgrade)
        upgradeButton.setOnClickListener {
            // Hiện tại chỉ hiển thị Toast, sau này sẽ thêm chức năng nâng cấp thực sự
            Toast.makeText(this, R.string.upgrade_to_premium, Toast.LENGTH_SHORT).show()
        }

        // Đổi ngôn ngữ
        val languageSettingItem = findViewById<LinearLayout>(R.id.languageSettingItem)
        val currentLanguageText = findViewById<TextView>(R.id.tvCurrentLanguage)
        
        // Hiển thị ngôn ngữ hiện tại
        val currentLanguage = Locale.getDefault().language
        if (currentLanguage == "en") {
            currentLanguageText.setText(R.string.english)
        } else {
            currentLanguageText.setText(R.string.vietnamese)
        }
        
        languageSettingItem.setOnClickListener {
            // Đơn giản chỉ chuyển đổi giữa tiếng Việt và tiếng Anh
            // Trong thực tế, nên hiển thị dialog chọn ngôn ngữ
            if (currentLanguage == "en") {
                // Chuyển sang tiếng Việt
                setLocale("vi")
            } else {
                // Chuyển sang tiếng Anh
                setLocale("en")
            }
        }

        // Đánh giá ứng dụng
        val rateAppSettingItem = findViewById<LinearLayout>(R.id.rateAppSettingItem)
        rateAppSettingItem.setOnClickListener {
            try {
                val uri = Uri.parse("market://details?id=$packageName")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                startActivity(intent)
            } catch (e: Exception) {
                // Nếu không có Google Play, mở trong trình duyệt
                val uri = Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }

        // Chia sẻ ứng dụng
        val shareAppSettingItem = findViewById<LinearLayout>(R.id.shareAppSettingItem)
        shareAppSettingItem.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            val shareMessage = "${getString(R.string.app_name)}\nhttps://play.google.com/store/apps/details?id=$packageName"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
        }

        // Chính sách bảo mật
        val privacyPolicySettingItem = findViewById<LinearLayout>(R.id.privacyPolicySettingItem)
        privacyPolicySettingItem.setOnClickListener {
            // URL ví dụ, thay thế bằng URL thực tế của chính sách bảo mật của bạn
            openUrl("https://example.com/privacy-policy")
        }

        // Điều khoản sử dụng
        val termsOfUseSettingItem = findViewById<LinearLayout>(R.id.termsOfUseSettingItem)
        termsOfUseSettingItem.setOnClickListener {
            // URL ví dụ, thay thế bằng URL thực tế của điều khoản sử dụng của bạn
            openUrl("https://example.com/terms-of-use")
        }
    }

    private fun setLocale(languageCode: String) {
        // Thay đổi ngôn ngữ
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

        // Khởi động lại activity để áp dụng thay đổi ngôn ngữ
        recreate()
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
} 