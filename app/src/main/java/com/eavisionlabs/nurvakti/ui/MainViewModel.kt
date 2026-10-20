package com.eavisionlabs.nurvakti.ui

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eavisionlabs.nurvakti.billing.BillingManager
import com.eavisionlabs.nurvakti.data.*
import com.eavisionlabs.nurvakti.i18n.I18n
import com.eavisionlabs.nurvakti.i18n.t
import com.eavisionlabs.nurvakti.model.*
import com.eavisionlabs.nurvakti.notify.CountdownNotifier
import com.eavisionlabs.nurvakti.notify.NotificationHelper
import com.eavisionlabs.nurvakti.notify.PrayerAlarmScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    PREMIUM,
    PRAYER_TRACKER
}

data class ZakatCalculationResult(
    val totalWealthTry: Double = 0.0,
    val goldGramPriceTry: Double = 3450.0,
    val nisabThresholdTry: Double = 276621.0,
    val isZakatObligatory: Boolean = false,
    val zakatAmountDueTry: Double = 0.0,
    val isCustomGoldPrice: Boolean = false
)

/** Gün bazlı istatistik çubuğu */
data class DayStat(val label: String, val value: Int, val isToday: Boolean)

/** Okuyucuda gösterilen içerik: bir sure, bir cüz ya da arama sonucu */
enum class ReaderMode { SURAH, JUZ }

data class RamadanInfo(
    val isRamadan: Boolean = false,
    val dayOfRamadan: Int = 0,
    /** Ramazan başlangıcına kalan gün (Ramazan değilse) */
    val daysUntilRamadan: Int = -1,
    val startDateLabel: String = "",
    val imsakiye: List<MonthlyDaySchedule> = emptyList()
)

