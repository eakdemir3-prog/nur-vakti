package com.eavisionlabs.nurvakti.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eavisionlabs.nurvakti.data.*
import com.eavisionlabs.nurvakti.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar

enum class AppScreen {
    ONBOARDING,
    HOME,
    QURAN,
    PRAYER_TIMES,
    EXPLORE,
    PROFILE,
    QURAN_READER,
    QIBLA,
    TASBIH,
    DUAS,
    ASMAUL_HUSNA,
    RAMAZAN_HATIM,
    ZAKAT_CALCULATOR,
    KAZA_NAMAZ,
    SETTINGS,
    PREMIUM
}

data class ZakatCalculationResult(
    val totalWealthTry: Double = 0.0,
    val goldGramPriceTry: Double = 3450.0,
    val nisabThresholdTry: Double = 276621.0, // 80.18 grams * 3450 TL
    val isZakatObligatory: Boolean = false,
    val zakatAmountDueTry: Double = 0.0,
    val isCustomGoldPrice: Boolean = false
)

data class UiState(
    val currentScreen: AppScreen = AppScreen.HOME,
    val navigationStack: List<AppScreen> = listOf(AppScreen.HOME),
    val isOnboardingComplete: Boolean = true,

    // Prayer Schedule
    val prayerSchedule: DailyPrayerSchedule? = null,
    val selectedCity: City = CityProvider.defaultCity,
    val citySearchQuery: String = "",
    val filteredCities: List<City> = CityProvider.getAllCities(),
    val isCityPickerVisible: Boolean = false,
    val monthlySchedule: List<MonthlyDaySchedule> = emptyList(),
    val isMonthlyScheduleVisible: Boolean = false,
    val completedPrayersToday: Set<String> = emptySet(),

    // Quran
    val surahs: List<Surah> = QuranRepository.surahs,
    val quranSearchQuery: String = "",
    val selectedSurah: Surah? = null,
    val currentSurahVerses: List<Ayah> = emptyList(),
    val lastReadSurahNumber: Int = 1,
    val lastReadAyahNumber: Int = 1,
    val arabicFontSize: Int = 28,
    val mealFontSize: Int = 16,
    val readingTheme: String = "DARK", // "DARK", "LIGHT", "SEPIA"
    val showMeal: Boolean = true,
    val showArabic: Boolean = true,
    val favoriteSurahs: Set<Int> = emptySet(),

    // Audio Player
    val playerState: PlayerState = PlayerState(),
    val isAudioSheetExpanded: Boolean = false,

    // Compass
    val compassData: CompassData = CompassData(),

    // Tasbih
    val currentDhikrCount: Int = 0,
    val targetDhikrCount: Int = 33,
    val selectedDhikrTitle: String = "Sübhanallah",
    val selectedDhikrArabic: String = "سُبْحَانَ اللَّهِ",
    val selectedDhikrMeaning: String = "Allah her türlü eksiklikten münezzehtir.",
    val todayDhikrCompletedTotal: Int = 45,
    val isResetConfirmDialogOpen: Boolean = false,
    val isAddCustomDhikrDialogOpen: Boolean = false,

    // Duas & Asma
    val selectedDuaCategory: String = "sabah",
    val duaSearchQuery: String = "",
    val filteredDuas: List<DuaItem> = DuaRepository.getDuasByCategory("sabah"),
    val favoriteDuas: Set<String> = emptySet(),
    val asmaSearchQuery: String = "",
    val filteredAsma: List<AsmaItem> = AsmaulHusnaRepository.names,
    val favoriteAsma: Set<Int> = emptySet(),

    // Religious Days
    val religiousDays: List<ReligiousDayItem> = ReligiousDaysProvider.getReligiousDays(),

    // Hatim & Ramazan
    val completedJuzs: Set<Int> = emptySet(),
    val zakatResult: ZakatCalculationResult = ZakatCalculationResult(),

    // Premium & Kaza Namazı
    val isPremiumSheetVisible: Boolean = false,
    val isPremiumActive: Boolean = false,
    val isLocating: Boolean = false,
    val kazaSabah: Int = 12,
    val kazaOgle: Int = 12,
    val kazaIkindi: Int = 12,
    val kazaAksam: Int = 12,
    val kazaYatsi: Int = 12,
    val kazaVitir: Int = 12,

    // Theme & Settings
    val isDarkMode: Boolean = true
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = UserPreferences(application)
    val audioPlayerManager = AudioPlayerManager(application)
    val compassSensorManager = CompassSensorManager(application)
    val autoLocationManager = AutoLocationManager(application)
    private val vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    private val _uiState = MutableStateFlow(
        UiState(
            isOnboardingComplete = prefs.isOnboardingCompleted,
            currentScreen = if (prefs.isOnboardingCompleted) AppScreen.HOME else AppScreen.ONBOARDING,
            navigationStack = if (prefs.isOnboardingCompleted) listOf(AppScreen.HOME) else listOf(AppScreen.ONBOARDING),
            lastReadSurahNumber = prefs.lastReadSurahNumber,
            lastReadAyahNumber = prefs.lastReadAyahNumber,
            arabicFontSize = prefs.arabicFontSize,
            mealFontSize = prefs.mealFontSize,
            readingTheme = prefs.readingTheme,
            showMeal = prefs.showMeal,
            showArabic = prefs.showArabic,
            favoriteSurahs = prefs.getFavoriteSurahs(),
            favoriteDuas = prefs.getFavoriteDuas(),
            favoriteAsma = prefs.getFavoriteAsma(),
            currentDhikrCount = prefs.tasbihCurrentCount,
            targetDhikrCount = prefs.tasbihTarget,
            todayDhikrCompletedTotal = prefs.todayDhikrCompleted,
            completedPrayersToday = prefs.getCompletedPrayersToday(),
            completedJuzs = prefs.getHatimCompletedJuzs(),
            isPremiumActive = prefs.isPremiumActive
        )
    )
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        // Load initial city
        val city = CityProvider.getAllCities().find { it.id == prefs.selectedCityId } ?: CityProvider.defaultCity
        _uiState.value = _uiState.value.copy(selectedCity = city)

        // Try automatic location detection if permission already granted
        if (autoLocationManager.isLocationPermissionGranted()) {
            detectLocation()
        }

        // Calculate initial prayer times and start 1-sec ticking countdown
        refreshPrayerTimes()
        startPrayerCountdown()

        // Sync audio state
        viewModelScope.launch {
            audioPlayerManager.playerState.collect { pState ->
                _uiState.value = _uiState.value.copy(playerState = pState)
            }
        }

        // Sync compass state
        viewModelScope.launch {
            compassSensorManager.compassData.collect { cData ->
                _uiState.value = _uiState.value.copy(compassData = cData)
            }
        }
    }

    fun completeOnboarding() {
        prefs.isOnboardingCompleted = true
        _uiState.value = _uiState.value.copy(
            isOnboardingComplete = true,
            currentScreen = AppScreen.HOME,
            navigationStack = listOf(AppScreen.HOME)
        )
    }

    fun navigateTo(screen: AppScreen) {
        val stack = _uiState.value.navigationStack.toMutableList()
        if (screen == AppScreen.HOME || screen == AppScreen.QURAN || screen == AppScreen.PRAYER_TIMES || screen == AppScreen.EXPLORE || screen == AppScreen.PROFILE) {
            // Main bottom bar tab switch
            stack.clear()
            stack.add(screen)
        } else {
            stack.add(screen)
        }

        if (screen == AppScreen.QIBLA) {
            compassSensorManager.setLocation(
                _uiState.value.selectedCity.latitude,
                _uiState.value.selectedCity.longitude
            )
            compassSensorManager.startListening()
        } else {
            compassSensorManager.stopListening()
        }

        _uiState.value = _uiState.value.copy(
            currentScreen = screen,
            navigationStack = stack
        )
    }

    fun navigateBack(): Boolean {
        val stack = _uiState.value.navigationStack.toMutableList()
        if (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
            val previousScreen = stack.last()

            if (previousScreen == AppScreen.QIBLA) {
                compassSensorManager.startListening()
            } else {
                compassSensorManager.stopListening()
            }

            _uiState.value = _uiState.value.copy(
                currentScreen = previousScreen,
                navigationStack = stack
            )
            return true
        }
        return false
    }

    // Prayer Times & City
    fun selectCity(city: City) {
        prefs.selectedCityId = city.id
        prefs.selectedCityName = city.name
        prefs.selectedCityLat = city.latitude
        prefs.selectedCityLng = city.longitude

        _uiState.value = _uiState.value.copy(
            selectedCity = city,
            isCityPickerVisible = false
        )
        refreshPrayerTimes()
        compassSensorManager.setLocation(city.latitude, city.longitude)
    }

    fun searchCities(query: String) {
        val filtered = CityProvider.search(query)
        _uiState.value = _uiState.value.copy(
            citySearchQuery = query,
            filteredCities = filtered
        )
    }

    fun setCityPickerVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(isCityPickerVisible = visible)
    }

    fun detectLocation(onFinished: ((Boolean) -> Unit)? = null) {
        _uiState.value = _uiState.value.copy(isLocating = true)
        viewModelScope.launch {
            autoLocationManager.requestCurrentLocation { city ->
                _uiState.value = _uiState.value.copy(isLocating = false)
                if (city != null) {
                    selectCity(city)
                    triggerHaptic(isTargetReached = true)
                    onFinished?.invoke(true)
                } else {
                    onFinished?.invoke(false)
                }
            }
        }
    }

    fun setMonthlyScheduleVisible(visible: Boolean) {
        if (visible && _uiState.value.monthlySchedule.isEmpty()) {
            val monthly = PrayerCalculationEngine.generateMonthlySchedule(
                Calendar.getInstance(),
                _uiState.value.selectedCity.latitude,
                _uiState.value.selectedCity.longitude,
                _uiState.value.selectedCity.timezoneOffsetHours
            )
            _uiState.value = _uiState.value.copy(
                monthlySchedule = monthly,
                isMonthlyScheduleVisible = true
            )
        } else {
            _uiState.value = _uiState.value.copy(isMonthlyScheduleVisible = visible)
        }
    }

    fun togglePrayerCompletedToday(prayerCode: String) {
        val current = _uiState.value.completedPrayersToday.toMutableSet()
        if (current.contains(prayerCode)) {
            current.remove(prayerCode)
        } else {
            current.add(prayerCode)
            triggerHaptic()
        }
        prefs.setCompletedPrayersToday(current)
        _uiState.value = _uiState.value.copy(completedPrayersToday = current)
    }

    private fun refreshPrayerTimes() {
        val city = _uiState.value.selectedCity
        val schedule = PrayerCalculationEngine.calculateDailySchedule(
            cityName = city.name,
            districtName = city.country,
            calendar = Calendar.getInstance(),
            latitude = city.latitude,
            longitude = city.longitude,
            timezoneOffset = city.timezoneOffsetHours
        )
        _uiState.value = _uiState.value.copy(prayerSchedule = schedule)
    }

    private fun startPrayerCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val currentSchedule = _uiState.value.prayerSchedule
                if (currentSchedule != null && currentSchedule.nextPrayer != null) {
                    val remaining = (currentSchedule.nextPrayer.timestampMillis - System.currentTimeMillis()).coerceAtLeast(0L)
                    if (remaining == 0L) {
                        // Refresh to switch to next prayer period
                        refreshPrayerTimes()
                    } else {
                        _uiState.value = _uiState.value.copy(
                            prayerSchedule = currentSchedule.copy(remainingMillisToNext = remaining)
                        )
                    }
                }
            }
        }
    }

    // Quran
    fun searchSurahs(query: String) {
        val filtered = QuranRepository.searchSurahs(query)
        _uiState.value = _uiState.value.copy(
            quranSearchQuery = query,
            surahs = filtered
        )
    }

    fun openSurahReader(surah: Surah) {
        val verses = QuranRepository.getVersesForSurah(surah.number)
        prefs.lastReadSurahNumber = surah.number
        prefs.lastReadAyahNumber = 1
        _uiState.value = _uiState.value.copy(
            selectedSurah = surah,
            currentSurahVerses = verses,
            lastReadSurahNumber = surah.number,
            lastReadAyahNumber = 1
        )
        navigateTo(AppScreen.QURAN_READER)
    }

    fun setArabicFontSize(size: Int) {
        val clamped = size.coerceIn(20, 48)
        prefs.arabicFontSize = clamped
        _uiState.value = _uiState.value.copy(arabicFontSize = clamped)
    }

    fun setMealFontSize(size: Int) {
        val clamped = size.coerceIn(12, 28)
        prefs.mealFontSize = clamped
        _uiState.value = _uiState.value.copy(mealFontSize = clamped)
    }

    fun setReadingTheme(theme: String) {
        prefs.readingTheme = theme
        _uiState.value = _uiState.value.copy(readingTheme = theme)
    }

    fun toggleShowMeal() {
        val updated = !_uiState.value.showMeal
        prefs.showMeal = updated
        _uiState.value = _uiState.value.copy(showMeal = updated)
    }

    fun toggleShowArabic() {
        val updated = !_uiState.value.showArabic
        prefs.showArabic = updated
        _uiState.value = _uiState.value.copy(showArabic = updated)
    }

    fun toggleFavoriteSurah(surahNumber: Int) {
        prefs.toggleFavoriteSurah(surahNumber)
        _uiState.value = _uiState.value.copy(favoriteSurahs = prefs.getFavoriteSurahs())
        triggerHaptic()
    }

    // Audio
    fun playSurahAudio(surahNumber: Int) {
        audioPlayerManager.playSurah(surahNumber)
    }

    fun togglePlayPauseAudio() {
        audioPlayerManager.togglePlayPause()
    }

    fun seekAudioTo(positionMs: Int) {
        audioPlayerManager.seekTo(positionMs)
    }

    fun seekAudioRelative(seconds: Int) {
        audioPlayerManager.seekRelative(seconds)
    }

    fun setAudioPlaybackSpeed(speed: Float) {
        audioPlayerManager.setSpeed(speed)
    }

    fun setAudioSleepTimer(minutes: Int?) {
        audioPlayerManager.setSleepTimer(minutes)
    }

    fun setAudioSheetExpanded(expanded: Boolean) {
        _uiState.value = _uiState.value.copy(isAudioSheetExpanded = expanded)
    }

    // Tasbih
    fun incrementDhikr() {
        val next = _uiState.value.currentDhikrCount + 1
        val target = _uiState.value.targetDhikrCount
        val total = _uiState.value.todayDhikrCompletedTotal + 1

        triggerHaptic(isTargetReached = next == target)

        prefs.tasbihCurrentCount = next
        prefs.todayDhikrCompleted = total

        _uiState.value = _uiState.value.copy(
            currentDhikrCount = next,
            todayDhikrCompletedTotal = total
        )
    }

    fun decrementDhikr() {
        if (_uiState.value.currentDhikrCount > 0) {
            val prev = _uiState.value.currentDhikrCount - 1
            prefs.tasbihCurrentCount = prev
            _uiState.value = _uiState.value.copy(currentDhikrCount = prev)
            triggerHaptic()
        }
    }

    fun confirmResetDhikr() {
        prefs.tasbihCurrentCount = 0
        _uiState.value = _uiState.value.copy(
            currentDhikrCount = 0,
            isResetConfirmDialogOpen = false
        )
        triggerHaptic()
    }

    fun setResetConfirmDialogOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isResetConfirmDialogOpen = open)
    }

    fun setTargetDhikrCount(target: Int) {
        prefs.tasbihTarget = target
        _uiState.value = _uiState.value.copy(targetDhikrCount = target)
    }

    fun selectPresetDhikr(title: String, arabic: String, meaning: String, target: Int = 33) {
        prefs.tasbihTarget = target
        prefs.tasbihCurrentCount = 0
        _uiState.value = _uiState.value.copy(
            selectedDhikrTitle = title,
            selectedDhikrArabic = arabic,
            selectedDhikrMeaning = meaning,
            targetDhikrCount = target,
            currentDhikrCount = 0
        )
    }

    // Duas & Asma
    fun selectDuaCategory(categoryId: String) {
        val list = DuaRepository.getDuasByCategory(categoryId)
        _uiState.value = _uiState.value.copy(
            selectedDuaCategory = categoryId,
            filteredDuas = list,
            duaSearchQuery = ""
        )
    }

    fun searchDuas(query: String) {
        val filtered = DuaRepository.searchDuas(query)
        _uiState.value = _uiState.value.copy(
            duaSearchQuery = query,
            filteredDuas = filtered
        )
    }

    fun toggleFavoriteDua(duaId: String) {
        prefs.toggleFavoriteDua(duaId)
        _uiState.value = _uiState.value.copy(favoriteDuas = prefs.getFavoriteDuas())
        triggerHaptic()
    }

    fun searchAsma(query: String) {
        val filtered = AsmaulHusnaRepository.search(query)
        _uiState.value = _uiState.value.copy(
            asmaSearchQuery = query,
            filteredAsma = filtered
        )
    }

    fun toggleFavoriteAsma(number: Int) {
        prefs.toggleFavoriteAsma(number)
        _uiState.value = _uiState.value.copy(favoriteAsma = prefs.getFavoriteAsma())
        triggerHaptic()
    }

    // Hatim
    fun toggleJuzCompleted(juz: Int) {
        val set = _uiState.value.completedJuzs.toMutableSet()
        if (set.contains(juz)) {
            set.remove(juz)
        } else {
            set.add(juz)
            triggerHaptic()
        }
        prefs.setHatimCompletedJuzs(set)
        _uiState.value = _uiState.value.copy(completedJuzs = set)
    }

    // Zakat
    fun calculateZakat(
        goldGrams: Double,
        cashTry: Double,
        goodsTry: Double,
        debtsTry: Double,
        customGoldPrice: Double? = null
    ) {
        val goldPricePerGram = (customGoldPrice ?: 3450.0).coerceAtLeast(1.0)
        val goldValue = goldGrams * goldPricePerGram
        val totalAssets = goldValue + cashTry + goodsTry
        val netWealth = (totalAssets - debtsTry).coerceAtLeast(0.0)
        val nisab = 80.18 * goldPricePerGram // 80.18 grams standard gold nisab
        val isObligatory = netWealth >= nisab
        val zakatDue = if (isObligatory) netWealth * 0.025 else 0.0

        _uiState.value = _uiState.value.copy(
            zakatResult = ZakatCalculationResult(
                totalWealthTry = netWealth,
                goldGramPriceTry = goldPricePerGram,
                nisabThresholdTry = nisab,
                isZakatObligatory = isObligatory,
                zakatAmountDueTry = zakatDue,
                isCustomGoldPrice = customGoldPrice != null
            )
        )
    }

    fun toggleAppTheme() {
        val newDark = !_uiState.value.isDarkMode
        _uiState.value = _uiState.value.copy(isDarkMode = newDark)
    }

    // Premium & Kaza
    fun setPremiumSheetVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(isPremiumSheetVisible = visible)
    }

    fun activatePremium() {
        prefs.isPremiumActive = true
        _uiState.value = _uiState.value.copy(
            isPremiumActive = true,
            isPremiumSheetVisible = false
        )
        triggerHaptic(isTargetReached = true)
    }

    fun updateKazaCount(type: String, delta: Int) {
        when (type) {
            "sabah" -> {
                val next = (_uiState.value.kazaSabah + delta).coerceAtLeast(0)
                prefs.kazaSabah = next
                _uiState.value = _uiState.value.copy(kazaSabah = next)
            }
            "ogle" -> {
                val next = (_uiState.value.kazaOgle + delta).coerceAtLeast(0)
                prefs.kazaOgle = next
                _uiState.value = _uiState.value.copy(kazaOgle = next)
            }
            "ikindi" -> {
                val next = (_uiState.value.kazaIkindi + delta).coerceAtLeast(0)
                prefs.kazaIkindi = next
                _uiState.value = _uiState.value.copy(kazaIkindi = next)
            }
            "aksam" -> {
                val next = (_uiState.value.kazaAksam + delta).coerceAtLeast(0)
                prefs.kazaAksam = next
                _uiState.value = _uiState.value.copy(kazaAksam = next)
            }
            "yatsi" -> {
                val next = (_uiState.value.kazaYatsi + delta).coerceAtLeast(0)
                prefs.kazaYatsi = next
                _uiState.value = _uiState.value.copy(kazaYatsi = next)
            }
            "vitir" -> {
                val next = (_uiState.value.kazaVitir + delta).coerceAtLeast(0)
                prefs.kazaVitir = next
                _uiState.value = _uiState.value.copy(kazaVitir = next)
            }
        }
        triggerHaptic()
    }

    fun addBulkKazaDays(days: Int) {
        val s = _uiState.value.kazaSabah + days
        val o = _uiState.value.kazaOgle + days
        val i = _uiState.value.kazaIkindi + days
        val a = _uiState.value.kazaAksam + days
        val y = _uiState.value.kazaYatsi + days
        val v = _uiState.value.kazaVitir + days

        prefs.kazaSabah = s
        prefs.kazaOgle = o
        prefs.kazaIkindi = i
        prefs.kazaAksam = a
        prefs.kazaYatsi = y
        prefs.kazaVitir = v

        _uiState.value = _uiState.value.copy(
            kazaSabah = s,
            kazaOgle = o,
            kazaIkindi = i,
            kazaAksam = a,
            kazaYatsi = y,
            kazaVitir = v
        )
        triggerHaptic()
    }

    private fun triggerHaptic(isTargetReached: Boolean = false) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (isTargetReached) {
                    vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 150), -1))
                } else {
                    vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(if (isTargetReached) 200 else 30)
            }
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
        audioPlayerManager.stop()
        compassSensorManager.stopListening()
    }
}
