plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
}

// Release imzalama bilgileri ortam değişkenlerinden okunur. Yoksa release çıktısı imzasız üretilir
// (AAB daha sonra jarsigner ile yükleme anahtarıyla imzalanır).
val releaseKeystorePath: String? = System.getenv("KEYSTORE_PATH")
val hasReleaseKeystore = releaseKeystorePath != null && file(releaseKeystorePath).exists()

android {
  namespace = "com.eavisionlabs.nurvakti"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    // Play Store kimliği değiştirilmez.
    applicationId = "com.aistudio.nurvakti.app"
    minSdk = 24
    targetSdk = 36
    versionCode = 2
    versionName = "1.0.1"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    if (hasReleaseKeystore) {
      create("release") {
        storeFile = file(releaseKeystorePath!!)
        storePassword = System.getenv("STORE_PASSWORD")
        keyAlias = System.getenv("KEY_ALIAS") ?: "upload"
        keyPassword = System.getenv("KEY_PASSWORD")
      }
    }
    getByName("debug") {
      val debugKs = file("${rootDir}/debug.keystore")
      if (debugKs.exists()) {
        storeFile = debugKs
        storePassword = "android"
        keyAlias = "androiddebugkey"
        keyPassword = "android"
      }
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      if (hasReleaseKeystore) signingConfig = signingConfigs.getByName("release")
    }
    debug {
      applicationIdSuffix = ".debug"
      versionNameSuffix = "-test"
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.play.services.location)
  implementation(libs.billing.ktx)

  testImplementation(libs.junit)
  testImplementation(libs.robolectric)
  testImplementation(libs.androidx.core)
  testImplementation(platform(libs.androidx.compose.bom))
  testImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
}
