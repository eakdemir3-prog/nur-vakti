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
    versionCode = 21
    versionName = "1.2.5"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    // RevenueCat herkese açık Android SDK anahtarı (goog_ ile başlar). Uygulamada bulunması güvenlidir.
    val rcKey = (project.findProperty("revenuecatApiKey") as String?)
      ?: System.getenv("REVENUECAT_API_KEY")
      ?: ""
    buildConfigField("String", "REVENUECAT_API_KEY", "\"$rcKey\"")

    // AdMob kimlikleri gradle.properties'ten okunur. Boşsa release sürümünde reklam gösterilmez.
    fun prop(name: String) = (project.findProperty(name) as String?)?.trim().orEmpty()
    val admobApp = prop("admobAppId").ifEmpty { "ca-app-pub-3940256099942544~3347511713" }
    manifestPlaceholders["admobAppId"] = admobApp
    buildConfigField("String", "AD_INTERSTITIAL", "\"${prop("admobInterstitialId")}\"")
    buildConfigField("String", "AD_APP_OPEN", "\"${prop("admobAppOpenId")}\"")
    buildConfigField("String", "AD_REWARDED", "\"${prop("admobRewardedId")}\"")
    // Google ile giriş: Google Cloud'daki "Web uygulaması" OAuth istemci kimliği
    buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"${prop("googleWebClientId")}\"")
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
    // Kontrol APK'sı: Play sürümüyle aynı küçültme/R8 ayarları, ayrı paket adıyla yan yana kurulur
    create("qa") {
      initWith(getByName("release"))
      applicationIdSuffix = ".qa"
      versionNameSuffix = "-kontrol"
      signingConfig = signingConfigs.getByName("debug")
      matchingFallbacks += listOf("release")
      // Kendi reklamına tıklamamak için kontrol APK'sında Google test reklamları
      manifestPlaceholders["admobAppId"] = "ca-app-pub-3940256099942544~3347511713"
      buildConfigField("String", "AD_INTERSTITIAL", "\"ca-app-pub-3940256099942544/1033173712\"")
      buildConfigField("String", "AD_APP_OPEN", "\"ca-app-pub-3940256099942544/9257395921\"")
      buildConfigField("String", "AD_REWARDED", "\"ca-app-pub-3940256099942544/5224354917\"")
    }
    debug {
      // Test APK'sında Google'ın test reklamları gösterilir
      manifestPlaceholders["admobAppId"] = "ca-app-pub-3940256099942544~3347511713"
      buildConfigField("String", "AD_INTERSTITIAL", "\"ca-app-pub-3940256099942544/1033173712\"")
      buildConfigField("String", "AD_APP_OPEN", "\"ca-app-pub-3940256099942544/9257395921\"")
      buildConfigField("String", "AD_REWARDED", "\"ca-app-pub-3940256099942544/5224354917\"")
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
  // Ses dosyaları sıkıştırılmasın (doğrudan oynatılabilsin)
  androidResources { noCompress += listOf("ogg", "mp3") }
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
  implementation(libs.revenuecat.purchases)
  // play-services eski fragment 1.1.0 getiriyor; Play uyarısı için güncel sürüme zorlanır
  implementation(libs.androidx.fragment)
  implementation(libs.play.services.ads)
  implementation(libs.ump)
  implementation(libs.play.review)
  // AdMob eski WorkManager 2.7 getiriyor; R8 tam modda açılışta çöküyordu. Güncel sürüme zorlanır.
  implementation(libs.androidx.work.runtime)
  implementation(libs.androidx.credentials)
  implementation(libs.androidx.credentials.play.services)
  implementation(libs.googleid)
  implementation(libs.play.services.auth)

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
