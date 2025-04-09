# Cài đặt môi trường phát triển

## Tổng quan

Module này hướng dẫn cách thiết lập môi trường phát triển cho ứng dụng khôi phục file Android. Việc cài đặt đúng môi trường sẽ đảm bảo quá trình phát triển suôn sẻ và tuân thủ các tiêu chuẩn dự án.

## Cài đặt công cụ

### Android Studio

1. Tải Android Studio phiên bản mới nhất từ [trang chủ](https://developer.android.com/studio)
2. Cài đặt Android Studio theo hướng dẫn cho hệ điều hành của bạn
3. Cấu hình Android SDK:
   - Android SDK Platform 33 (Android 13) trở lên
   - Android SDK Build-Tools 33.0.0 trở lên
   - Android SDK Command-line Tools
   - Android Emulator
   - Android SDK Platform-Tools

### Kotlin

1. Sử dụng Kotlin 1.8.0 trở lên
2. Cấu hình qua Android Studio:
   - Settings > Build, Execution, Deployment > Build Tools > Gradle > Kotlin
   - Đảm bảo plugin Kotlin được cài đặt và kích hoạt

### JDK

1. Cài đặt JDK 11 hoặc cao hơn
2. Đặt biến môi trường JAVA_HOME trỏ đến thư mục cài đặt JDK
3. Cấu hình Android Studio sử dụng JDK đã cài đặt:
   - Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK

### Git

1. Cài đặt Git từ [git-scm.com](https://git-scm.com/)
2. Cấu hình Git:
   ```
   git config --global user.name "Tên của bạn"
   git config --global user.email "email@example.com"
   ```

## Thiết lập dự án

### Tạo dự án mới

1. Mở Android Studio
2. Chọn "New Project"
3. Chọn "Empty Activity" hoặc "Empty Compose Activity"
4. Cấu hình dự án:
   - Tên: "File Recovery"
   - Package name: "com.example.filerecovery" (hoặc domain thực tế)
   - Language: Kotlin
   - Minimum SDK: API 24 (Android 7.0)
   - Target SDK: API 33 (Android 13)
   - Use AndroidX artifacts: Đã chọn

### Cấu trúc dự án

Dự án sẽ được tổ chức theo kiến trúc MVVM, phân chia các thành phần như sau:

```
com.example.filerecovery/
├── data/                 # Data layer
│   ├── model/            # Model classes
│   ├── repository/       # Repositories
│   └── datasource/       # Data sources (local, remote)
├── domain/               # Domain layer (use cases)
│   ├── model/            # Domain models
│   ├── repository/       # Repository interfaces
│   └── usecase/          # Use cases
├── presentation/         # Presentation layer
│   ├── ui/               # UI components
│   │   ├── scan/         # Scan screen components
│   │   ├── recovery/     # Recovery screen components  
│   │   ├── detail/       # Detail screen components
│   │   ├── settings/     # Settings screen components
│   │   └── common/       # Shared UI components
│   ├── viewmodel/        # ViewModels
│   └── navigation/       # Navigation components
├── di/                   # Dependency injection
└── utils/                # Utility classes
```

### Thiết lập Gradle

1. **app/build.gradle**

Cập nhật file `app/build.gradle` với các dependency cần thiết:

```gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'kotlin-kapt'
    id 'dagger.hilt.android.plugin'
}

android {
    namespace 'com.example.filerecovery'
    compileSdk 33
    
    defaultConfig {
        applicationId "com.example.filerecovery"
        minSdk 24
        targetSdk 33
        versionCode 1
        versionName "1.0"
        
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary true
        }
    }
    
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = '11'
    }
    
    buildFeatures {
        compose true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion '1.4.0'
    }
    
    packagingOptions {
        resources {
            excludes += '/META-INF/{AL2.0,LGPL2.1}'
        }
    }
}

dependencies {
    // Core & Kotlin
    implementation 'androidx.core:core-ktx:1.10.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.6.1'
    
    // Compose
    implementation 'androidx.activity:activity-compose:1.7.0'
    implementation platform('androidx.compose:compose-bom:2023.01.00')
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.ui:ui-graphics'
    implementation 'androidx.compose.ui:ui-tooling-preview'
    implementation 'androidx.compose.material3:material3'
    
    // Navigation
    implementation 'androidx.navigation:navigation-compose:2.6.0'
    
    // Dependency Injection
    implementation 'com.google.dagger:hilt-android:2.44'
    kapt 'com.google.dagger:hilt-compiler:2.44'
    implementation 'androidx.hilt:hilt-navigation-compose:1.0.0'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4'
    
    // AdMob
    implementation 'com.google.android.gms:play-services-ads:22.0.0'
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
    androidTestImplementation platform('androidx.compose:compose-bom:2023.01.00')
    androidTestImplementation 'androidx.compose.ui:ui-test-junit4'
    debugImplementation 'androidx.compose.ui:ui-tooling'
    debugImplementation 'androidx.compose.ui:ui-test-manifest'
}
```

2. **project/build.gradle**

Cập nhật file `build.gradle` ở cấp project:

```gradle
buildscript {
    ext {
        compose_version = '1.4.0'
        kotlin_version = '1.8.0'
    }
    
    dependencies {
        classpath "com.google.dagger:hilt-android-gradle-plugin:2.44"
    }
}

plugins {
    id 'com.android.application' version '8.0.0' apply false
    id 'com.android.library' version '8.0.0' apply false
    id 'org.jetbrains.kotlin.android' version '1.8.0' apply false
}
```

### Thiết lập MVVM Architecture

1. Tạo các package cần thiết theo cấu trúc dự án đã định
2. Tạo file `Application` class:

```kotlin
package com.example.filerecovery

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FileRecoveryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Khởi tạo các thành phần app-wide tại đây
    }
}
```

3. Cập nhật AndroidManifest.xml:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:name=".FileRecoveryApplication"
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.FileRecovery"
        tools:targetApi="31">
        <activity
            android:name=".presentation.ui.MainActivity"
            android:exported="true"
            android:theme="@style/Theme.FileRecovery">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <!-- AdMob App ID -->
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy" />
    </application>
</manifest>
```

## Thiết lập Material Design 3

1. Tạo theme định nghĩa trong `app/src/main/java/com/example/filerecovery/presentation/ui/theme/Theme.kt`:

```kotlin
package com.example.filerecovery.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    // Các màu khác...
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    // Các màu khác...
)

@Composable
fun FileRecoveryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
```

2. Tạo MainActivity.kt:

```kotlin
package com.example.filerecovery.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.filerecovery.presentation.navigation.AppNavHost
import com.example.filerecovery.presentation.ui.theme.FileRecoveryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FileRecoveryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}
```

## Kiểm tra cài đặt

Sau khi hoàn thành các bước trên, thực hiện các kiểm tra sau để đảm bảo môi trường đã được thiết lập đúng:

1. Build dự án để kiểm tra các dependency đã được cài đặt đúng
2. Chạy ứng dụng trên emulator hoặc thiết bị thật
3. Kiểm tra cấu trúc dự án đã được tổ chức theo MVVM
4. Xác nhận Material Design 3 đã được áp dụng

## Các bước tiếp theo

Sau khi hoàn thành cài đặt môi trường, bạn có thể tiếp tục với các module tiếp theo:

1. [Quét & Phát hiện file](../core/scan-module.md)
2. [Khôi phục file](../core/recovery-module.md)

## Tài nguyên bổ sung

- [Android Developers](https://developer.android.com/)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android) 