package com.htnguyen.ifu

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

class MyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Khởi tạo ngôn ngữ cho ứng dụng từ SharedPreferences
        applyLanguage()
    }
    
    override fun attachBaseContext(base: Context) {
        // Lấy ngôn ngữ đã lưu
        val sharedPreferences = base.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val languageCode = sharedPreferences.getString("language", Locale.getDefault().language) ?: "en"
        
        // Áp dụng ngôn ngữ cho context
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val configuration = Configuration(base.resources.configuration)
        configuration.setLocale(locale)
        
        val context = base.createConfigurationContext(configuration)
        super.attachBaseContext(context)
    }
    
    /**
     * Áp dụng ngôn ngữ cho ứng dụng
     */
    private fun applyLanguage() {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val languageCode = sharedPreferences.getString("language", Locale.getDefault().language) ?: "en"
        
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
} 