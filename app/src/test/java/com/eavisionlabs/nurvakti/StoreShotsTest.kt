package com.eavisionlabs.nurvakti

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import com.eavisionlabs.nurvakti.data.*
import com.eavisionlabs.nurvakti.i18n.I18n
import com.eavisionlabs.nurvakti.model.NotificationPreference
import com.eavisionlabs.nurvakti.model.PrayerType
import com.eavisionlabs.nurvakti.ui.AppScreen
import com.eavisionlabs.nurvakti.ui.DayStat
import com.eavisionlabs.nurvakti.ui.UiState
import com.eavisionlabs.nurvakti.ui.components.NurVaktiBottomNav
import com.eavisionlabs.nurvakti.ui.components.NurVaktiTopBar
import com.eavisionlabs.nurvakti.ui.screens.*
import com.eavisionlabs.nurvakti.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/** Play Store için gerçek uygulama ekranları (TR ve EN). Çıktı: app/build/screenshots/store */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi", application = Application::class)
class StoreShotsTest {

    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    private fun state(): UiState {
        val app = ApplicationProvider.getApplicationContext<Application>()
        CityProvider.init(app)
        QuranRepository.init(app)
        val city = CityProvider.defaultCity
        return UiState(
            selectedCity = city,
            prayerSchedule = PrayerCalculationEngine.calculateDailySchedule(city),
            completedPrayersToday = setOf("imsak", "ogle", "ikindi"),
            verseOfDay = DailyContent.verse(app, 13, 28),
            hadithOfDay = DailyContent.hadithOfDay(),
            duaOfDay = DailyContent.duaOfDay(),
            religiousDays = ReligiousDaysProvider.getReligiousDays(),
            selectedSurah = QuranRepository.surahs[0],
            currentSurahVerses = QuranRepository.getVersesForSurah(1),
            prayerWeek = listOf(5, 5, 4, 5, 5, 5, 3).mapIndexed { i, v -> DayStat("G${i + 1}", v, i == 6) },
            prayerMonth = (1..30).map { DayStat("$it", listOf(5, 4, 5, 5, 3, 5)[it % 6], it == 30) },
            readingWeek = listOf(4, 6, 3, 8, 5, 7, 6).mapIndexed { i, v -> DayStat("G${i + 1}", v, i == 6) },
            dhikrWeek = listOf(99, 330, 165, 99, 500, 264, 198).mapIndexed { i, v -> DayStat("G${i + 1}", v, i == 6) },
            prayerStreak = 12,
            pagesReadToday = 6,
            hatimPagesRead = 312,
            currentDhikrCount = 27,
            isPremiumActive = true,
            notificationPrefs = PrayerType.entries.associateWith {
                if (it == PrayerType.GUNES) NotificationPreference.DISABLED else NotificationPreference.AZAN
            },
            notificationsAllowed = true
        )
    }

    private fun bars(s: UiState, screen: AppScreen, body: @Composable () -> Unit) {
        androidx.compose.material3.Scaffold(
            topBar = { NurVaktiTopBar(s.selectedCity, {}, {}, {}) },
            bottomBar = { NurVaktiBottomNav(screen, {}) }
        ) { p -> Box(Modifier.padding(p)) { body() } }
    }

