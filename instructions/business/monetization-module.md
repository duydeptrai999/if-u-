# Module Monetization

## Tổng quan

Module này định nghĩa các thành phần và cấu trúc để tích hợp quảng cáo và mua hàng trong ứng dụng "Easy Recovery". Module này sẽ cung cấp nguồn thu từ cả quảng cáo và mua hàng, đồng thời đảm bảo trải nghiệm người dùng không bị gián đoạn quá mức.

## Thành phần chính

### 1. AdManager

**Mô tả**: Quản lý tất cả các loại quảng cáo trong ứng dụng.

**Yêu cầu chức năng**:
- Khởi tạo và cấu hình Google AdMob SDK
- Quản lý tất cả các loại quảng cáo:
  * Banner ads
  * Interstitial ads (toàn màn hình)
  * Native ads (tích hợp trong UI)
  * Rewarded ads (thưởng)
- Cơ chế điều chỉnh tần suất hiển thị quảng cáo
- Xử lý lỗi khi quảng cáo không tải được
- Phân tích hiệu suất quảng cáo

**Triển khai**:
```kotlin
package com.example.filerecovery.ads

import android.content.Context
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val billingManager: BillingManager
) {
    private var interstitialAd: InterstitialAd? = null
    private var lastAdShownTime = 0L
    private val adFrequencyLimit = 180000L // 3 phút giữa các quảng cáo

    init {
        // Khởi tạo Mobile Ads SDK
        MobileAds.initialize(context)
        
        // Cấu hình request
        val requestConfiguration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf("TEST_DEVICE_ID")) // Thay bằng ID thiết bị test thực tế
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        
        // Preload interstitial ad
        loadInterstitialAd()
    }
    
    /**
     * Tạo banner ad request
     */
    fun createBannerAd(): AdView {
        val adView = AdView(context)
        adView.adUnitId = AD_UNIT_ID_BANNER
        adView.adSize = AdSize.BANNER
        
        val adRequest = createAdRequest()
        adView.loadAd(adRequest)
        
        return adView
    }
    
    /**
     * Tải interstitial ad
     */
    private fun loadInterstitialAd() {
        if (billingManager.isUserPremium()) return
        
        val adRequest = createAdRequest()
        InterstitialAd.load(
            context,
            AD_UNIT_ID_INTERSTITIAL,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }
                
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    // Retry after delay
                    Handler(Looper.getMainLooper()).postDelayed({
                        loadInterstitialAd()
                    }, 30000) // 30 giây retry
                }
            }
        )
    }
    
    /**
     * Hiển thị interstitial ad
     * @return true nếu quảng cáo được hiển thị, false nếu không
     */
    fun showInterstitialAd(activity: Activity): Boolean {
        if (billingManager.isUserPremium()) return false
        
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAdShownTime < adFrequencyLimit) {
            return false // Không hiển thị quảng cáo quá thường xuyên
        }
        
        val ad = interstitialAd ?: return false
        
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                loadInterstitialAd() // Tải quảng cáo mới khi quảng cáo hiện tại đóng
            }
            
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
                loadInterstitialAd()
            }
            
            override fun onAdShowedFullScreenContent() {
                lastAdShownTime = System.currentTimeMillis()
            }
        }
        
        ad.show(activity)
        return true
    }
    
    private fun createAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }
    
    companion object {
        private const val AD_UNIT_ID_BANNER = "ca-app-pub-xxxxxxxxxxxxxxxx/yyyyyyyyyy"
        private const val AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-xxxxxxxxxxxxxxxx/zzzzzzzzzz"
        private const val AD_UNIT_ID_NATIVE = "ca-app-pub-xxxxxxxxxxxxxxxx/wwwwwwwwww"
    }
}
```

### 2. BillingManager

**Mô tả**: Quản lý các giao dịch mua hàng trong ứng dụng.

**Yêu cầu chức năng**:
- Khởi tạo và cấu hình Google Play Billing Library
- Định nghĩa các sản phẩm mua một lần và đăng ký
- Xử lý toàn bộ flow mua hàng
- Xác minh giao dịch với máy chủ Google Play
- Kiểm tra trạng thái đăng ký và khôi phục mua hàng
- Lưu trữ trạng thái premium của người dùng cục bộ

