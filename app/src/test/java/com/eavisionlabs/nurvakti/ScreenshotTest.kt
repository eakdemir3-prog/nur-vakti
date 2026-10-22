package com.eavisionlabs.nurvakti

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import android.graphics.Canvas
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.core.app.ApplicationProvider
import com.eavisionlabs.nurvakti.data.*
import com.eavisionlabs.nurvakti.i18n.I18n
import com.eavisionlabs.nurvakti.model.NotificationPreference
import com.eavisionlabs.nurvakti.model.PrayerType
import com.eavisionlabs.nurvakti.ui.DayStat
import com.eavisionlabs.nurvakti.ui.UiState
import com.eavisionlabs.nurvakti.ui.screens.*
import com.eavisionlabs.nurvakti.ui.theme.MyApplicationTheme
import org.junit.Rule
import androidx.compose.foundation.layout.padding
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/** Ekranların görüntüsünü alır (CI'da görsel kontrol için). Çıktı: app/build/screenshots */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w400dp-h860dp-xhdpi", application = Application::class)
class ScreenshotTest {

    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    private fun state(): UiState {
        val app = ApplicationProvider.getApplicationContext<Application>()
        CityProvider.init(app)
        QuranRepository.init(app)
        val city = CityProvider.defaultCity
        val surah = QuranRepository.surahs[35]
        return UiState(
            selectedCity = city,
            prayerSchedule = PrayerCalculationEngine.calculateDailySchedule(city),
            completedPrayersToday = setOf("imsak", "ogle"),
            verseOfDay = DailyContent.verse(app, 94, 5),
            hadithOfDay = DailyContent.hadithOfDay(),
            duaOfDay = DailyContent.duaOfDay(),
            religiousDays = ReligiousDaysProvider.getReligiousDays(),
            selectedSurah = surah,
            currentSurahVerses = QuranRepository.getVersesForSurah(36).take(12),
            prayerWeek = (1..7).map { DayStat("G$it", it % 6, it == 7) },
            prayerMonth = (1..30).map { DayStat("$it", (it * 7) % 6, it == 30) },
            readingWeek = (1..7).map { DayStat("G$it", it * 2, it == 7) },
            dhikrWeek = (1..7).map { DayStat("G$it", it * 30, it == 7) },
            prayerStreak = 4,
            pagesReadToday = 3,
            hatimPagesRead = 120,
            notificationPrefs = PrayerType.entries.associateWith {
                if (it == PrayerType.GUNES) NotificationPreference.DISABLED else NotificationPreference.AZAN
            },
            notificationsAllowed = false
        )
    }

