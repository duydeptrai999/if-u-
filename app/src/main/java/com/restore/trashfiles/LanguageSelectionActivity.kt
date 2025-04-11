package com.restore.trashfiles

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.restore.trashfiles.ads.AdManager
import java.util.Locale

class LanguageSelectionActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var selectedLanguage: String = "en" // Default language is English
    
    // Language selection views
    private lateinit var cardEnglish: CardView
    private lateinit var cardVietnamese: CardView
    private lateinit var cardFrench: CardView
    private lateinit var cardSpanish: CardView
    private lateinit var cardGerman: CardView
    
    // Selected indicators
    private lateinit var ivEnglishSelected: View
    private lateinit var ivVietnameseSelected: View
    private lateinit var ivFrenchSelected: View
    private lateinit var ivSpanishSelected: View
    private lateinit var ivGermanSelected: View
    
    // Next button
    private lateinit var btnNext: TextView
    
    // Native Ad container
    private lateinit var nativeAdContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Kiểm tra xem đã chọn ngôn ngữ chưa
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        if (isLanguageSelected()) {
            // Nếu đã chọn ngôn ngữ, chuyển đến màn hình intro
            startIntroActivity()
            return
        }
        
        setContentView(R.layout.activity_language_selection)
        initViews()
        setupListeners()
        
        // Hiển thị quảng cáo native
        loadNativeAd()
    }
    
    private fun loadNativeAd() {
        // Hiển thị quảng cáo native
        val adManager = AdManager.getInstance()
        adManager.loadNativeAd(this, nativeAdContainer)
    }

    private fun initViews() {
        // Initialize card views
        cardEnglish = findViewById(R.id.cardEnglish)
        cardVietnamese = findViewById(R.id.cardVietnamese)
        cardFrench = findViewById(R.id.cardFrench)
        cardSpanish = findViewById(R.id.cardSpanish)
        cardGerman = findViewById(R.id.cardGerman)
        
        // Initialize selection indicators
        ivEnglishSelected = findViewById(R.id.ivEnglishSelected)
        ivVietnameseSelected = findViewById(R.id.ivVietnameseSelected)
        ivFrenchSelected = findViewById(R.id.ivFrenchSelected)
        ivSpanishSelected = findViewById(R.id.ivSpanishSelected)
        ivGermanSelected = findViewById(R.id.ivGermanSelected)
        
        // Initialize next button
        btnNext = findViewById(R.id.btnNext)
        
        // Initialize native ad container
        nativeAdContainer = findViewById(R.id.nativeAdContainer)
        
        // Set English as default selected language
        updateSelectedView("en")
    }

    private fun setupListeners() {
        // Setup card click listeners
        cardEnglish.setOnClickListener {
            updateSelectedView("en")
            animateCardSelection(cardEnglish)
        }
        
        cardVietnamese.setOnClickListener {
            updateSelectedView("vi")
            animateCardSelection(cardVietnamese)
        }
        
        cardFrench.setOnClickListener {
            updateSelectedView("fr")
            animateCardSelection(cardFrench)
        }
        
        cardSpanish.setOnClickListener {
            updateSelectedView("es")
            animateCardSelection(cardSpanish)
        }
        
        cardGerman.setOnClickListener {
            updateSelectedView("de")
            animateCardSelection(cardGerman)
        }
        
        // Setup next button click listener
        btnNext.setOnClickListener {
            setLocale(selectedLanguage)
            saveLanguagePreference(selectedLanguage)
            startIntroActivity()
        }
    }
    
    private fun animateCardSelection(selectedCard: CardView) {
        // Reset all cards
        resetCardAppearance(cardEnglish)
        resetCardAppearance(cardVietnamese)
        resetCardAppearance(cardFrench)
        resetCardAppearance(cardSpanish)
        resetCardAppearance(cardGerman)
        
        // Highlight the selected card
        selectedCard.apply {
            cardElevation = resources.getDimension(R.dimen.card_elevation_selected)
            // Optional: animate scale or other properties
            animate().scaleX(1.02f).scaleY(1.02f).setDuration(150).start()
        }
    }
    
    private fun resetCardAppearance(card: CardView) {
        card.apply {
            cardElevation = 0f
            animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start()
        }
    }
    
    private fun updateSelectedView(languageCode: String) {
        // Hide all selection indicators
        ivEnglishSelected.visibility = View.INVISIBLE
        ivVietnameseSelected.visibility = View.INVISIBLE
        ivFrenchSelected.visibility = View.INVISIBLE
        ivSpanishSelected.visibility = View.INVISIBLE
        ivGermanSelected.visibility = View.INVISIBLE
        
        // Show the selected language indicator
        when (languageCode) {
            "en" -> ivEnglishSelected.visibility = View.VISIBLE
            "vi" -> ivVietnameseSelected.visibility = View.VISIBLE
            "fr" -> ivFrenchSelected.visibility = View.VISIBLE
            "es" -> ivSpanishSelected.visibility = View.VISIBLE
            "de" -> ivGermanSelected.visibility = View.VISIBLE
        }
        
        // Update selected language
        selectedLanguage = languageCode
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    private fun saveLanguagePreference(languageCode: String) {
        val editor = sharedPreferences.edit()
        editor.putString("language", languageCode)
        editor.putBoolean("language_selected", true)
        editor.apply()
    }

    private fun isLanguageSelected(): Boolean {
        return sharedPreferences.getBoolean("language_selected", false)
    }

    private fun startIntroActivity() {
        val intent = Intent(this, IntroActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        // Giải phóng tài nguyên quảng cáo khi activity bị hủy
        super.onDestroy()
    }
} 