**Triển khai**:
```kotlin
package com.example.filerecovery.billing

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.android.billingclient.api.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PurchasesUpdatedListener, BillingClientStateListener {

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()
    
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val prefs: SharedPreferences = context.getSharedPreferences("billing_prefs", Context.MODE_PRIVATE)
    
    // State flows
    private val _premiumStatus = MutableStateFlow(false)
    val premiumStatus: StateFlow<Boolean> = _premiumStatus
    
    init {
        // Khởi tạo kết nối đến billing service
        billingClient.startConnection(this)
        
        // Khôi phục trạng thái premium từ local storage
        _premiumStatus.value = isPremiumSaved()
    }
    
    /**
     * Kiểm tra user có premium không
     */
    fun isUserPremium(): Boolean {
        return _premiumStatus.value
    }
    
    /**
     * Khởi chạy flow mua premium
     */
    fun launchPremiumPurchaseFlow(activity: Activity) {
        if (billingClient.isReady) {
            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(PRODUCT_ID_PREMIUM)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )
            
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()
                
            billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (productDetailsList.isNotEmpty()) {
                        val productDetails = productDetailsList[0]
                        
                        val billingFlowParams = BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(
                                listOf(
                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                        .setProductDetails(productDetails)
                                        .build()
                                )
                            )
                            .build()
                        
                        billingClient.launchBillingFlow(activity, billingFlowParams)
                    }
                }
            }
        } else {
            billingClient.startConnection(this)
        }
    }
    
    /**
     * Khôi phục các giao dịch mua trước đó
     */
    fun restorePurchases() {
        if (!billingClient.isReady) {
            billingClient.startConnection(this)
            return
        }
        
        coroutineScope.launch {
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
                
            billingClient.queryPurchasesAsync(params) { billingResult, purchasesList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    processPurchases(purchasesList)
                }
            }
        }
    }
    
    private fun processPurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged) {
                    acknowledgePurchase(purchase.purchaseToken)
                }
                
                if (purchase.products.contains(PRODUCT_ID_PREMIUM)) {
                    setPremiumStatus(true)
                }
            }
        }
    }
    
    private fun acknowledgePurchase(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
            
        billingClient.acknowledgePurchase(params) { /* Xử lý kết quả nếu cần */ }
    }
    
    private fun setPremiumStatus(isPremium: Boolean) {
        _premiumStatus.value = isPremium
        savePremiumStatus(isPremium)
    }
    
    private fun savePremiumStatus(isPremium: Boolean) {
        prefs.edit().putBoolean(KEY_PREMIUM_STATUS, isPremium).apply()
    }
    
    private fun isPremiumSaved(): Boolean {
        return prefs.getBoolean(KEY_PREMIUM_STATUS, false)
    }
    
    // BillingClientStateListener implementations
    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            restorePurchases()
        }
    }
    
    override fun onBillingServiceDisconnected() {
        // Xử lý mất kết nối đến billing service
    }
    
    // PurchasesUpdatedListener implementation
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            processPurchases(purchases)
        }
    }
    
    companion object {
        private const val PRODUCT_ID_PREMIUM = "premium_no_ads"
        private const val KEY_PREMIUM_STATUS = "premium_status"
    }
}
```

### 3. PremiumFeatures

**Mô tả**: Quản lý các tính năng premium và quyết định khi nào hiển thị chúng.

**Yêu cầu chức năng**:
- Quản lý danh sách tính năng premium
- Kiểm tra quyền truy cập tính năng dựa trên trạng thái premium
- Cung cấp các thông tin về tính năng premium cho UI
- Xử lý việc unlock tính năng khi mua premium

