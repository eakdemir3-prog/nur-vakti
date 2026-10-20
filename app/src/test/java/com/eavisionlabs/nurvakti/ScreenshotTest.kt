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

    private fun shot(name: String, theme: String = "dark", rtl: Boolean = false, content: @Composable () -> Unit) {
        compose.setContent {
            MyApplicationTheme(themeMode = theme) {
                CompositionLocalProvider(LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr) {
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
}