    private fun shot(lang: String, name: String, theme: String = "dark", content: @Composable () -> Unit) {
        I18n.language = lang
        compose.setContent { MyApplicationTheme(themeMode = theme) { content() } }
        try {
            compose.waitForIdle()
            val view = compose.activity.window.decorView
            val bmp = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bmp))
            val dir = File("build/screenshots/store/$lang").apply { mkdirs() }
            File(dir, "$name.png").outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        } catch (e: Throwable) {
            File("build/screenshots/store").apply { mkdirs() }.resolve("$lang-$name.error.txt").writeText(e.stackTraceToString())
        } finally {
            I18n.language = "tr"
        }
    }

    private fun all(name: String, theme: String = "dark", content: @Composable () -> Unit) {
        shot("tr", name, theme, content)
    }

    // Her dil için ayrı test: setContent bir testte bir kez çağrılabilir
    @Test fun tr_home() { val s = state(); shot("tr", "1_home") { bars(s, AppScreen.HOME) { HomeScreen(s, {}, {}, {}) } } }
    @Test fun en_home() { val s = state(); shot("en", "1_home") { bars(s, AppScreen.HOME) { HomeScreen(s, {}, {}, {}) } } }
    @Test fun tr_times() { val s = state(); shot("tr", "2_times") { bars(s, AppScreen.PRAYER_TIMES) { PrayerTimesScreen(s, {}, {}) } } }
    @Test fun en_times() { val s = state(); shot("en", "2_times") { bars(s, AppScreen.PRAYER_TIMES) { PrayerTimesScreen(s, {}, {}) } } }
    @Test fun tr_reader() { val s = state(); shot("tr", "3_reader") { QuranReaderScreen(s, {}, {}, {}, {}, {}, {}, {}, { _, _ -> }, { _, _ -> }, {}, {}, {}, { _, _ -> }, {}, {}, {}) } }
    @Test fun en_reader() { val s = state(); shot("en", "3_reader") { QuranReaderScreen(s, {}, {}, {}, {}, {}, {}, {}, { _, _ -> }, { _, _ -> }, {}, {}, {}, { _, _ -> }, {}, {}, {}) } }
    @Test fun tr_topics() { shot("tr", "4_topics") { TopicsScreen({}, { _, _ -> }, {}) } }
    @Test fun en_topics() { shot("en", "4_topics") { TopicsScreen({}, { _, _ -> }, {}) } }
    @Test fun tr_greetings() { shot("tr", "5_greetings") { GreetingsScreen({}, {}, {}) } }
    @Test fun en_greetings() { shot("en", "5_greetings") { GreetingsScreen({}, {}, {}) } }
    @Test fun tr_tasbih() { val s = state(); shot("tr", "6_tasbih") { TasbihScreen(s, {}, {}, {}, {}, {}, {}, { _, _, _, _ -> }) } }
    @Test fun en_tasbih() { val s = state(); shot("en", "6_tasbih") { TasbihScreen(s, {}, {}, {}, {}, {}, {}, { _, _, _, _ -> }) } }
    @Test fun tr_tracker() { val s = state(); shot("tr", "7_tracker") { PrayerTrackerScreen(s, {}, {}, {}) } }
    @Test fun en_tracker() { val s = state(); shot("en", "7_tracker") { PrayerTrackerScreen(s, {}, {}, {}) } }
    @Test fun tr_theme_ottoman() { val s = state(); shot("tr", "8a_ottoman", "ottoman") { bars(s, AppScreen.HOME) { HomeScreen(s, {}, {}, {}) } } }
    @Test fun en_theme_ottoman() { val s = state(); shot("en", "8a_ottoman", "ottoman") { bars(s, AppScreen.HOME) { HomeScreen(s, {}, {}, {}) } } }
    @Test fun tr_theme_night() { val s = state(); shot("tr", "8b_night", "night") { bars(s, AppScreen.HOME) { HomeScreen(s, {}, {}, {}) } } }
    @Test fun en_theme_night() { val s = state(); shot("en", "8b_night", "night") { bars(s, AppScreen.HOME) { HomeScreen(s, {}, {}, {}) } } }
    @Test fun tr_theme_kiswa() { val s = state(); shot("tr", "8c_kiswa", "kiswa") { bars(s, AppScreen.HOME) { HomeScreen(s, {}, {}, {}) } } }
    @Test fun en_theme_kiswa() { val s = state(); shot("en", "8c_kiswa", "kiswa") { bars(s, AppScreen.HOME) { HomeScreen(s, {}, {}, {}) } } }
    @Test fun tr_reader_light() { val s = state(); shot("tr", "3b_reader_light", "light") { QuranReaderScreen(s, {}, {}, {}, {}, {}, {}, {}, { _, _ -> }, { _, _ -> }, {}, {}, {}, { _, _ -> }, {}, {}, {}) } }
    @Test fun en_reader_light() { val s = state(); shot("en", "3b_reader_light", "light") { QuranReaderScreen(s, {}, {}, {}, {}, {}, {}, {}, { _, _ -> }, { _, _ -> }, {}, {}, {}, { _, _ -> }, {}, {}, {}) } }

    @Test fun cards() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        QuranRepository.init(app)
        val dir = File("build/screenshots/store").apply { mkdirs() }
        val r = com.eavisionlabs.nurvakti.share.ShareCardRenderer
        val msg = DailyContent.greetingsFor("cuma")[1]
        r.renderCard(app, r.styles[0], "Hayırlı Cumalar", msg).let { b -> File(dir, "card_tr.png").outputStream().use { b.compress(Bitmap.CompressFormat.PNG, 100, it) } }
        r.renderCard(app, r.styles[2], "Jumu'ah Mubarak", "May Allah fill your Friday with peace, mercy and countless blessings. Remember us in your prayers.")
            .let { b -> File(dir, "card_en.png").outputStream().use { b.compress(Bitmap.CompressFormat.PNG, 100, it) } }
    }
}