**Triển khai**:
```kotlin
package com.example.filerecovery.premium

import com.example.filerecovery.billing.BillingManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PremiumFeatures @Inject constructor(
    private val billingManager: BillingManager
) {
    /**
     * Kiểm tra xem một tính năng cụ thể có khả dụng không
     */
    fun isFeatureAvailable(feature: PremiumFeature): Boolean {
        return when (feature) {
            PremiumFeature.NO_ADS -> billingManager.isUserPremium()
            PremiumFeature.DEEP_SCAN -> billingManager.isUserPremium()
            PremiumFeature.CLOUD_BACKUP -> billingManager.isUserPremium()
            PremiumFeature.BATCH_RECOVERY -> true // Miễn phí cho tất cả người dùng
            PremiumFeature.SCHEDULED_SCAN -> billingManager.isUserPremium()
            PremiumFeature.FILE_VAULT -> billingManager.isUserPremium()
        }
    }
    
    /**
     * Lấy danh sách tính năng premium và trạng thái
     */
    fun getPremiumFeatures(): List<PremiumFeatureInfo> {
        return PremiumFeature.values().map { feature ->
            PremiumFeatureInfo(
                feature = feature,
                isAvailable = isFeatureAvailable(feature)
            )
        }
    }
    
    /**
     * Xử lý giới hạn phiên bản miễn phí
     */
    fun getFreeVersionLimits(): FreeVersionLimits {
        return FreeVersionLimits(
            maxRecoveryFilesPerDay = 10,
            maxRecoverySizePerDay = 100 * 1024 * 1024, // 100MB
            maxScanTimeMinutes = 2
        )
    }
}

/**
 * Các tính năng premium
 */
enum class PremiumFeature {
    NO_ADS,             // Xóa quảng cáo
    DEEP_SCAN,          // Quét sâu để tìm nhiều file hơn
    CLOUD_BACKUP,       // Backup lên cloud
    BATCH_RECOVERY,     // Khôi phục hàng loạt
    SCHEDULED_SCAN,     // Lên lịch quét tự động
    FILE_VAULT          // Mã hóa file khôi phục
}

/**
 * Thông tin chi tiết về tính năng premium
 */
data class PremiumFeatureInfo(
    val feature: PremiumFeature,
    val isAvailable: Boolean
)

/**
 * Giới hạn cho phiên bản miễn phí
 */
data class FreeVersionLimits(
    val maxRecoveryFilesPerDay: Int,
    val maxRecoverySizePerDay: Long,
    val maxScanTimeMinutes: Int
)
```

### 4. PremiumScreen

**Mô tả**: Màn hình hiển thị lợi ích của bản premium và flow mua hàng.

**Yêu cầu chức năng**:
- Hiển thị toàn bộ lợi ích của bản premium
- So sánh giữa Free và Premium
- Nút mua premium với giá hiển thị từ Play Billing
- Khôi phục giao dịch mua trước đó
- Liên kết chính sách bảo mật và điều khoản

**Triển khai**:
```kotlin
package com.example.filerecovery.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.filerecovery.premium.PremiumFeature
import com.example.filerecovery.ui.viewmodels.PremiumViewModel

@Composable
fun PremiumScreen(
    onBackClick: () -> Unit,
    viewModel: PremiumViewModel = hiltViewModel()
) {
    val isPremium by viewModel.isPremium.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        PremiumHeader(onBackClick)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Premium benefits
        PremiumBenefits()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Comparison table
        PremiumComparisonTable(isPremium)
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Buy button or "Already Premium" message
        if (isPremium) {
            AlreadyPremiumMessage()
        } else {
            PurchaseButton(
                price = viewModel.premiumPrice,
                onClick = { viewModel.purchasePremium() }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = { viewModel.restorePurchases() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Khôi phục giao dịch mua")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Privacy and terms
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = { viewModel.openPrivacyPolicy() }) {
                Text("Chính sách bảo mật")
            }
            
            TextButton(onClick = { viewModel.openTermsOfService() }) {
                Text("Điều khoản sử dụng")
            }
        }
    }
}

@Composable
fun PremiumHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Quay lại"
            )
        }
        
        Text(
            text = "Easy Recovery Premium",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PremiumBenefits() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nâng cấp lên Premium",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Khôi phục files không giới hạn, xóa quảng cáo và mở khóa tất cả tính năng cao cấp",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PremiumComparisonTable(isPremium: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Table header
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Tính năng",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Free",
                    modifier = Modifier.width(80.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Premium",
                    modifier = Modifier.width(80.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Feature rows
            FeatureRow(
                feature = "Không quảng cáo",
                freeAvailable = false,
                premiumAvailable = true,
                isPremium = isPremium
            )
            
            FeatureRow(
                feature = "Khôi phục không giới hạn",
                freeAvailable = false,
                premiumAvailable = true,
                isPremium = isPremium
            )
            
            FeatureRow(
                feature = "Quét sâu",
                freeAvailable = false,
                premiumAvailable = true,
                isPremium = isPremium
            )
            
            FeatureRow(
                feature = "Backup lên cloud",
                freeAvailable = false,
                premiumAvailable = true,
                isPremium = isPremium
            )
            
            FeatureRow(
                feature = "Lên lịch quét tự động",
                freeAvailable = false,
                premiumAvailable = true,
                isPremium = isPremium
            )
            
            FeatureRow(
                feature = "Mã hóa file",
                freeAvailable = false,
                premiumAvailable = true,
                isPremium = isPremium
            )
        }
    }
}

@Composable
fun FeatureRow(
    feature: String,
    freeAvailable: Boolean,
    premiumAvailable: Boolean,
    isPremium: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = feature,
            modifier = Modifier.weight(1f)
        )
        
        Box(
            modifier = Modifier.width(80.dp),
            contentAlignment = Alignment.Center
        ) {
            if (freeAvailable) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Có sẵn",
                    tint = Color.Green
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Không có sẵn",
                    tint = Color.Red
                )
            }
        }
        
        Box(
            modifier = Modifier.width(80.dp),
            contentAlignment = Alignment.Center
        ) {
            if (premiumAvailable) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Có sẵn",
                    tint = if (isPremium) Color.Green else MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Không có sẵn",
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun PurchaseButton(price: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = "Nâng cấp lên Premium - $price",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AlreadyPremiumMessage() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bạn đã là người dùng Premium!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Cảm ơn bạn đã ủng hộ Easy Recovery",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}
```

