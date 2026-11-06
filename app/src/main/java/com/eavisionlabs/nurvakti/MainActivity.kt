package com.eavisionlabs.nurvakti

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import android.graphics.Bitmap
import android.view.KeyEvent
import androidx.core.content.FileProvider
import com.eavisionlabs.nurvakti.ads.AdsManager
import com.eavisionlabs.nurvakti.account.DriveBackup
import com.eavisionlabs.nurvakti.ui.UiEvent
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.launch
import com.eavisionlabs.nurvakti.data.UserPreferences
import com.eavisionlabs.nurvakti.i18n.t
import com.eavisionlabs.nurvakti.ui.AppScreen
import com.eavisionlabs.nurvakti.ui.MainViewModel
import com.eavisionlabs.nurvakti.ui.UiState
import com.eavisionlabs.nurvakti.ui.components.*
import com.eavisionlabs.nurvakti.ui.screens.*
import com.eavisionlabs.nurvakti.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    companion object {
        const val EXTRA_OPEN_SCREEN = "open_screen"
        const val PRIVACY_URL = "https://eakdemir3-prog.github.io/fitnex-legal/nurvakti/gizlilik.html"
        const val PLAY_URL = "https://play.google.com/store/apps/details?id=com.aistudio.nurvakti.app"
    }

    private val viewModel: MainViewModel by viewModels()

    /** Arka plana geçiş zamanı (açılış reklamı için) */
    private var backgroundAt = 0L
    /** Paylaşım, harita, ayarlar gibi dış ekranlara gidildiyse bu zamana kadar açılış reklamı yok */
    private var suppressAppOpenUntil = 0L
    /** Paylaşımdan dönünce reklam molası */
    private var pendingAdBreak = false

    // --- Google Drive izni (onay ekranı gerekirse) ---------------------
    private var consentWaiter: kotlinx.coroutines.CompletableDeferred<String?>? = null
    private val consentLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { r ->
        consentWaiter?.complete(if (r.resultCode == RESULT_OK) DriveBackup.tokenFromResult(this, r.data) else null)
        consentWaiter = null
    }

    /** Drive erişim belirteci; [interactive] ise gerekirse izin ekranını açar */
    suspend fun driveToken(interactive: Boolean): String? =
        when (val a = runCatching { DriveBackup.authorize(this) }.getOrNull()) {
            is DriveBackup.Auth.Token -> a.value
            is DriveBackup.Auth.NeedsConsent -> if (!interactive) null else {
                val d = kotlinx.coroutines.CompletableDeferred<String?>()
                consentWaiter = d
                markExternalLaunch()
                consentLauncher.launch(androidx.activity.result.IntentSenderRequest.Builder(a.sender).build())
                d.await()
            }
            null -> null
        }

    fun accountActions() = AccountActions(
        signIn = { markExternalLaunch(); viewModel.signInWithGoogle(this) { driveToken(it) } },
        backup = { viewModel.backupToCloud({ driveToken(it) }) },
        restore = { viewModel.restoreFromCloud({ driveToken(it) }) { recreate() } },
        setAuto = { viewModel.setCloudAutoBackup(it) },
        signOut = { viewModel.signOutGoogle() },
        delete = { viewModel.deleteAccount { driveToken(it) } }
    )

    fun markExternalLaunch() { suppressAppOpenUntil = System.currentTimeMillis() + 10 * 60_000L }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val coldStart = savedInstanceState == null
        enableEdgeToEdge()
        handleIntent(intent)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { e ->
                    when (e) {
                        UiEvent.AdBreak -> AdsManager.maybeShowInterstitial(this@MainActivity)
                        UiEvent.AskReview -> askForReview()
                    }
                }
            }
        }

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            MyApplicationTheme(themeMode = uiState.themeMode) {
                val density = LocalDensity.current
                val direction = if (uiState.language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
                CompositionLocalProvider(
                    LocalDensity provides Density(density.density, density.fontScale * uiState.fontScale),
                    LocalLayoutDirection provides direction
                ) {
                    var showIntro by rememberSaveable { mutableStateOf(coldStart) }
                    Box(Modifier.fillMaxSize()) {
                        NurVaktiApp(activity = this@MainActivity, viewModel = viewModel, uiState = uiState)
                        if (showIntro) IntroAnimation(onFinished = { showIntro = false })
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.uiState.value.isOnboardingComplete) AdsManager.init(this)
        viewModel.autoBackupIfDue { driveToken(it) }
        viewModel.onAppResumed()
        val now = System.currentTimeMillis()
        if (pendingAdBreak) {
            pendingAdBreak = false
            AdsManager.maybeShowInterstitial(this)
        } else if (backgroundAt > 0L && now - backgroundAt > 30_000L && now > suppressAppOpenUntil) {
            AdsManager.maybeShowAppOpen(this)
        }
        backgroundAt = 0L
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations && !AdsManager.isShowingFullScreen) backgroundAt = System.currentTimeMillis()
    }

    /** Tesbih ekranında ses tuşlarıyla zikir */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP || event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            val st = viewModel.uiState.value
            if (st.currentScreen == AppScreen.TASBIH && st.tasbihVolumeKeys) {
                if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) viewModel.onVolumeKeyDhikr()
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun askForReview() {
        viewModel.markReviewRequested()
        val manager = ReviewManagerFactory.create(this)
        manager.requestReviewFlow().addOnCompleteListener { task ->
            if (task.isSuccessful && !isFinishing) {
                markExternalLaunch()
                manager.launchReviewFlow(this, task.result)
            }
        }
    }

    fun watchRewardedAd() {
        val wait = viewModel.uiState.value.rewardAvailableAt - System.currentTimeMillis()
        if (wait > 0) {
            val days = ((wait + 86_399_999L) / 86_400_000L).toInt()
            viewModel.showMessage(String.format(t("Ücretsiz deneme %d gün sonra yeniden açılır."), days)); return
        }
        val shown = AdsManager.showRewarded(this) { viewModel.grantRewardTrial() }
        if (!shown) viewModel.showMessage(t("Reklam şu an hazır değil, birazdan tekrar deneyin."))
    }

    /** Görsel kartı (JPEG) paylaşır */
    fun shareImage(bitmap: Bitmap) {
        try {
            val dir = java.io.File(cacheDir, "share").apply { mkdirs() }
            dir.listFiles()?.forEach { it.delete() }
            val file = java.io.File(dir, "nurvakti_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 92, it) }
            val uri = FileProvider.getUriForFile(this, "$packageName.files", file)
            val send = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, t("NurVakti ile hazırlandı") + " • " + PLAY_URL)
                clipData = android.content.ClipData.newRawUri("NurVakti", uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            markExternalLaunch()
            pendingAdBreak = true
            startActivity(Intent.createChooser(send, t("Paylaş")))
        } catch (_: Exception) {
            pendingAdBreak = false
            Toast.makeText(this, t("Paylaşım uygulaması bulunamadı."), Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleIntent(intent: Intent?) {
        val screen = intent?.getStringExtra(EXTRA_OPEN_SCREEN) ?: return
        // Bildirim / widget'tan açılışta açılış reklamı gösterilmez
        suppressAppOpenUntil = System.currentTimeMillis() + 60_000L
        viewModel.openFromIntent(screen)
        intent.removeExtra(EXTRA_OPEN_SCREEN)
    }

    fun share(text: String) {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        markExternalLaunch()
        try {
            startActivity(Intent.createChooser(send, t("Paylaş")))
        } catch (_: Exception) {
            Toast.makeText(this, t("Paylaşım uygulaması bulunamadı."), Toast.LENGTH_SHORT).show()
        }
    }

    fun findMosques() {
        markExternalLaunch()
        val city = viewModel.uiState.value.selectedCity
        val geo = Intent(Intent.ACTION_VIEW, Uri.parse("geo:${city.latitude},${city.longitude}?q=${Uri.encode(t("cami"))}"))
        try {
            startActivity(geo)
        } catch (_: Exception) {
            val web = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/${Uri.encode(t("cami"))}/@${city.latitude},${city.longitude},14z")
            )
            try { startActivity(web) } catch (_: Exception) {
                Toast.makeText(this, t("Harita uygulaması bulunamadı."), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openUrl(url: String) {
        markExternalLaunch()
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: Exception) {
            Toast.makeText(this, url, Toast.LENGTH_LONG).show()
        }
    }

    fun openExactAlarmSettings() {
        markExternalLaunch()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:$packageName")))
            } catch (_: Exception) {
                openAppNotificationSettings()
            }
        }
    }

    fun openAppNotificationSettings() {
        markExternalLaunch()
        val i = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        }
        try { startActivity(i) } catch (_: Exception) {}
    }

    fun displayName(uri: Uri): String? = try {
        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
            if (c.moveToFirst()) c.getString(0) else null
        }
    } catch (_: Exception) { null }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NurVaktiApp(
    activity: MainActivity,
    viewModel: MainViewModel,
    uiState: UiState
) {
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }

    // Uygulama öne geldiğinde izin durumlarını yenile (ayarlardan dönüş)
    val lifecycleOwner = LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, e -> if (e == Lifecycle.Event.ON_RESUME) viewModel.onPermissionsChanged() }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose { lifecycleOwner.lifecycle.removeObserver(obs) }
    }

    LaunchedEffect(uiState.message) {
        val msg = uiState.message ?: return@LaunchedEffect
        snackbar.showSnackbar(msg)
        viewModel.clearMessage()
    }

    // --- İzin ve dosya başlatıcıları -------------------------------------
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { viewModel.onPermissionsChanged() }

    val requestNotifications = {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            val prefs = UserPreferences(context)
            if (prefs.notificationPermissionAsked &&
                !activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
            ) {
                activity.openAppNotificationSettings()
            } else {
                prefs.notificationPermissionAsked = true
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            activity.openAppNotificationSettings()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.showMessage(t("Konumunuz alınıyor…"))
            viewModel.detectLocation { success ->
                viewModel.showMessage(
                    if (success) t("Konum güncellendi:") + " " + viewModel.uiState.value.selectedCity.displayName
                    else t("GPS konumu alınamadı, lütfen listeden seçin.")
                )
            }
        } else {
            viewModel.showMessage(t("Konum izni verilmedi. Şehir listesinden seçebilirsiniz."))
        }
    }

    val requestLocationAction = {
        if (viewModel.autoLocationManager.isLocationPermissionGranted()) {
            viewModel.showMessage(t("Konumunuz alınıyor…"))
            viewModel.detectLocation { success ->
                viewModel.showMessage(
                    if (success) t("Konum güncellendi:") + " " + viewModel.uiState.value.selectedCity.displayName
                    else t("GPS sinyali alınamadı. Şehir listesinden seçebilirsiniz.")
                )
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    val adhanPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) viewModel.setAdhanSound(uri, activity.displayName(uri) ?: t("Seçilen ses"))
    }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) viewModel.exportBackup(uri)
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) viewModel.importBackup(uri) { activity.recreate() }
    }

    // --- Gezinme -------------------------------------------------------
    val mainTabs = listOf(AppScreen.HOME, AppScreen.QURAN, AppScreen.PRAYER_TIMES, AppScreen.EXPLORE, AppScreen.PROFILE)
    val isMainTab = uiState.currentScreen in mainTabs

    if (!isMainTab && uiState.currentScreen != AppScreen.ONBOARDING) {
        BackHandler { viewModel.navigateBack() }
    } else if (uiState.currentScreen != AppScreen.HOME && isMainTab) {
        BackHandler { viewModel.navigateTo(AppScreen.HOME) }
    }

    // Giriş ekranı: Google ile giriş ya da hesapsız devam
    if (uiState.needsLogin) {
        Box(Modifier.fillMaxSize()) {
            LoginScreen(
                isBusy = uiState.cloudBusy,
                onGoogle = { activity.accountActions().signIn() },
                onGuest = { viewModel.continueAsGuest() },
                onOpenPrivacy = { activity.openUrl(MainActivity.PRIVACY_URL) }
            )
            SnackbarHost(snackbar, Modifier.align(androidx.compose.ui.Alignment.BottomCenter).navigationBarsPadding())
        }
        return
    }

    if (!uiState.isOnboardingComplete) {
        OnboardingScreen(
            onComplete = {
                viewModel.completeOnboarding()
                requestLocationAction()
                if (Build.VERSION.SDK_INT >= 33) requestNotifications()
            }
        )
        return
    }

    val openSettings = { viewModel.navigateTo(AppScreen.SETTINGS) }

    Scaffold(
        topBar = {
            if (isMainTab) {
                NurVaktiTopBar(
                    selectedCity = uiState.selectedCity,
                    onCityClick = { viewModel.setCityPickerVisible(true) },
                    onSettingsClick = openSettings,
                    onOpenPremium = { viewModel.navigateTo(AppScreen.PREMIUM) }
                )
            }
        },
        bottomBar = {
            Column {
                MiniAudioPlayer(
                    playerState = uiState.playerState,
                    onTogglePlayPause = { viewModel.togglePlayPauseAudio() },
                    onPlayNext = { viewModel.audioPlayerManager.playNext() },
                    onClose = { viewModel.audioPlayerManager.stop() },
                    onExpand = { viewModel.setAudioSheetExpanded(true) }
                )
                if (isMainTab) {
                    NurVaktiBottomNav(currentScreen = uiState.currentScreen, onNavigate = { viewModel.navigateTo(it) })
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        val soundPlaying by com.eavisionlabs.nurvakti.notify.AdhanService.playing.collectAsState()
        // Dış Scaffold'un bıraktığı boşluklar tüketilir; alt ekranların kendi üst çubukları durum çubuğu boşluğunu ikinci kez eklemesin
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).consumeWindowInsets(innerPadding)) {
            when (uiState.currentScreen) {
                AppScreen.QURAN -> QuranScreen(
                    uiState = uiState,
                    onSearchChange = { viewModel.searchSurahs(it) },
                    onOpenSurah = { viewModel.openSurahReader(it) },
                    onPlayAudio = { viewModel.playSurahAudio(it) },
                    onToggleFavorite = { viewModel.toggleFavoriteSurah(it) },
                    onOpenJuz = { viewModel.openJuz(it) },
                    onOpenAyah = { s, a -> viewModel.openAyah(s, a) },
                    onContinueReading = { viewModel.continueReading() },
                    onSetDailyGoal = { viewModel.setDailyPageGoal(it) }
                )

                AppScreen.PRAYER_TIMES -> PrayerTimesScreen(
                    uiState = uiState,
                    onOpenCityPicker = { viewModel.setCityPickerVisible(true) },
                    onOpenMonthlySchedule = { viewModel.setMonthlyScheduleVisible(it) },
                    onOpenSettings = openSettings,
                    onSetNotificationPref = { type, pref -> viewModel.setNotificationPref(type, pref) },
                    onShare = { activity.share(it) }
                )

                AppScreen.EXPLORE -> ExploreScreen(
                    uiState = uiState,
                    onNavigate = { viewModel.navigateTo(it) },
                    onOpenPremium = { viewModel.navigateTo(AppScreen.PREMIUM) },
                    onFindMosques = { activity.findMosques() },
                    onShare = { activity.share(it) }
                )

                AppScreen.PROFILE -> ProfileScreen(
                    uiState = uiState,
                    onNavigate = { viewModel.navigateTo(it) },
                    onOpenCityPicker = { viewModel.setCityPickerVisible(true) },
                    onToggleTheme = { viewModel.toggleAppTheme() },
                    onOpenPremium = { viewModel.navigateTo(AppScreen.PREMIUM) },
                    onAutoDetectLocation = { requestLocationAction() },
                    onOpenSettings = openSettings,
                    account = activity.accountActions()
                )

                AppScreen.SETTINGS -> SettingsScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() },
                    onRequestNotifications = { requestNotifications() },
                    onRequestExactAlarm = { activity.openExactAlarmSettings() },
                    onOpenSystemNotificationSettings = { activity.openAppNotificationSettings() },
                    onPickAdhan = { activity.markExternalLaunch(); adhanPicker.launch(arrayOf("audio/*")) },
                    onExport = { activity.markExternalLaunch(); exportLauncher.launch("nurvakti-yedek-${UserPreferences.dayKey()}.json") },
                    onImport = { activity.markExternalLaunch(); importLauncher.launch(arrayOf("application/json", "text/plain", "*/*")) },
                    onOpenPrivacy = { activity.openUrl(MainActivity.PRIVACY_URL) },
                    onOpenCityPicker = { viewModel.setCityPickerVisible(true) },
                    onAutoDetectLocation = { requestLocationAction() },
                    onAdPrivacy = if (AdsManager.privacyOptionsRequired(context)) ({ AdsManager.showPrivacyOptions(activity) }) else null,
                    account = activity.accountActions()
                )

                AppScreen.PRAYER_TRACKER -> PrayerTrackerScreen(
                    uiState = uiState,
                    onBack = { viewModel.navigateBack() },
                    onTogglePrayerDone = { viewModel.togglePrayerCompletedToday(it) },
                    onOpenPremium = { viewModel.navigateTo(AppScreen.PREMIUM) }
                )

                AppScreen.PREMIUM -> PremiumScreen(
                    uiState = uiState,
                    onBack = { viewModel.navigateBack() },
                    onPurchase = { plan -> activity.markExternalLaunch(); viewModel.purchase(activity, plan) },
                    onRestore = { viewModel.restorePurchases() },
                    onWatchReward = if (AdsManager.isConfigured) ({ activity.watchRewardedAd() }) else null
                )

                AppScreen.QURAN_READER -> QuranReaderScreen(
                    uiState = uiState,
                    onBack = { viewModel.navigateBack() },
                    onSetArabicFontSize = { viewModel.setArabicFontSize(it) },
                    onSetMealFontSize = { viewModel.setMealFontSize(it) },
                    onSetReadingTheme = { viewModel.setReadingTheme(it) },
                    onToggleShowMeal = { viewModel.toggleShowMeal() },
                    onToggleShowArabic = { viewModel.toggleShowArabic() },
                    onVisible = { viewModel.onReaderVisible(it) },
                    onToggleBookmark = { s, a -> viewModel.toggleBookmark(s, a) },
                    onPlayFrom = { index, repeat -> viewModel.playReaderFrom(index, repeat) },
                    onStopAudio = { viewModel.audioPlayerManager.stop() },
                    onTogglePause = { viewModel.togglePlayPauseAudio() },
                    onSetRepeat = { viewModel.setRepeatEach(it) },
                    onSetHifz = { a, m -> viewModel.setHifzHide(a, m) },
                    onSetReciter = { viewModel.setAyahReciter(it) },
                    onShare = { activity.share(it) },
                    onOpenPremium = { viewModel.navigateTo(AppScreen.PREMIUM) }
                )

                AppScreen.QIBLA -> QiblaScreen(uiState = uiState, onBack = { viewModel.navigateBack() })

                AppScreen.TASBIH -> TasbihScreen(
                    uiState = uiState,
                    onBack = { viewModel.navigateBack() },
                    onIncrement = { viewModel.incrementDhikr() },
                    onDecrement = { viewModel.decrementDhikr() },
                    onConfirmReset = { viewModel.confirmResetDhikr() },
                    onSetResetDialogOpen = { viewModel.setResetConfirmDialogOpen(it) },
                    onSetTarget = { viewModel.setTargetDhikrCount(it) },
                    onSelectPreset = { title, arabic, meaning, target -> viewModel.selectPresetDhikr(title, arabic, meaning, target) },
                    onSetAddDialogOpen = { viewModel.setAddCustomDhikrDialogOpen(it) },
                    onAddCustom = { title, arabic, meaning, target -> viewModel.addCustomDhikr(title, arabic, meaning, target) },
                    onRemoveCustom = { viewModel.removeCustomDhikr(it) },
                    onSetVibration = { viewModel.setTasbihVibration(it) },
                    onSetVolumeKeys = { viewModel.setTasbihVolumeKeys(it) }
                )

                AppScreen.DUAS -> DuasScreen(
                    uiState = uiState,
                    onBack = { viewModel.navigateBack() },
                    onSelectCategory = { viewModel.selectDuaCategory(it) },
                    onSearchChange = { viewModel.searchDuas(it) },
                    onToggleFavorite = { viewModel.toggleFavoriteDua(it) }
                )

                AppScreen.ASMAUL_HUSNA -> AsmaulHusnaScreen(
                    uiState = uiState,
                    onBack = { viewModel.navigateBack() },
                    onSearchChange = { viewModel.searchAsma(it) },
                    onToggleFavorite = { viewModel.toggleFavoriteAsma(it) }
                )

                AppScreen.RAMAZAN_HATIM -> RamazanHatimScreen(
                    uiState = uiState,
                    onBack = { viewModel.navigateBack() },
                    onToggleJuz = { viewModel.toggleJuzCompleted(it) },
                    onNavigateZakat = { viewModel.navigateTo(AppScreen.ZAKAT_CALCULATOR) },
                    onOpenJuz = { viewModel.openJuz(it) },
                    onShare = { activity.share(it) },
                    onShareImage = { activity.shareImage(it) },
                    onOpenPremium = { viewModel.navigateTo(AppScreen.PREMIUM) }
                )

                AppScreen.ZAKAT_CALCULATOR -> ZakatCalculatorScreen(
                    uiState = uiState,
                    onBack = { viewModel.navigateBack() },
                    onCalculate = { gold, cash, goods, debts, customPrice ->
                        viewModel.calculateZakat(gold, cash, goods, debts, customPrice)
                    }
                )

                AppScreen.KAZA_NAMAZ -> KazaNamazScreen(
                    uiState = uiState,
                    onBack = { viewModel.navigateBack() },
                    onUpdateKaza = { type, delta -> viewModel.updateKazaCount(type, delta) },
                    onAddBulkKazaDays = { days -> viewModel.addBulkKazaDays(days) },
                    onSetKazaPerDay = { viewModel.setKazaPerDay(it) }
                )

                AppScreen.GREETINGS -> GreetingsScreen(
                    onBack = { viewModel.navigateBack() },
                    onShare = { activity.share(it) },
                    onShareImage = { activity.shareImage(it) }
                )

                AppScreen.TOPICS -> TopicsScreen(
                    onBack = { viewModel.navigateBack() },
                    onOpenAyah = { s, a -> viewModel.openAyah(s, a) },
                    onShare = { activity.share(it) }
                )

                else -> HomeScreen(
                    uiState = uiState,
                    onNavigate = { viewModel.navigateTo(it) },
                    onTogglePrayerDone = { viewModel.togglePrayerCompletedToday(it) },
                    onOpenCityPicker = { viewModel.setCityPickerVisible(true) },
                    onAutoDetectLocation = { requestLocationAction() },
                    onOpenPremium = { viewModel.navigateTo(AppScreen.PREMIUM) },
                    onOpenAyah = { s, a -> viewModel.openAyah(s, a) },
                    onContinueReading = { viewModel.continueReading() },
                    onShare = { activity.share(it) },
                    onFindMosques = { activity.findMosques() },
                    onEnableNotifications = { requestNotifications() },
                    onShareImage = { activity.shareImage(it) }
                )
            }
        
            // Ses çalarken (bildirim izni kapalı olsa da) uygulama içinden durdurma çubuğu
            if (soundPlaying) {
                Surface(
                    modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter).padding(12.dp).fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                    color = com.eavisionlabs.nurvakti.ui.theme.GoldChampagne,
                    shadowElevation = 6.dp
                ) {
                    Row(Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = com.eavisionlabs.nurvakti.ui.theme.EmeraldDarkest)
                        Spacer(Modifier.width(10.dp))
                        Text(t("Ses çalıyor"), color = com.eavisionlabs.nurvakti.ui.theme.EmeraldDarkest, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, modifier = Modifier.weight(1f))
                        Button(
                            onClick = { com.eavisionlabs.nurvakti.notify.AdhanService.stop(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = com.eavisionlabs.nurvakti.ui.theme.EmeraldDarkest, contentColor = com.eavisionlabs.nurvakti.ui.theme.GoldChampagne)
                        ) { Text(t("Durdur")) }
                    }
                }
            }
        }
    }

    if (uiState.isCityPickerVisible) {
        CityPickerDialog(
            selectedCity = uiState.selectedCity,
            searchQuery = uiState.citySearchQuery,
            filteredCities = uiState.filteredCities,
            isLocating = uiState.isLocating,
            onAutoDetectLocation = { requestLocationAction() },
            onSearchChange = { viewModel.searchCities(it) },
            onSelectCity = { viewModel.selectCity(it) },
            onDismiss = { viewModel.setCityPickerVisible(false) }
        )
    }

    if (uiState.isAudioSheetExpanded) {
        ExpandedAudioPlayerSheet(
            playerState = uiState.playerState,
            onTogglePlayPause = { viewModel.togglePlayPauseAudio() },
            onSeekTo = { viewModel.seekAudioTo(it) },
            onSeekRelative = { viewModel.seekAudioRelative(it) },
            onPlayNext = { viewModel.audioPlayerManager.playNext() },
            onPlayPrevious = { viewModel.audioPlayerManager.playPrevious() },
            onSetSpeed = { viewModel.setAudioPlaybackSpeed(it) },
            onSetSleepTimer = { viewModel.setAudioSleepTimer(it) },
            onDismiss = { viewModel.setAudioSheetExpanded(false) },
            onSetReciter = { viewModel.setSurahReciter(it) }
        )
    }

}