data class UiState(
    val currentScreen: AppScreen = AppScreen.HOME,
    val navigationStack: List<AppScreen> = listOf(AppScreen.HOME),
    val isOnboardingComplete: Boolean = true,

    // Namaz vakitleri
    val prayerSchedule: DailyPrayerSchedule? = null,
    val selectedCity: City = CityProvider.defaultCity,
    val citySearchQuery: String = "",
    val filteredCities: List<City> = CityProvider.getAllCities(),
    val isCityPickerVisible: Boolean = false,
    val monthlySchedule: List<MonthlyDaySchedule> = emptyList(),
    val isMonthlyScheduleVisible: Boolean = false,
    val completedPrayersToday: Set<String> = emptySet(),

    // Namaz takibi
    val prayerWeek: List<DayStat> = emptyList(),
    val prayerMonth: List<DayStat> = emptyList(),
    val prayerStreak: Int = 0,
    val prayerTotal30: Int = 0,

    // Kur'an
    val surahs: List<Surah> = QuranRepository.surahs,
    val quranSearchQuery: String = "",
    val verseSearchResults: List<Ayah> = emptyList(),
    val selectedSurah: Surah? = null,
    val readerMode: ReaderMode = ReaderMode.SURAH,
    val readerJuz: Int = 0,
    val currentSurahVerses: List<Ayah> = emptyList(),
    val readerScrollTarget: Int = 0,
    val lastReadSurahNumber: Int = 1,
    val lastReadAyahNumber: Int = 1,
    val lastReadPage: Int = 1,
    val arabicFontSize: Int = 28,
    val mealFontSize: Int = 16,
    val readingTheme: String = "DARK",
    val showMeal: Boolean = true,
    val showArabic: Boolean = true,
    val favoriteSurahs: Set<Int> = emptySet(),
    val bookmarks: Set<String> = emptySet(),
    val pagesReadToday: Int = 0,
    val dailyPageGoal: Int = 5,
    val hatimPagesRead: Int = 0,
    val completedHatims: Int = 0,
    val ayahReciter: String = "alafasy",
    val hifzHideArabic: Boolean = false,
    val hifzHideMeal: Boolean = false,
    val readingWeek: List<DayStat> = emptyList(),

    // Ses
    val playerState: PlayerState = PlayerState(),
    val isAudioSheetExpanded: Boolean = false,

    // Pusula
    val compassData: CompassData = CompassData(),

    // Tesbih
    val currentDhikrCount: Int = 0,
    val targetDhikrCount: Int = 33,
    val selectedDhikrTitle: String = "Sübhanallah",
    val selectedDhikrArabic: String = "سُبْحَانَ اللَّهِ",
    val selectedDhikrMeaning: String = "Allah her türlü eksiklikten münezzehtir.",
    val todayDhikrCompletedTotal: Int = 0,
    val isResetConfirmDialogOpen: Boolean = false,
    val isAddCustomDhikrDialogOpen: Boolean = false,
    val customDhikrs: List<List<String>> = emptyList(),
    val dhikrWeek: List<DayStat> = emptyList(),
    val tasbihVibration: Boolean = true,

    // Dualar & Esma
    val selectedDuaCategory: String = "sabah",
    val duaSearchQuery: String = "",
    val filteredDuas: List<DuaItem> = DuaRepository.getDuasByCategory("sabah"),
    val favoriteDuas: Set<String> = emptySet(),
    val asmaSearchQuery: String = "",
    val filteredAsma: List<AsmaItem> = AsmaulHusnaRepository.names,
    val favoriteAsma: Set<Int> = emptySet(),

    // Günün içerikleri
    val verseOfDay: DailyVerse? = null,
    val hadithOfDay: Hadith? = null,
    val duaOfDay: DuaItem? = null,

    // Dini günler / Ramazan
    val religiousDays: List<ReligiousDayItem> = emptyList(),
    val ramadan: RamadanInfo = RamadanInfo(),

    // Hatim & zekât
    val completedJuzs: Set<Int> = emptySet(),
    val zakatResult: ZakatCalculationResult = ZakatCalculationResult(),

    // Premium & kaza
    val isPremiumSheetVisible: Boolean = false,
    val isPremiumActive: Boolean = false,
    val premiumPrices: Map<String, String> = emptyMap(),
    val premiumProduct: String? = null,
    val isPurchasing: Boolean = false,
    val isLocating: Boolean = false,
    val kazaSabah: Int = 0,
    val kazaOgle: Int = 0,
    val kazaIkindi: Int = 0,
    val kazaAksam: Int = 0,
    val kazaYatsi: Int = 0,
    val kazaVitir: Int = 0,

    // Görünüm & dil
    val themeMode: String = "dark",
    val isDarkMode: Boolean = true,
    val fontScale: Float = 1f,
    val language: String = "tr",

    // Bildirim ayarları
    val notificationPrefs: Map<PrayerType, NotificationPreference> = emptyMap(),
    val preReminderMinutes: Int = 15,
    val ongoingCountdown: Boolean = false,
    val adhanSoundName: String? = null,
    val fridayReminder: Boolean = true,
    val kandilReminder: Boolean = true,
    val dailyVerseNotification: Boolean = true,
    val notificationsAllowed: Boolean = true,
    val exactAlarmAllowed: Boolean = true,
    val prayerAdjustments: List<Int> = List(6) { 0 },
    val hijriOffset: Int = 0,

    // Kısa bilgi mesajı (Snackbar)
    val message: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application
    private val prefs = UserPreferences(application)
    val audioPlayerManager = AudioPlayerManager(application)
    val compassSensorManager = CompassSensorManager(application)
    val autoLocationManager = AutoLocationManager(application)
    val billing = BillingManager(application)
    private val vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null
    private var currentDayKey = UserPreferences.dayKey()

    init {
        CityProvider.init(application)
        QuranRepository.init(application)
        I18n.language = prefs.language
        PrayerAlarmScheduler.applyEngineSettings(application)

        val city = prefs.loadCity() ?: CityProvider.findById(prefs.selectedCityId) ?: CityProvider.defaultCity
        val onboardingDone = prefs.isOnboardingCompleted

        _uiState.value = UiState(
            isOnboardingComplete = onboardingDone,
            currentScreen = if (onboardingDone) AppScreen.HOME else AppScreen.ONBOARDING,
            navigationStack = listOf(if (onboardingDone) AppScreen.HOME else AppScreen.ONBOARDING),
            selectedCity = city,
            lastReadSurahNumber = prefs.lastReadSurahNumber,
            lastReadAyahNumber = prefs.lastReadAyahNumber,
            lastReadPage = prefs.lastReadPage,
            arabicFontSize = prefs.arabicFontSize,
            mealFontSize = prefs.mealFontSize,
            readingTheme = prefs.readingTheme,
            showMeal = prefs.showMeal,
            showArabic = prefs.showArabic,
            favoriteSurahs = prefs.getFavoriteSurahs(),
            favoriteDuas = prefs.getFavoriteDuas(),
            favoriteAsma = prefs.getFavoriteAsma(),
            bookmarks = prefs.getBookmarks(),
            dailyPageGoal = prefs.dailyQuranGoalPages,
            completedHatims = prefs.completedHatimCount,
            ayahReciter = prefs.ayahReciterKey,
            currentDhikrCount = prefs.tasbihCurrentCount,
            targetDhikrCount = prefs.tasbihTarget,
            selectedDhikrTitle = prefs.selectedDhikrTitle,
            selectedDhikrArabic = prefs.selectedDhikrArabic,
            selectedDhikrMeaning = prefs.selectedDhikrMeaning,
            customDhikrs = prefs.getCustomDhikrs(),
            tasbihVibration = prefs.tasbihVibrationEnabled,
            completedJuzs = prefs.getHatimCompletedJuzs(),
            isPremiumActive = prefs.isPremiumActive,
            premiumProduct = prefs.premiumProductId,
            kazaSabah = prefs.kazaSabah,
            kazaOgle = prefs.kazaOgle,
            kazaIkindi = prefs.kazaIkindi,
            kazaAksam = prefs.kazaAksam,
            kazaYatsi = prefs.kazaYatsi,
            kazaVitir = prefs.kazaVitir,
            themeMode = prefs.themeMode,
            fontScale = prefs.fontScale,
            language = prefs.language,
            hijriOffset = prefs.hijriOffset,
            prayerAdjustments = prefs.getPrayerAdjustments().toList()
        )

        refreshDailyData()
        refreshPrayerTimes()
        refreshNotificationSettings()
        startPrayerCountdown()

        viewModelScope.launch(Dispatchers.Default) {
            val days = ReligiousDaysProvider.getReligiousDays()
            val ramadan = computeRamadanInfo(city)
            _uiState.update { it.copy(religiousDays = days, ramadan = ramadan) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            val verse = runCatching { DailyContent.verseOfDay(app) }.getOrNull()
            _uiState.update {
                it.copy(verseOfDay = verse, hadithOfDay = DailyContent.hadithOfDay(), duaOfDay = DailyContent.duaOfDay())
            }
        }

        viewModelScope.launch {
            audioPlayerManager.playerState.collect { pState -> _uiState.update { it.copy(playerState = pState) } }
        }
        viewModelScope.launch {
            compassSensorManager.compassData.collect { cData -> _uiState.update { it.copy(compassData = cData) } }
        }
        viewModelScope.launch {
            billing.state.collect { b ->
                _uiState.update {
                    it.copy(
                        isPremiumActive = b.isPremium,
                        premiumProduct = b.activeProduct,
                        premiumPrices = b.prices,
                        isPurchasing = b.isPurchasing,
                        message = b.message ?: it.message
                    )
                }
                if (b.message != null) billing.clearMessage()
                // Premium bittiyse Kiswa temasından çık
                if (!b.isPremium && prefs.themeMode == "kiswa") setThemeMode("dark")
            }
        }
        billing.connect()

        if (autoLocationManager.isLocationPermissionGranted() && prefs.loadCity()?.id == AutoLocationManager.AUTO_CITY_ID) {
            detectLocation()
        }
        rescheduleAlarms()
    }

    // ------------------------------------------------------------------
    // Gezinme
    // ------------------------------------------------------------------

    private val mainTabs = setOf(AppScreen.HOME, AppScreen.QURAN, AppScreen.PRAYER_TIMES, AppScreen.EXPLORE, AppScreen.PROFILE)

    fun completeOnboarding() {
        prefs.isOnboardingCompleted = true
        _uiState.update {
            it.copy(isOnboardingComplete = true, currentScreen = AppScreen.HOME, navigationStack = listOf(AppScreen.HOME))
        }
    }

    fun navigateTo(screen: AppScreen) {
        val stack = _uiState.value.navigationStack.toMutableList()
        if (screen in mainTabs) {
            stack.clear()
            stack.add(screen)
        } else if (stack.lastOrNull() != screen) {
            stack.add(screen)
        }
        if (screen == AppScreen.QIBLA) {
            compassSensorManager.setLocation(_uiState.value.selectedCity.latitude, _uiState.value.selectedCity.longitude)
            compassSensorManager.startListening()
        } else {
            compassSensorManager.stopListening()
        }
        when (screen) {
            AppScreen.PRAYER_TRACKER, AppScreen.PROFILE -> refreshStats()
            AppScreen.TASBIH -> refreshDhikrStats()
            AppScreen.SETTINGS -> refreshNotificationSettings()
            else -> Unit
        }
        _uiState.update { it.copy(currentScreen = screen, navigationStack = stack) }
    }

    fun navigateBack(): Boolean {
        val stack = _uiState.value.navigationStack.toMutableList()
        if (stack.size > 1) {
            val leaving = stack.removeAt(stack.lastIndex)
            if (leaving == AppScreen.QURAN_READER && _uiState.value.playerState.ayahMode) audioPlayerManager.stop()
            val previous = stack.last()
            if (previous == AppScreen.QIBLA) compassSensorManager.startListening() else compassSensorManager.stopListening()
            _uiState.update { it.copy(currentScreen = previous, navigationStack = stack) }
            return true
        }
        return false
    }

    /** Bildirim / widget'tan gelen ekran isteği */
    fun openFromIntent(screen: String?) {
        when (screen) {
            "PRAYER_TIMES" -> navigateTo(AppScreen.PRAYER_TIMES)
            "HOME" -> navigateTo(AppScreen.HOME)
            "SHARE_GREETING" -> navigateTo(AppScreen.HOME)
        }
    }

    fun showMessage(msg: String) = _uiState.update { it.copy(message = msg) }
    fun clearMessage() = _uiState.update { it.copy(message = null) }

    // ------------------------------------------------------------------
    // Konum & vakitler
    // ------------------------------------------------------------------

    fun selectCity(city: City) {
        prefs.saveCity(city)
        prefs.selectedCityName = city.name
        prefs.selectedCityLat = city.latitude
        prefs.selectedCityLng = city.longitude
        _uiState.update { it.copy(selectedCity = city, isCityPickerVisible = false, monthlySchedule = emptyList(), citySearchQuery = "", filteredCities = CityProvider.getAllCities()) }
        refreshPrayerTimes()
        compassSensorManager.setLocation(city.latitude, city.longitude)
        viewModelScope.launch(Dispatchers.Default) {
            val r = computeRamadanInfo(city)
            _uiState.update { it.copy(ramadan = r) }
        }
        rescheduleAlarms()
    }

    fun searchCities(query: String) {
        _uiState.update { it.copy(citySearchQuery = query) }
        viewModelScope.launch(Dispatchers.Default) {
            val filtered = CityProvider.search(query)
            if (_uiState.value.citySearchQuery == query) _uiState.update { it.copy(filteredCities = filtered) }
        }
    }

    fun setCityPickerVisible(visible: Boolean) = _uiState.update { it.copy(isCityPickerVisible = visible) }

    fun detectLocation(onFinished: ((Boolean) -> Unit)? = null) {
        _uiState.update { it.copy(isLocating = true) }
        viewModelScope.launch {
            autoLocationManager.requestCurrentLocation { city ->
                _uiState.update { it.copy(isLocating = false) }
                if (city != null) {
                    selectCity(city)
                    onFinished?.invoke(true)
                } else {
                    onFinished?.invoke(false)
                }
            }
        }
    }

    fun setMonthlyScheduleVisible(visible: Boolean) {
        if (visible && _uiState.value.monthlySchedule.isEmpty()) {
            val monthly = PrayerCalculationEngine.generateMonthlySchedule(_uiState.value.selectedCity)
            _uiState.update { it.copy(monthlySchedule = monthly, isMonthlyScheduleVisible = true) }
        } else {
            _uiState.update { it.copy(isMonthlyScheduleVisible = visible) }
        }
    }

    private fun refreshPrayerTimes() {
        val schedule = PrayerCalculationEngine.calculateDailySchedule(_uiState.value.selectedCity)
        _uiState.update { it.copy(prayerSchedule = schedule) }
    }

    private fun startPrayerCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val today = UserPreferences.dayKey()
                if (today != currentDayKey) {
                    currentDayKey = today
                    refreshDailyData()
                    refreshPrayerTimes()
                    continue
                }
                val s = _uiState.value.prayerSchedule ?: continue
                val next = s.nextPrayer ?: continue
                val remaining = (next.timestampMillis - System.currentTimeMillis()).coerceAtLeast(0L)
                if (remaining == 0L) refreshPrayerTimes()
                else _uiState.update { it.copy(prayerSchedule = s.copy(remainingMillisToNext = remaining)) }
            }
        }
    }

    /** Gün değişiminde günlük sayaçları ilgili günün kayıtlarından yükler. */
    private fun refreshDailyData() {
        val day = UserPreferences.dayKey()
        // Eski sürümden kalan "bugün" verisini bugüne taşı (bir kereye mahsus)
        if (prefs.lastDailyResetDay.isEmpty()) {
            val legacy = prefs.getCompletedPrayersToday()
            if (legacy.isNotEmpty()) prefs.setPrayersForDay(day, legacy)
            if (prefs.todayDhikrCompleted > 0) prefs.setDhikrForDay(day, prefs.todayDhikrCompleted)
        }
        prefs.lastDailyResetDay = day
        _uiState.update {
            it.copy(
                completedPrayersToday = prefs.getPrayersForDay(day),
                todayDhikrCompletedTotal = prefs.getDhikrForDay(day),
                pagesReadToday = prefs.getPagesReadForDay(day).size,
                hatimPagesRead = prefs.getHatimPages().size
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            val verse = runCatching { DailyContent.verseOfDay(app) }.getOrNull()
            _uiState.update { it.copy(verseOfDay = verse, hadithOfDay = DailyContent.hadithOfDay(), duaOfDay = DailyContent.duaOfDay()) }
        }
    }

    // ------------------------------------------------------------------
    // Namaz takibi
    // ------------------------------------------------------------------

    fun togglePrayerCompletedToday(prayerCode: String) {
        val day = UserPreferences.dayKey()
        val current = prefs.getPrayersForDay(day).toMutableSet()
        if (!current.remove(prayerCode)) {
            current.add(prayerCode)
            triggerHaptic()
        }
        prefs.setPrayersForDay(day, current)
        _uiState.update { it.copy(completedPrayersToday = current) }
        refreshStats()
    }

    private val shortDays = arrayOf("", "Paz", "Pzt", "Sal", "Çar", "Per", "Cum", "Cmt")

    fun refreshStats() {
        val history = prefs.prayerHistory(30)
        val fardh = setOf("IMSAK", "OGLE", "IKINDI", "AKSAM", "YATSI")
        fun count(set: Set<String>) = set.count { it.uppercase() in fardh || it in fardh }
        val month = history.mapIndexed { i, (c, set) ->
            DayStat(c.get(Calendar.DAY_OF_MONTH).toString(), count(set), i == history.lastIndex)
        }
        val week = history.takeLast(7).mapIndexed { i, (c, set) ->
            DayStat(t(shortDays[c.get(Calendar.DAY_OF_WEEK)]), count(set), i == 6)
        }
        // Seri: bugünden geriye 5 vaktin tamamının kılındığı ardışık gün sayısı (bugün tamamlanmadıysa dünden başlar)
        var streak = 0
        val reversed = history.reversed()
        for ((i, pair) in reversed.withIndex()) {
            val full = count(pair.second) >= 5
            if (full) streak++ else if (i == 0) continue else break
        }
        val readHistory = (6 downTo 0).map { i ->
            val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -i) }
            DayStat(t(shortDays[c.get(Calendar.DAY_OF_WEEK)]), prefs.getPagesReadForDay(UserPreferences.dayKey(c)).size, i == 0)
        }
        _uiState.update {
            it.copy(
                prayerWeek = week, prayerMonth = month, prayerStreak = streak,
                prayerTotal30 = month.sumOf { d -> d.value }, readingWeek = readHistory
            )
        }
        refreshDhikrStats()
    }

    private fun refreshDhikrStats() {
        val week = prefs.dhikrHistory(7).mapIndexed { i, (c, v) -> DayStat(t(shortDays[c.get(Calendar.DAY_OF_WEEK)]), v, i == 6) }
        _uiState.update { it.copy(dhikrWeek = week) }
    }

    // ------------------------------------------------------------------
    // Ramazan
    // ------------------------------------------------------------------

    private fun computeRamadanInfo(city: City): RamadanInfo {
        val today = Calendar.getInstance()
        val h = PrayerCalculationEngine.hijriParts(today)
        if (h.second == 9) {
            // İçinde bulunulan Ramazan: 1 Ramazan'ı bul
            val start = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, -(h.first - 1)) }
            val days = ramadanLength(start)
            return RamadanInfo(
                isRamadan = true,
                dayOfRamadan = h.first,
                startDateLabel = formatDate(start),
                imsakiye = PrayerCalculationEngine.scheduleForRange(city, start, days)
            )
        }
        // Gelecek Ramazan'ın ilk gününü ara (en fazla ~360 gün)
        val c = today.clone() as Calendar
        for (i in 1..400) {
            c.add(Calendar.DAY_OF_MONTH, 1)
            val p = PrayerCalculationEngine.hijriParts(c)
            if (p.second == 9 && p.first == 1) {
                return RamadanInfo(
                    isRamadan = false,
                    daysUntilRamadan = i,
                    startDateLabel = formatDate(c),
                    imsakiye = PrayerCalculationEngine.scheduleForRange(city, c, ramadanLength(c))
                )
            }
        }
        return RamadanInfo()
    }

    private fun ramadanLength(start: Calendar): Int {
        val c = start.clone() as Calendar
        var n = 0
        while (n < 31) {
            if (PrayerCalculationEngine.hijriParts(c).second != 9) break
            n++
            c.add(Calendar.DAY_OF_MONTH, 1)
        }
        return n.coerceIn(29, 30)
    }

    private fun formatDate(c: Calendar): String {
        val months = arrayOf("Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık")
        return "${c.get(Calendar.DAY_OF_MONTH)} ${t(months[c.get(Calendar.MONTH)])} ${c.get(Calendar.YEAR)}"
    }

    // ------------------------------------------------------------------
    // Kur'an
    // ------------------------------------------------------------------

    fun searchSurahs(query: String) {
        val filtered = QuranRepository.searchSurahs(query)
        _uiState.update { it.copy(quranSearchQuery = query, surahs = filtered) }
        viewModelScope.launch(Dispatchers.Default) {
            val verses = if (query.trim().length >= 3 && query.toIntOrNull() == null) QuranRepository.searchVerses(query, 50) else emptyList()
            if (_uiState.value.quranSearchQuery == query) _uiState.update { it.copy(verseSearchResults = verses) }
        }
    }

    fun openSurahReader(surah: Surah, startAyah: Int = 1) {
        viewModelScope.launch {
            val verses = withContext(Dispatchers.IO) { QuranRepository.getVersesForSurah(surah.number) }
            val target = (startAyah - 1).coerceIn(0, (verses.size - 1).coerceAtLeast(0))
            _uiState.update {
                it.copy(
                    selectedSurah = surah, readerMode = ReaderMode.SURAH, readerJuz = 0,
                    currentSurahVerses = verses, readerScrollTarget = target
                )
            }
            navigateTo(AppScreen.QURAN_READER)
        }
    }

    fun openAyah(surahNumber: Int, ayah: Int) {
        val surah = QuranRepository.surahs.getOrNull(surahNumber - 1) ?: return
        openSurahReader(surah, ayah)
    }

    fun continueReading() = openAyah(prefs.lastReadSurahNumber, prefs.lastReadAyahNumber)

    fun openJuz(juz: Int) {
        viewModelScope.launch {
            val verses = withContext(Dispatchers.IO) { QuranRepository.versesForJuz(juz) }
            val first = verses.firstOrNull() ?: return@launch
            _uiState.update {
                it.copy(
                    selectedSurah = QuranRepository.surahs.getOrNull(first.surahNumber - 1),
                    readerMode = ReaderMode.JUZ, readerJuz = juz,
                    currentSurahVerses = verses, readerScrollTarget = 0
                )
            }
            navigateTo(AppScreen.QURAN_READER)
        }
    }

    private var lastSavedKey = ""

    /** Okuyucuda ekrandaki ayetler değiştikçe çağrılır: kaldığı yeri ve okunan sayfaları kaydeder. */
    fun onReaderVisible(visible: List<Ayah>) {
        val first = visible.firstOrNull() ?: return
        val key = "${first.surahNumber}:${first.ayahNumber}"
        if (key == lastSavedKey) return
        lastSavedKey = key
        prefs.lastReadSurahNumber = first.surahNumber
        prefs.lastReadAyahNumber = first.ayahNumber
        if (first.page > 0) prefs.lastReadPage = first.page
        val pages = visible.map { it.page }.filter { it > 0 }.toSet()
        val day = UserPreferences.dayKey()
        val before = prefs.getPagesReadForDay(day).size
        prefs.addPagesRead(day, pages)
        prefs.addHatimPages(pages)
        val after = prefs.getPagesReadForDay(day).size
        var hatimPages = prefs.getHatimPages().size
        var hatims = prefs.completedHatimCount
        var msg: String? = null
        if (hatimPages >= QuranRepository.TOTAL_PAGES) {
            hatims += 1
            prefs.completedHatimCount = hatims
            prefs.resetHatimPages()
            hatimPages = 0
            msg = t("Tebrikler! Bir hatmi tamamladınız. Allah kabul etsin.")
            triggerHaptic(true)
        } else if (before < _uiState.value.dailyPageGoal && after >= _uiState.value.dailyPageGoal) {
            msg = t("Bugünkü okuma hedefinizi tamamladınız. Maşallah!")
            triggerHaptic(true)
        }
        _uiState.update {
            it.copy(
                lastReadSurahNumber = first.surahNumber, lastReadAyahNumber = first.ayahNumber,
                lastReadPage = if (first.page > 0) first.page else it.lastReadPage,
                pagesReadToday = after, hatimPagesRead = hatimPages, completedHatims = hatims,
                message = msg ?: it.message
            )
        }
    }

    fun toggleBookmark(surah: Int, ayah: Int) {
        val set = prefs.toggleBookmark(surah, ayah)
        _uiState.update { it.copy(bookmarks = set) }
        triggerHaptic()
    }

    fun setDailyPageGoal(pages: Int) {
        val v = pages.coerceIn(1, 40)
        prefs.dailyQuranGoalPages = v
        _uiState.update { it.copy(dailyPageGoal = v) }
    }

    fun setArabicFontSize(size: Int) {
        val clamped = size.coerceIn(20, 48)
        prefs.arabicFontSize = clamped
        _uiState.update { it.copy(arabicFontSize = clamped) }
    }

    fun setMealFontSize(size: Int) {
        val clamped = size.coerceIn(12, 28)
        prefs.mealFontSize = clamped
        _uiState.update { it.copy(mealFontSize = clamped) }
    }

    fun setReadingTheme(theme: String) {
        prefs.readingTheme = theme
        _uiState.update { it.copy(readingTheme = theme) }
    }

    fun toggleShowMeal() {
        val v = !_uiState.value.showMeal
        prefs.showMeal = v
        _uiState.update { it.copy(showMeal = v) }
    }

    fun toggleShowArabic() {
        val v = !_uiState.value.showArabic
        prefs.showArabic = v
        _uiState.update { it.copy(showArabic = v) }
    }

    fun toggleFavoriteSurah(surahNumber: Int) {
        prefs.toggleFavoriteSurah(surahNumber)
        _uiState.update { it.copy(favoriteSurahs = prefs.getFavoriteSurahs()) }
        triggerHaptic()
    }

    // Hıfz (Pro)
    fun setHifzHide(arabic: Boolean, meal: Boolean) {
        if (!requirePremium()) return
        _uiState.update { it.copy(hifzHideArabic = arabic, hifzHideMeal = meal) }
    }

    fun setAyahReciter(key: String) {
        prefs.ayahReciterKey = key
        _uiState.update { it.copy(ayahReciter = key) }
    }

    /** Okuyucudaki ayetleri [startIndex]'ten itibaren ayet ayet çalar. */
    fun playReaderFrom(startIndex: Int, repeatEach: Int = 1) {
        if (repeatEach > 1 && !requirePremium()) return
        val verses = _uiState.value.currentSurahVerses
        audioPlayerManager.playAyahs(verses.map { it.surahNumber to it.ayahNumber }, startIndex, repeatEach, _uiState.value.ayahReciter)
    }

    fun setRepeatEach(times: Int) {
        if (times > 1 && !requirePremium()) return
        audioPlayerManager.setRepeatEach(times)
    }

    // ------------------------------------------------------------------
    // Ses
    // ------------------------------------------------------------------

    fun playSurahAudio(surahNumber: Int) = audioPlayerManager.playSurah(surahNumber, prefs.selectedReciterKey)
    fun togglePlayPauseAudio() = audioPlayerManager.togglePlayPause()
    fun seekAudioTo(positionMs: Int) = audioPlayerManager.seekTo(positionMs)
    fun seekAudioRelative(seconds: Int) = audioPlayerManager.seekRelative(seconds)
    fun setAudioPlaybackSpeed(speed: Float) = audioPlayerManager.setSpeed(speed)
    fun setAudioSleepTimer(minutes: Int?) = audioPlayerManager.setSleepTimer(minutes)
    fun setAudioSheetExpanded(expanded: Boolean) = _uiState.update { it.copy(isAudioSheetExpanded = expanded) }

    fun setSurahReciter(key: String) {
        prefs.selectedReciterKey = key
        val st = _uiState.value.playerState
        if (!st.ayahMode && st.status != PlaybackStatus.IDLE) audioPlayerManager.playSurah(st.currentSurahNumber, key)
    }

    // ------------------------------------------------------------------
    // Tesbih
    // ------------------------------------------------------------------

    fun incrementDhikr() {
        val next = _uiState.value.currentDhikrCount + 1
        val target = _uiState.value.targetDhikrCount
        val day = UserPreferences.dayKey()
        val total = prefs.getDhikrForDay(day) + 1
        if (prefs.tasbihVibrationEnabled) triggerHaptic(isTargetReached = target > 0 && next % target == 0)
        prefs.tasbihCurrentCount = next
        prefs.setDhikrForDay(day, total)
        prefs.todayDhikrCompleted = total
        _uiState.update { it.copy(currentDhikrCount = next, todayDhikrCompletedTotal = total) }
    }

    fun decrementDhikr() {
        if (_uiState.value.currentDhikrCount > 0) {
            val prev = _uiState.value.currentDhikrCount - 1
            prefs.tasbihCurrentCount = prev
            _uiState.update { it.copy(currentDhikrCount = prev) }
            if (prefs.tasbihVibrationEnabled) triggerHaptic()
        }
    }

    fun confirmResetDhikr() {
        prefs.tasbihCurrentCount = 0
        _uiState.update { it.copy(currentDhikrCount = 0, isResetConfirmDialogOpen = false) }
        triggerHaptic()
        refreshDhikrStats()
    }

    fun setResetConfirmDialogOpen(open: Boolean) = _uiState.update { it.copy(isResetConfirmDialogOpen = open) }

    fun setTargetDhikrCount(target: Int) {
        prefs.tasbihTarget = target
        _uiState.update { it.copy(targetDhikrCount = target) }
    }

    fun selectPresetDhikr(title: String, arabic: String, meaning: String, target: Int = 33) {
        prefs.tasbihTarget = target
        prefs.tasbihCurrentCount = 0
        prefs.selectedDhikrTitle = title
        prefs.selectedDhikrArabic = arabic
        prefs.selectedDhikrMeaning = meaning
        _uiState.update {
            it.copy(
                selectedDhikrTitle = title, selectedDhikrArabic = arabic, selectedDhikrMeaning = meaning,
                targetDhikrCount = target, currentDhikrCount = 0
            )
        }
    }

    fun setAddCustomDhikrDialogOpen(open: Boolean) = _uiState.update { it.copy(isAddCustomDhikrDialogOpen = open) }

    fun addCustomDhikr(title: String, arabic: String, meaning: String, target: Int) {
        if (title.isBlank()) return
        prefs.addCustomDhikr(title.trim(), arabic.trim(), meaning.trim(), target.coerceIn(1, 100000))
        _uiState.update { it.copy(customDhikrs = prefs.getCustomDhikrs(), isAddCustomDhikrDialogOpen = false) }
        selectPresetDhikr(title.trim(), arabic.trim(), meaning.trim(), target.coerceIn(1, 100000))
    }

    fun removeCustomDhikr(title: String) {
        prefs.removeCustomDhikr(title)
        _uiState.update { it.copy(customDhikrs = prefs.getCustomDhikrs()) }
    }

    fun setTasbihVibration(enabled: Boolean) {
        prefs.tasbihVibrationEnabled = enabled
        _uiState.update { it.copy(tasbihVibration = enabled) }
    }

    // ------------------------------------------------------------------
    // Dualar & Esma
    // ------------------------------------------------------------------

    fun selectDuaCategory(categoryId: String) {
        _uiState.update {
            it.copy(selectedDuaCategory = categoryId, filteredDuas = DuaRepository.getDuasByCategory(categoryId), duaSearchQuery = "")
        }
    }

    fun searchDuas(query: String) {
        _uiState.update { it.copy(duaSearchQuery = query, filteredDuas = DuaRepository.searchDuas(query)) }
    }

    fun toggleFavoriteDua(duaId: String) {
        prefs.toggleFavoriteDua(duaId)
        _uiState.update { it.copy(favoriteDuas = prefs.getFavoriteDuas()) }
        triggerHaptic()
    }

    fun searchAsma(query: String) {
        _uiState.update { it.copy(asmaSearchQuery = query, filteredAsma = AsmaulHusnaRepository.search(query)) }
    }

    fun toggleFavoriteAsma(number: Int) {
        prefs.toggleFavoriteAsma(number)
        _uiState.update { it.copy(favoriteAsma = prefs.getFavoriteAsma()) }
        triggerHaptic()
    }

    // ------------------------------------------------------------------
    // Hatim & Zekât
    // ------------------------------------------------------------------

    fun toggleJuzCompleted(juz: Int) {
        val set = _uiState.value.completedJuzs.toMutableSet()
        if (!set.remove(juz)) { set.add(juz); triggerHaptic() }
        prefs.setHatimCompletedJuzs(set)
        _uiState.update { it.copy(completedJuzs = set) }
    }

    fun calculateZakat(goldGrams: Double, cashTry: Double, goodsTry: Double, debtsTry: Double, customGoldPrice: Double? = null) {
        val goldPricePerGram = (customGoldPrice ?: 3450.0).coerceAtLeast(1.0)
        val totalAssets = goldGrams * goldPricePerGram + cashTry + goodsTry
        val netWealth = (totalAssets - debtsTry).coerceAtLeast(0.0)
        val nisab = 80.18 * goldPricePerGram
        val isObligatory = netWealth >= nisab
        _uiState.update {
            it.copy(
                zakatResult = ZakatCalculationResult(
                    totalWealthTry = netWealth, goldGramPriceTry = goldPricePerGram, nisabThresholdTry = nisab,
                    isZakatObligatory = isObligatory, zakatAmountDueTry = if (isObligatory) netWealth * 0.025 else 0.0,
                    isCustomGoldPrice = customGoldPrice != null
                )
            )
        }
    }

    // ------------------------------------------------------------------
    // Görünüm, dil
    // ------------------------------------------------------------------

    /** Eski profil düğmesi: koyu <-> açık */
    fun toggleAppTheme() = setThemeMode(if (_uiState.value.themeMode == "light") "dark" else "light")

    fun setThemeMode(mode: String) {
        if (mode == "kiswa" && !requirePremium()) return
        prefs.themeMode = mode
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun setFontScale(scale: Float) {
        val v = scale.coerceIn(0.85f, 1.3f)
        prefs.fontScale = v
        _uiState.update { it.copy(fontScale = v) }
    }

    fun setLanguage(lang: String) {
        prefs.language = lang
        I18n.language = lang
        _uiState.update { it.copy(language = lang) }
        NotificationHelper.createChannels(app)
        // Meal dili değiştiği için açık okuyucuyu yenile
        viewModelScope.launch(Dispatchers.IO) {
            val st = _uiState.value
            val verses = when {
                st.readerMode == ReaderMode.JUZ && st.readerJuz > 0 -> QuranRepository.versesForJuz(st.readerJuz)
                st.selectedSurah != null -> QuranRepository.getVersesForSurah(st.selectedSurah.number)
                else -> emptyList()
            }
            val verse = runCatching { DailyContent.verseOfDay(app) }.getOrNull()
            _uiState.update { it.copy(currentSurahVerses = verses, verseOfDay = verse) }
        }
        rescheduleAlarms()
    }

    // ------------------------------------------------------------------
    // Bildirim ayarları
    // ------------------------------------------------------------------

    fun refreshNotificationSettings() {
        val map = PrayerAlarmScheduler.prayerTypes.associateWith { prefs.getNotificationPref(it) }
        _uiState.update {
            it.copy(
                notificationPrefs = map,
                preReminderMinutes = prefs.preReminderMinutes,
                ongoingCountdown = prefs.ongoingCountdownEnabled,
                adhanSoundName = prefs.adhanSoundName,
                fridayReminder = prefs.fridayReminderEnabled,
                kandilReminder = prefs.kandilReminderEnabled,
                dailyVerseNotification = prefs.dailyVerseNotificationEnabled,
                notificationsAllowed = NotificationHelper.canPost(app),
                exactAlarmAllowed = PrayerAlarmScheduler.canScheduleExact(app),
                prayerAdjustments = prefs.getPrayerAdjustments().toList(),
                hijriOffset = prefs.hijriOffset
            )
        }
    }

    fun setNotificationPref(type: PrayerType, pref: NotificationPreference) {
        prefs.setNotificationPref(type, pref)
        refreshNotificationSettings()
        rescheduleAlarms()
    }

    /** Tüm farz vakitler için aynı tercihi uygular */
    fun setAllNotificationPrefs(pref: NotificationPreference) {
        PrayerAlarmScheduler.prayerTypes.filter { it != PrayerType.GUNES }.forEach { prefs.setNotificationPref(it, pref) }
        refreshNotificationSettings()
        rescheduleAlarms()
    }

    fun setPreReminderMinutes(m: Int) {
        prefs.preReminderMinutes = m
        refreshNotificationSettings()
        rescheduleAlarms()
    }

    fun setOngoingCountdown(enabled: Boolean) {
        prefs.ongoingCountdownEnabled = enabled
        refreshNotificationSettings()
        rescheduleAlarms()
    }

    fun setFridayReminder(v: Boolean) { prefs.fridayReminderEnabled = v; refreshNotificationSettings(); rescheduleAlarms() }
    fun setKandilReminder(v: Boolean) { prefs.kandilReminderEnabled = v; refreshNotificationSettings(); rescheduleAlarms() }
    fun setDailyVerseNotification(v: Boolean) { prefs.dailyVerseNotificationEnabled = v; refreshNotificationSettings(); rescheduleAlarms() }

    fun setAdhanSound(uri: Uri?, name: String?) {
        if (uri != null) {
            try {
                app.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {}
        }
        prefs.adhanSoundUri = uri?.toString()
        prefs.adhanSoundName = name
        refreshNotificationSettings()
    }

    /** Seçili ezan sesini dener (10 sn sonra kendiliğinden durmaz; bildirimden durdurulur) */
    fun previewAdhan() {
        val i = Intent(app, com.eavisionlabs.nurvakti.notify.AdhanService::class.java)
            .putExtra(com.eavisionlabs.nurvakti.notify.AdhanService.EXTRA_TITLE, t("Ezan sesi deneniyor"))
            .putExtra(com.eavisionlabs.nurvakti.notify.AdhanService.EXTRA_TEXT, t("Durdurmak için dokunun"))
        try { androidx.core.content.ContextCompat.startForegroundService(app, i) } catch (_: Exception) {}
    }

    fun setPrayerAdjustment(index: Int, minutes: Int) {
        prefs.setPrayerAdjustment(index, minutes)
        PrayerAlarmScheduler.applyEngineSettings(app)
        refreshNotificationSettings()
        refreshPrayerTimes()
        _uiState.update { it.copy(monthlySchedule = emptyList()) }
        rescheduleAlarms()
    }

    fun setHijriOffset(days: Int) {
        prefs.hijriOffset = days
        PrayerAlarmScheduler.applyEngineSettings(app)
        refreshNotificationSettings()
        refreshPrayerTimes()
        viewModelScope.launch(Dispatchers.Default) {
            val d = ReligiousDaysProvider.getReligiousDays()
            val r = computeRamadanInfo(_uiState.value.selectedCity)
            _uiState.update { it.copy(religiousDays = d, ramadan = r, monthlySchedule = emptyList()) }
        }
        rescheduleAlarms()
    }

    fun rescheduleAlarms() {
        viewModelScope.launch(Dispatchers.Default) {
            try { PrayerAlarmScheduler.scheduleNext(app) } catch (_: Exception) {}
        }
    }

    fun onPermissionsChanged() {
        refreshNotificationSettings()
        rescheduleAlarms()
        CountdownNotifier.update(app)
    }

    // ------------------------------------------------------------------
    // Yedekleme
    // ------------------------------------------------------------------

    fun exportBackup(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val ok = runCatching {
                app.contentResolver.openOutputStream(uri, "wt")?.use { it.write(prefs.exportJson().toByteArray(Charsets.UTF_8)) }
                    ?: error("no stream")
            }.isSuccess
            showMessage(if (ok) t("Yedek kaydedildi.") else t("Yedek kaydedilemedi."))
        }
    }

    fun importBackup(uri: Uri, onRestored: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = runCatching {
                val text = app.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) } ?: error("no stream")
                prefs.importJson(text)
            }
            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    showMessage(t("Yedek geri yüklendi. Uygulama yenileniyor…"))
                    onRestored()
                } else {
                    showMessage(t("Bu dosya geri yüklenemedi.") + " " + (result.exceptionOrNull()?.message ?: ""))
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // Premium & Kaza
    // ------------------------------------------------------------------

    fun setPremiumSheetVisible(visible: Boolean) = _uiState.update { it.copy(isPremiumSheetVisible = visible) }

    /** Pro özelliği isteyen işlemlerden önce çağrılır; Pro değilse Pro ekranını açar. */
    private fun requirePremium(): Boolean {
        if (_uiState.value.isPremiumActive) return true
        showMessage(t("Bu özellik NurVakti Pro ile kullanılabilir."))
        navigateTo(AppScreen.PREMIUM)
        return false
    }

    fun purchase(activity: Activity, plan: String) {
        val id = when (plan) {
            "monthly" -> BillingManager.SUB_MONTHLY
            "lifetime" -> BillingManager.LIFETIME
            else -> BillingManager.SUB_YEARLY
        }
        billing.launchPurchase(activity, id)
    }

    fun restorePurchases() {
        billing.connect()
        billing.restorePurchases { active ->
            showMessage(if (active) t("Pro üyeliğiniz geri yüklendi.") else t("Bu hesapta etkin bir Pro satın alımı bulunamadı."))
        }
    }

    /** Eski API — artık doğrudan satın alma akışını başlatamaz; Pro ekranını açar. */
    fun activatePremium() {
        _uiState.update { it.copy(isPremiumSheetVisible = false) }
        navigateTo(AppScreen.PREMIUM)
    }

    fun updateKazaCount(type: String, delta: Int) {
        val s = _uiState.value
        when (type) {
            "sabah" -> (s.kazaSabah + delta).coerceAtLeast(0).also { prefs.kazaSabah = it; _uiState.update { u -> u.copy(kazaSabah = it) } }
            "ogle" -> (s.kazaOgle + delta).coerceAtLeast(0).also { prefs.kazaOgle = it; _uiState.update { u -> u.copy(kazaOgle = it) } }
            "ikindi" -> (s.kazaIkindi + delta).coerceAtLeast(0).also { prefs.kazaIkindi = it; _uiState.update { u -> u.copy(kazaIkindi = it) } }
            "aksam" -> (s.kazaAksam + delta).coerceAtLeast(0).also { prefs.kazaAksam = it; _uiState.update { u -> u.copy(kazaAksam = it) } }
            "yatsi" -> (s.kazaYatsi + delta).coerceAtLeast(0).also { prefs.kazaYatsi = it; _uiState.update { u -> u.copy(kazaYatsi = it) } }
            "vitir" -> (s.kazaVitir + delta).coerceAtLeast(0).also { prefs.kazaVitir = it; _uiState.update { u -> u.copy(kazaVitir = it) } }
        }
        triggerHaptic()
    }

    fun addBulkKazaDays(days: Int) {
        val s = _uiState.value
        prefs.kazaSabah = s.kazaSabah + days
        prefs.kazaOgle = s.kazaOgle + days
        prefs.kazaIkindi = s.kazaIkindi + days
        prefs.kazaAksam = s.kazaAksam + days
        prefs.kazaYatsi = s.kazaYatsi + days
        prefs.kazaVitir = s.kazaVitir + days
        _uiState.update {
            it.copy(
                kazaSabah = prefs.kazaSabah, kazaOgle = prefs.kazaOgle, kazaIkindi = prefs.kazaIkindi,
                kazaAksam = prefs.kazaAksam, kazaYatsi = prefs.kazaYatsi, kazaVitir = prefs.kazaVitir
            )
        }
        triggerHaptic()
    }

    private fun triggerHaptic(isTargetReached: Boolean = false) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (isTargetReached) vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 150), -1))
                else vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
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
        billing.end()
    }
}