## Triển khai Monetization

### 1. Tích hợp AdMob

**Bước 1**: Thêm dependencies vào build.gradle

```kotlin
dependencies {
    // Google Play services ads
    implementation 'com.google.android.gms:play-services-ads:22.0.0'
    
    // Google Play billing
    implementation 'com.android.billingclient:billing-ktx:6.0.1'
}
```

**Bước 2**: Cấu hình AndroidManifest.xml

```xml
<manifest>
    <application>
        <!-- AdMob App ID -->
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy"/>
    </application>
</manifest>
```

**Bước 3**: Tạo các Ad Units trong AdMob console và sử dụng IDs trong code

### 2. Tích hợp Google Play Billing

**Bước 1**: Tạo sản phẩm trong Google Play Console
- Product ID: `premium_no_ads`
- Product Type: One-time product
- Giá: Xác định theo thị trường

**Bước 2**: Cài đặt test account
- Thêm test accounts trong Google Play Console
- Cấu hình license testing

**Bước 3**: Triển khai BillingManager (xem code bên trên)

### 3. Vị trí hiển thị quảng cáo

#### Banner Ads
- Hiển thị ở cuối màn hình chính (HomeScreen)
- Hiển thị ở cuối danh sách file được tìm thấy (ScanScreen)

```kotlin
@Composable
fun BannerAdView(
    adManager: AdManager,
    modifier: Modifier = Modifier
) {
    val bannerAd = remember { adManager.createBannerAd() }
    
    AndroidView(
        factory = { bannerAd },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    )
}
```

#### Interstitial Ads
- Hiển thị sau khi hoàn thành quá trình khôi phục
- Hiển thị sau khi hoàn thành quá trình quét sâu
- Tối đa 1 quảng cáo mỗi 3 phút để tránh ảnh hưởng trải nghiệm

```kotlin
// Gọi trong ViewModel sau khi hoàn thành recovery
fun showPostRecoveryAd(activity: Activity) {
    if (!billingManager.isUserPremium()) {
        adManager.showInterstitialAd(activity)
    }
}
```

#### Native Ads
- Tích hợp vào danh sách file tìm thấy (mỗi 10 items)
- Thiết kế phù hợp với UI của ứng dụng

```kotlin
@Composable
fun NativeAdItem(
    adManager: AdManager,
    modifier: Modifier = Modifier
) {
    val nativeAd = remember { adManager.createNativeAd() }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AndroidView(
            factory = { nativeAd },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
```

### 4. Giới hạn phiên bản Free

**Giới hạn cho người dùng miễn phí**:
- Giới hạn số lượng file khôi phục: 10 file/ngày
- Giới hạn kích thước khôi phục: 100MB/ngày
- Giới hạn thời gian quét: 2 phút