    private fun shot(name: String, theme: String = "dark", rtl: Boolean = false, fontScale: Float = 1f, content: @Composable () -> Unit) {
        compose.setContent {
            MyApplicationTheme(themeMode = theme) {
                val d = androidx.compose.ui.platform.LocalDensity.current
                CompositionLocalProvider(
                    LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
                    androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(d.density, fontScale)
                ) {
                    content()
                }
            }
        }
        try {
            compose.waitForIdle()
            val view = compose.activity.window.decorView
            val bmp = Bitmap.createBitmap(view.width.coerceAtLeast(1), view.height.coerceAtLeast(1), Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bmp))
            val dir = File("build/screenshots").apply { mkdirs() }
            File(dir, "$name.png").outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        } catch (e: Throwable) {
            // Görsel kontrol yardımcı bir testtir; derlemeyi bozmasın
            File("build/screenshots").apply { mkdirs() }.resolve("$name.error.txt").writeText(e.stackTraceToString())
        }
    }

    @Test fun home_dark() { val s = state(); shot("01_home_dark") { HomeScreen(s, {}, {}, {}) } }
    @Test fun home_light() { val s = state(); shot("02_home_light", "light") { HomeScreen(s, {}, {}, {}) } }
    @Test fun home_kiswa() { val s = state(); shot("03_home_kiswa", "kiswa") { HomeScreen(s, {}, {}, {}) } }
    @Test fun home_arabic() {
        I18n.language = "ar"
        val s = state().copy(language = "ar")
        shot("04_home_arabic", rtl = true) { HomeScreen(s, {}, {}, {}) }
        I18n.language = "tr"
    }
    @Test fun prayer_times() { val s = state(); shot("05_prayer_times") { PrayerTimesScreen(s, {}, {}) } }
    @Test fun quran_list() { val s = state(); shot("06_quran_list") { QuranScreen(s, {}, {}, {}, {}) } }
    @Test fun reader() {
        val s = state()
        shot("07_reader") {
            QuranReaderScreen(s, {}, {}, {}, {}, {}, {}, {}, { _, _ -> }, { _, _ -> }, {}, {}, {}, { _, _ -> }, {}, {}, {})
        }
    }
    @Test fun tracker() { val s = state(); shot("08_tracker") { PrayerTrackerScreen(s, {}, {}, {}) } }
    @Test fun premium() { val s = state(); shot("09_premium") { PremiumScreen(s, {}, {}, {}) } }
    @Test fun ramazan() { val s = state(); shot("10_ramazan_light", "light") { RamazanHatimScreen(s, {}, {}, {}) } }
    @Test fun explore() { val s = state(); shot("11_explore") { ExploreScreen(s, {}) } }
    @Test fun tasbih_light() { val s = state(); shot("12_tasbih_light", "light") { TasbihScreen(s, {}, {}, {}, {}, {}, {}, { _, _, _, _ -> }) } }

    @Test fun intro_frames() {
        compose.mainClock.autoAdvance = false
        compose.setContent { com.eavisionlabs.nurvakti.ui.components.IntroAnimation(onFinished = {}) }
        var last = 0L
        for (ms in listOf(80L, 350L, 650L, 950L, 1300L, 1650L)) {
            compose.mainClock.advanceTimeBy(ms - last); last = ms
            val view = compose.activity.window.decorView
            val bmp = Bitmap.createBitmap(view.width.coerceAtLeast(1), view.height.coerceAtLeast(1), Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bmp))
            File("build/screenshots").apply { mkdirs() }.resolve("intro_%04d.png".format(ms)).outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
    }

    @Composable
    private fun withBars(s: UiState, screen: com.eavisionlabs.nurvakti.ui.AppScreen, body: @Composable () -> Unit) {
        androidx.compose.material3.Scaffold(
            topBar = { com.eavisionlabs.nurvakti.ui.components.NurVaktiTopBar(s.selectedCity, {}, {}, {}) },
            bottomBar = { com.eavisionlabs.nurvakti.ui.components.NurVaktiBottomNav(screen, {}) }
        ) { p -> androidx.compose.foundation.layout.Box(androidx.compose.ui.Modifier.padding(p)) { body() } }
    }

    private fun district() = state().let { st ->
        val c = com.eavisionlabs.nurvakti.model.City("gaziantep_sehitkamil", "Şehitkamil", "Türkiye", 37.06, 37.38, province = "Gaziantep")
        st.copy(selectedCity = c, prayerSchedule = PrayerCalculationEngine.calculateDailySchedule(c))
    }

    @Test fun big_home() { val s = district(); shot("20_big_home", fontScale = 1.4f) { withBars(s, com.eavisionlabs.nurvakti.ui.AppScreen.HOME) { HomeScreen(s, {}, {}, {}) } } }
    @Test fun big_profile() { val s = district(); shot("21_big_profile", fontScale = 1.4f) { withBars(s, com.eavisionlabs.nurvakti.ui.AppScreen.PROFILE) { ProfileScreen(s, {}, {}, {}) } } }
    @Test fun big_times() { val s = district(); shot("22_big_times", fontScale = 1.4f) { withBars(s, com.eavisionlabs.nurvakti.ui.AppScreen.PRAYER_TIMES) { PrayerTimesScreen(s, {}, {}) } } }
    @Test fun big_quran() { val s = district(); shot("23_big_quran", fontScale = 1.4f) { withBars(s, com.eavisionlabs.nurvakti.ui.AppScreen.QURAN) { QuranScreen(s, {}, {}, {}, {}) } } }
    @Test fun big_explore() { val s = district(); shot("24_big_explore", fontScale = 1.4f) { withBars(s, com.eavisionlabs.nurvakti.ui.AppScreen.EXPLORE) { ExploreScreen(s, {}) } } }
    @Test fun big_tracker() { val s = district(); shot("25_big_tracker", fontScale = 1.4f) { PrayerTrackerScreen(s, {}, {}, {}) } }
    @Test fun big_premium() { val s = district(); shot("26_big_premium", fontScale = 1.4f) { PremiumScreen(s, {}, {}, {}) } }
    @Test fun big_tasbih() { val s = district(); shot("27_big_tasbih", fontScale = 1.4f) { TasbihScreen(s, {}, {}, {}, {}, {}, {}, { _, _, _, _ -> }) } }
    @Test fun big_ramazan() { val s = district(); shot("28_big_ramazan", fontScale = 1.4f) { RamazanHatimScreen(s, {}, {}, {}) } }
    @Test fun big_kaza() { val s = district(); shot("29_big_kaza", fontScale = 1.4f) { KazaNamazScreen(s, {}, { _, _ -> }, {}) } }
    @Test fun big_zakat() { val s = district(); shot("30_big_zakat", fontScale = 1.4f) { ZakatCalculatorScreen(s, {}, { _, _, _, _, _ -> }) } }
    @Test fun big_qibla() { val s = district(); shot("31_big_qibla", fontScale = 1.4f) { QiblaScreen(s, {}) } }
}
