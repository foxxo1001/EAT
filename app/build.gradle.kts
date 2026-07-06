import java.io.File

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.todayeat"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.todayeat"
        minSdk = 29   // Android 10+，按用户要求
        targetSdk = 35
       versionCode = 47
       versionName = "4.14.3"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

dependencies {

    // ================================================================
    //  Compose BOM —— 统一管理 Compose 库版本
    // ================================================================
    val composeBom = platform("androidx.compose:compose-bom:2024.09.00")
    implementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.foundation:foundation")

    // ================================================================
    //  Activity Compose
    // ================================================================
    implementation("androidx.activity:activity-compose:1.9.2")

    // ================================================================
    //  Lifecycle
    // ================================================================
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")

    // ================================================================
    //  Coil 图片加载库 —— 核心依赖
    //  版本 2.6.0 提供 SubcomposeAsyncImage，支持 Compose 原生加载
    //  如果需要 SVG 支持，可额外添加 coil-svg
    // ================================================================
    implementation("io.coil-kt:coil-compose:2.6.0")

    // ================================================================
    //  Kotlin 协程（异步网络请求必需）
    // ================================================================
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // ================================================================
    //  Core KTX
    // ================================================================
    implementation("androidx.core:core-ktx:1.13.1")

    // ================================================================
    //  Room Database —— 本地持久化数据库
    //  用于存储抽中历史记录，支持 Flow 响应式查询
    // ================================================================
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // ================================================================
    //  调试工具
    // ================================================================
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