**Triển khai kiểm tra giới hạn**:
```kotlin
class FreeVersionLimitChecker @Inject constructor(
    private val billingManager: BillingManager,
    private val premiumFeatures: PremiumFeatures,
    private val statsRepository: StatsRepository
) {
    /**
     * Kiểm tra xem người dùng có thể khôi phục thêm file không
     */
    suspend fun canRecoverMoreFiles(fileSize: Long): Boolean {
        if (billingManager.isUserPremium()) return true
        
        val limits = premiumFeatures.getFreeVersionLimits()
        val stats = statsRepository.getTodayStats()
        
        return stats.recoveredFilesCount < limits.maxRecoveryFilesPerDay &&
                stats.recoveredFilesSize + fileSize <= limits.maxRecoverySizePerDay
    }
    
    /**
     * Cập nhật thống kê sau khi khôi phục
     */
    suspend fun updateRecoveryStats(filesCount: Int, totalSize: Long) {
        if (!billingManager.isUserPremium()) {
            statsRepository.updateTodayStats(filesCount, totalSize)
        }
    }
}
```

### 5. Theo dõi hiệu suất quảng cáo

**Analytics Tracker**:
```kotlin
class MonetizationAnalytics @Inject constructor(
    private val analytics: FirebaseAnalytics
) {
    fun trackAdImpression(adType: String) {
        val params = Bundle().apply {
            putString("ad_type", adType)
        }
        analytics.logEvent("ad_impression", params)
    }
    
    fun trackAdClick(adType: String) {
        val params = Bundle().apply {
            putString("ad_type", adType)
        }
        analytics.logEvent("ad_click", params)
    }
    
    fun trackPurchaseAttempt() {
        analytics.logEvent("premium_purchase_attempt", null)
    }
    
    fun trackPurchaseSuccess() {
        analytics.logEvent("premium_purchase_success", null)
    }
    
    fun trackPurchaseCancel() {
        analytics.logEvent("premium_purchase_cancel", null)
    }
    
    fun trackPurchaseError(error: String) {
        val params = Bundle().apply {
            putString("error_reason", error)
        }
        analytics.logEvent("premium_purchase_error", params)
    }
}
```

## Báo cáo và A/B Testing

### 1. Báo cáo doanh thu và hiệu suất quảng cáo

**Doanh thu từ quảng cáo**:
- Theo dõi eCPM (doanh thu trên mỗi nghìn lần hiển thị)
- Theo dõi fill rate (tỷ lệ quảng cáo được hiển thị)
- Theo dõi CTR (tỷ lệ click)

**Doanh thu từ mua hàng trong ứng dụng**:
- Theo dõi conversion rate (tỷ lệ chuyển đổi)
- Theo dõi LTV (giá trị vòng đời người dùng)
- Theo dõi retention (tỷ lệ giữ chân người dùng) của người dùng premium

### 2. A/B Testing cho monetization

**Các yếu tố có thể test**:
- Vị trí hiển thị quảng cáo
- Tần suất hiển thị quảng cáo
- Giá của gói premium
- UI của màn hình premium

**Triển khai với Firebase Remote Config**:
```kotlin
class MonetizationConfig @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig
) {
    fun getInterstitialFrequency(): Long {
        return remoteConfig.getLong("interstitial_frequency_seconds")
    }
    
    fun getNativeAdFrequency(): Int {
        return remoteConfig.getLong("native_ad_item_frequency").toInt()
    }
    
    fun shouldShowBannerOnHome(): Boolean {
        return remoteConfig.getBoolean("show_banner_on_home")
    }
    
    fun getPremiumButtonColor(): String {
        return remoteConfig.getString("premium_button_color")
    }
    
    fun getPremiumPromotionMessage(): String {
        return remoteConfig.getString("premium_promotion_message")
    }
}
```

## Kết luận

Module Monetization cung cấp cơ chế tạo doanh thu cho ứng dụng "Easy Recovery" thông qua:

1. Quảng cáo hiển thị ở các vị trí chiến lược
2. Gói premium để loại bỏ quảng cáo và mở khóa tính năng
3. Giới hạn phiên bản miễn phí để khuyến khích nâng cấp
4. Hệ thống theo dõi và phân tích để tối ưu doanh thu

Việc triển khai sẽ đảm bảo cân bằng giữa tạo doanh thu và trải nghiệm người dùng, tránh làm gián đoạn quá mức chức năng chính của ứng dụng. 