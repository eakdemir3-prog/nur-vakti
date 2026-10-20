package com.eavisionlabs.nurvakti

import android.Manifest
import android.content.Context
import android.os.Bundle
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.eavisionlabs.nurvakti.ui.AppScreen
import com.eavisionlabs.nurvakti.ui.MainViewModel
import com.eavisionlabs.nurvakti.ui.components.*
import com.eavisionlabs.nurvakti.ui.screens.*
import com.eavisionlabs.nurvakti.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            MyApplicationTheme(darkTheme = uiState.isDarkMode) {
                NurVaktiApp(
                    viewModel = viewModel,
                    uiState = uiState
                )
            }
        }
    }
}

@Composable
fun NurVaktiApp(
    viewModel: MainViewModel,
    uiState: com.eavisionlabs.nurvakti.ui.UiState
) {
    val context = LocalContext.current

    // Auto-detect location seamlessly on launch if permission is already granted
    androidx.compose.runtime.LaunchedEffect(Unit) {
        if (viewModel.autoLocationManager.isLocationPermissionGranted()) {
            viewModel.detectLocation { _ -> }
        }
    }

    // Location Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            Toast.makeText(context, "Konum izni verildi, GPS ile konumunuz alınıyor...", Toast.LENGTH_SHORT).show()
            viewModel.detectLocation { success ->
                if (success) {
                    Toast.makeText(context, "Konumunuz güncellendi: ${viewModel.uiState.value.selectedCity.name}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "GPS konumu alınamadı, lütfen şehir listesinden seçin.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Konum izni verilmedi. Şehir listesinden manuel seçim yapabilirsiniz.", Toast.LENGTH_SHORT).show()
        }
    }

    val requestLocationAction = {
        if (viewModel.autoLocationManager.isLocationPermissionGranted()) {
            Toast.makeText(context, "GPS ile konumunuz aranıyor...", Toast.LENGTH_SHORT).show()
            viewModel.detectLocation { success ->
                if (success) {
                    Toast.makeText(context, "Konumunuz güncellendi: ${viewModel.uiState.value.selectedCity.name}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "GPS sinyali alınamadı. Şehir listesinden seçim yapabilirsiniz.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Top-level back handler for sub-screens
    val isMainTab = uiState.currentScreen in listOf(
        AppScreen.HOME,
        AppScreen.QURAN,
        AppScreen.PRAYER_TIMES,
        AppScreen.EXPLORE,
        AppScreen.PROFILE
    )

    if (!isMainTab && uiState.currentScreen != AppScreen.ONBOARDING) {
        BackHandler {
            viewModel.navigateBack()
        }
    }

    if (!uiState.isOnboardingComplete) {
        OnboardingScreen(
            onComplete = {
                viewModel.completeOnboarding()
                requestLocationAction()
            }
        )
        return
    }

    Scaffold(
        topBar = {
            if (isMainTab) {
                NurVaktiTopBar(
                    selectedCity = uiState.selectedCity,
                    onCityClick = { viewModel.setCityPickerVisible(true) },
                    onSettingsClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                    onOpenPremium = { viewModel.navigateTo(AppScreen.PREMIUM) }
                )
            }
        },
        bottomBar = {
            Column {
                // Persistent Mini Audio Player (appears on any screen when audio is active)
                MiniAudioPlayer(
                    playerState = uiState.playerState,
                    onTogglePlayPause = { viewModel.togglePlayPauseAudio() },
                    onPlayNext = { viewModel.audioPlayerManager.playNext() },
                    onClose = { viewModel.audioPlayerManager.stop() },
                    onExpand = { viewModel.setAudioSheetExpanded(true) }
                )

                if (isMainTab) {
                    NurVaktiBottomNav(
                        currentScreen = uiState.currentScreen,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentScreen) {
                AppScreen.HOME -> HomeScreen(
                    uiState = uiState,
                    onNavigate = { viewModel.navigateTo(it) },
                    onTogglePrayerDone = { viewModel.togglePrayerCompletedToday(it) },
                    onOpenCityPicker = { viewModel.setCityPickerVisible(true) },
                    onAutoDetectLocation = { requestLocationAction() },
                    onOpenPremium = { viewModel.navigateTo(AppScreen.PREMIUM) }
                )

                AppScreen.QURAN -> QuranScreen(
                    uiState = uiState,
                    onSearchChange = { viewModel.searchSurahs(it) },
                    onOpenSurah = { viewModel.openSurahReader(it) },
                    onPlayAudio = { viewModel.playSurahAudio(it) },
                    onToggleFavorite = { viewModel.toggleFavoriteSurah(it) }
                )

                AppScreen.PRAYER_TIMES -> PrayerTimesScreen(
                    uiState = uiState,
                    onOpenCityPicker = { viewModel.setCityPickerVisible(true) },
                    onOpenMonthlySchedule = { viewModel.setMonthlyScheduleVisible(it) }
                )

                AppScreen.EXPLORE -> ExploreScreen(
                    uiState = uiState,
                    onNavigate = { viewModel.navigateTo(it) },
                    onOpenPremium = { viewModel.navigateTo(AppScreen.PREMIUM) }
                )

                AppScreen.PROFILE -> ProfileScreen(
                    uiState = uiState,
                    onNavigate = { viewModel.navigateTo(it) },
                    onOpenCityPicker = { viewModel.setCityPickerVisible(true) },
                    onToggleTheme = { viewModel.toggleAppTheme() },
                    onOpenPremium = { viewModel.navigateTo(AppScreen.PREMIUM) },
                    onAutoDetectLocation = { requestLocationAction() }
                )

                AppScreen.PREMIUM -> PremiumScreen(
                    uiState = uiState,
                    onBack = { viewModel.navigateBack() },
                    onActivatePremium = { viewModel.activatePremium() }
                )

                AppScreen.QURAN_READER -> QuranReaderScreen(
                    surah = uiState.selectedSurah,
                    verses = uiState.currentSurahVerses,
                    uiState = uiState,
                    onBack = { viewModel.navigateBack() },
                    onPlayAudio = { viewModel.playSurahAudio(it) },
                    onSetArabicFontSize = { viewModel.setArabicFontSize(it) },
                    onSetMealFontSize = { viewModel.setMealFontSize(it) },
                    onSetReadingTheme = { viewModel.setReadingTheme(it) },
                    onToggleShowMeal = { viewModel.toggleShowMeal() },
                    onToggleShowArabic = { viewModel.toggleShowArabic() }
                )

                AppScreen.QIBLA -> QiblaScreen(
                    uiState = uiState,
                    onBack = { viewModel.navigateBack() }
                )

                AppScreen.TASBIH -> TasbihScreen(
                    uiState = uiState,
                    onBack = { viewModel.navigateBack() },
                    onIncrement = { viewModel.incrementDhikr() },
                    onDecrement = { viewModel.decrementDhikr() },
                    onConfirmReset = { viewModel.confirmResetDhikr() },
                    onSetResetDialogOpen = { viewModel.setResetConfirmDialogOpen(it) },
                    onSetTarget = { viewModel.setTargetDhikrCount(it) },
                    onSelectPreset = { title, arabic, meaning, target ->
                        viewModel.selectPresetDhikr(title, arabic, meaning, target)
                    }
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
                    onNavigateZakat = { viewModel.navigateTo(AppScreen.ZAKAT_CALCULATOR) }
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
                    onAddBulkKazaDays = { days -> viewModel.addBulkKazaDays(days) }
                )

                else -> HomeScreen(
                    uiState = uiState,
                    onNavigate = { viewModel.navigateTo(it) },
                    onTogglePrayerDone = { viewModel.togglePrayerCompletedToday(it) },
                    onOpenCityPicker = { viewModel.setCityPickerVisible(true) },
                    onAutoDetectLocation = { requestLocationAction() },
                    onOpenPremium = { viewModel.navigateTo(AppScreen.PREMIUM) }
                )
            }
        }
    }

    // Modal City Picker with Auto Location
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

    // Modal Expanded Full Audio Player Sheet
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
            onDismiss = { viewModel.setAudioSheetExpanded(false) }
        )
    }

    // Modal NurVakti Pro / Premium Sheet
    if (uiState.isPremiumSheetVisible) {
        NurVaktiPremiumSheet(
            isPremiumActive = uiState.isPremiumActive,
            onActivatePremium = { viewModel.activatePremium() },
            onOpenKazaTracker = { viewModel.navigateTo(AppScreen.KAZA_NAMAZ) },
            onDismiss = { viewModel.setPremiumSheetVisible(false) }
        )
    }
}
