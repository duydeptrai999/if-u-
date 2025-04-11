package com.htnguyen.ifu

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

open class BaseActivity : AppCompatActivity() {
    
    private lateinit var sharedPreferences: SharedPreferences
    
    override fun attachBaseContext(newBase: Context) {
        // Lấy ngôn ngữ đã lưu
        val sharedPreferences = newBase.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val languageCode = sharedPreferences.getString("language", Locale.getDefault().language) ?: "en"
        
        // Áp dụng ngôn ngữ
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val configuration = Configuration(newBase.resources.configuration)
        configuration.setLocale(locale)
        
        val context = newBase.createConfigurationContext(configuration)
        super.attachBaseContext(context)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        
        // Đảm bảo ngôn ngữ được áp dụng
        applyLanguage()
    }
    
    /**
     * Áp dụng ngôn ngữ cho activity
     */
    protected fun applyLanguage() {
        val languageCode = sharedPreferences.getString("language", Locale.getDefault().language) ?: "en"
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
} 