package com.eavisionlabs.nurvakti.data

import android.content.Context
import android.content.SharedPreferences
import com.eavisionlabs.nurvakti.model.City
import com.eavisionlabs.nurvakti.model.NotificationPreference
import com.eavisionlabs.nurvakti.model.PrayerType
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class UserPreferences(context: Context) {

    companion object {
        const val PREFS_NAME = "nurvakti_prefs"
        private val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
        fun dayKey(cal: Calendar = Calendar.getInstance()): String = synchronized(dayFormat) { dayFormat.format(cal.time) }
        fun dayKey(millis: Long): String = synchronized(dayFormat) { dayFormat.format(Date(millis)) }
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Onboarding
    var isOnboardingCompleted: Boolean
        get() = prefs.getBoolean("onboarding_completed", false)
        set(value) = prefs.edit().putBoolean("onboarding_completed", value).apply()

    // City Selection
    var selectedCityId: String
        get() = prefs.getString("selected_city_id", "istanbul") ?: "istanbul"
        set(value) = prefs.edit().putString("selected_city_id", value).apply()

    var selectedCityName: String
        get() = prefs.getString("selected_city_name", "İstanbul") ?: "İstanbul"
        set(value) = prefs.edit().putString("selected_city_name", value).apply()

    var selectedCityLat: Double
        get() = prefs.getFloat("selected_city_lat", 41.0082f).toDouble()
        set(value) = prefs.edit().putFloat("selected_city_lat", value.toFloat()).apply()

    var selectedCityLng: Double
        get() = prefs.getFloat("selected_city_lng", 28.9784f).toDouble()
        set(value) = prefs.edit().putFloat("selected_city_lng", value.toFloat()).apply()

    // Quran Reading Settings
    var arabicFontSize: Int
        get() = prefs.getInt("arabic_font_size", 28)
        set(value) = prefs.edit().putInt("arabic_font_size", value).apply()

    var mealFontSize: Int
        get() = prefs.getInt("meal_font_size", 16)
        set(value) = prefs.edit().putInt("meal_font_size", value).apply()

    var readingTheme: String // "DARK", "LIGHT", "SEPIA"
        get() = prefs.getString("reading_theme", "DARK") ?: "DARK"
        set(value) = prefs.edit().putString("reading_theme", value).apply()

    var showMeal: Boolean
        get() = prefs.getBoolean("show_meal", true)
        set(value) = prefs.edit().putBoolean("show_meal", value).apply()

    var showArabic: Boolean
        get() = prefs.getBoolean("show_arabic", true)
        set(value) = prefs.edit().putBoolean("show_arabic", value).apply()

    // Last Read Position
    var lastReadSurahNumber: Int
        get() = prefs.getInt("last_read_surah", 1)
        set(value) = prefs.edit().putInt("last_read_surah", value).apply()

    var lastReadAyahNumber: Int
        get() = prefs.getInt("last_read_ayah", 1)
        set(value) = prefs.edit().putInt("last_read_ayah", value).apply()

    // Daily Goals
    var dailyQuranGoalPages: Int
        get() = prefs.getInt("daily_quran_goal", 5)
        set(value) = prefs.edit().putInt("daily_quran_goal", value).apply()

    var todayPagesRead: Int
        get() = prefs.getInt("today_pages_read", 0)
        set(value) = prefs.edit().putInt("today_pages_read", value).apply()

    var dailyDhikrGoal: Int
        get() = prefs.getInt("daily_dhikr_goal", 100)
        set(value) = prefs.edit().putInt("daily_dhikr_goal", value).apply()

    var todayDhikrCompleted: Int
        get() = prefs.getInt("today_dhikr_completed", 0)
        set(value) = prefs.edit().putInt("today_dhikr_completed", value).apply()

    // Daily Prayers Performed Today (e.g. "imsak,ogle,ikindi")
    fun getCompletedPrayersToday(): Set<String> {
        return prefs.getStringSet("prayers_today", emptySet()) ?: emptySet()
    }

    fun setCompletedPrayersToday(prayers: Set<String>) {
        prefs.edit().putStringSet("prayers_today", prayers).apply()
    }

    // Hatim Completed Juzs
    fun getHatimCompletedJuzs(): Set<Int> {
        val raw = prefs.getStringSet("hatim_completed_juzs", emptySet()) ?: emptySet()
        return raw.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun setHatimCompletedJuzs(juzs: Set<Int>) {
        val raw = juzs.map { it.toString() }.toSet()
        prefs.edit().putStringSet("hatim_completed_juzs", raw).apply()
    }

    // Tasbih
    var tasbihCurrentCount: Int
        get() = prefs.getInt("tasbih_count", 0)
        set(value) = prefs.edit().putInt("tasbih_count", value).apply()

    var tasbihTarget: Int
        get() = prefs.getInt("tasbih_target", 33)
        set(value) = prefs.edit().putInt("tasbih_target", value).apply()

    var tasbihVibrationEnabled: Boolean
        get() = prefs.getBoolean("tasbih_vibration", true)
        set(value) = prefs.edit().putBoolean("tasbih_vibration", value).apply()

    var tasbihSoundEnabled: Boolean
        get() = prefs.getBoolean("tasbih_sound", false)
        set(value) = prefs.edit().putBoolean("tasbih_sound", value).apply()

    // Audio Reciter
    var selectedReciterKey: String
        get() = prefs.getString("selected_reciter", "alafasy") ?: "alafasy"
        set(value) = prefs.edit().putString("selected_reciter", value).apply()

    // Favorites
    fun getFavoriteSurahs(): Set<Int> {
        val set = prefs.getStringSet("favorite_surahs", setOf("1", "36", "67", "112")) ?: emptySet()
        return set.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun toggleFavoriteSurah(surahNumber: Int) {
        val current = getFavoriteSurahs().toMutableSet()
        if (current.contains(surahNumber)) {
            current.remove(surahNumber)
        } else {
            current.add(surahNumber)
        }
        prefs.edit().putStringSet("favorite_surahs", current.map { it.toString() }.toSet()).apply()
    }

    fun getFavoriteDuas(): Set<String> {
        return prefs.getStringSet("favorite_duas", setOf("s1", "si1", "ku1")) ?: emptySet()
    }

    fun toggleFavoriteDua(duaId: String) {
        val current = getFavoriteDuas().toMutableSet()
        if (current.contains(duaId)) {
            current.remove(duaId)
        } else {
            current.add(duaId)
        }
        prefs.edit().putStringSet("favorite_duas", current).apply()
    }

    fun getFavoriteAsma(): Set<Int> {
        val set = prefs.getStringSet("favorite_asma", setOf("1", "2", "3", "31", "48")) ?: emptySet()
        return set.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun toggleFavoriteAsma(number: Int) {
        val current = getFavoriteAsma().toMutableSet()
        if (current.contains(number)) {
            current.remove(number)
        } else {
            current.add(number)
        }
        prefs.edit().putStringSet("favorite_asma", current.map { it.toString() }.toSet()).apply()
    }

    // Premium
    var isPremiumActive: Boolean
        get() = prefs.getBoolean("is_premium_active", false)
        set(value) = prefs.edit().putBoolean("is_premium_active", value).apply()

    // Kaza Namazı Counts (Defaults to 0 for authentic new user tracking)
    var kazaSabah: Int
        get() = prefs.getInt("kaza_sabah", 0)
        set(value) = prefs.edit().putInt("kaza_sabah", value.coerceAtLeast(0)).apply()

    var kazaOgle: Int
        get() = prefs.getInt("kaza_ogle", 0)
        set(value) = prefs.edit().putInt("kaza_ogle", value.coerceAtLeast(0)).apply()

    var kazaIkindi: Int
        get() = prefs.getInt("kaza_ikindi", 0)
        set(value) = prefs.edit().putInt("kaza_ikindi", value.coerceAtLeast(0)).apply()

    var kazaAksam: Int
        get() = prefs.getInt("kaza_aksam", 0)
        set(value) = prefs.edit().putInt("kaza_aksam", value.coerceAtLeast(0)).apply()

    var kazaYatsi: Int
        get() = prefs.getInt("kaza_yatsi", 0)
        set(value) = prefs.edit().putInt("kaza_yatsi", value.coerceAtLeast(0)).apply()

    var kazaVitir: Int
        get() = prefs.getInt("kaza_vitir", 0)
        set(value) = prefs.edit().putInt("kaza_vitir", value.coerceAtLeast(0)).apply()

    // ------------------------------------------------------------------
    // Seçili konum (GPS ile bulunan konum dahil tam olarak saklanır)
    // ------------------------------------------------------------------
    fun saveCity(city: City) {
        val o = JSONObject()
            .put("id", city.id).put("name", city.name).put("country", city.country)
            .put("lat", city.latitude).put("lng", city.longitude)
            .put("tz", city.timeZoneId).put("province", city.province)
        prefs.edit()
            .putString("selected_city_json", o.toString())
            .putString("selected_city_id", city.id)
            .apply()
    }

    fun loadCity(): City? {
        val raw = prefs.getString("selected_city_json", null) ?: return null
        return try {
            val o = JSONObject(raw)
            City(
                id = o.getString("id"), name = o.getString("name"), country = o.optString("country", "Türkiye"),
                latitude = o.getDouble("lat"), longitude = o.getDouble("lng"),
                timeZoneId = o.optString("tz", "Europe/Istanbul"), province = o.optString("province", "")
            )
        } catch (_: Exception) { null }
    }

    // ------------------------------------------------------------------
    // Görünüm
    // ------------------------------------------------------------------
    /** "system", "dark", "light" */
    var themeMode: String
        get() = prefs.getString("theme_mode", "dark") ?: "dark"
        set(value) = prefs.edit().putString("theme_mode", value).apply()

    /** Genel yazı boyutu çarpanı (0.85 .. 1.3) */
    var fontScale: Float
        get() = prefs.getFloat("font_scale", 1.0f)
        set(value) = prefs.edit().putFloat("font_scale", value).apply()

    /** "tr", "en", "ar" */
    var language: String
        get() = prefs.getString("app_language", "tr") ?: "tr"
        set(value) = prefs.edit().putString("app_language", value).apply()

    // ------------------------------------------------------------------
    // Bildirimler
    // ------------------------------------------------------------------
    fun getNotificationPref(type: PrayerType): NotificationPreference {
        val default = if (type == PrayerType.GUNES) NotificationPreference.DISABLED else NotificationPreference.NOTIFICATION_SOUND
        val raw = prefs.getString("notif_${type.name}", null) ?: return default
        return runCatching { NotificationPreference.valueOf(raw) }.getOrDefault(default)
    }

    fun setNotificationPref(type: PrayerType, pref: NotificationPreference) =
        prefs.edit().putString("notif_${type.name}", pref.name).apply()

    /** Vakitten kaç dakika önce hatırlatılsın (0 = kapalı) */
    var preReminderMinutes: Int
        get() = prefs.getInt("pre_reminder_minutes", 15)
        set(value) = prefs.edit().putInt("pre_reminder_minutes", value).apply()

    var ongoingCountdownEnabled: Boolean
        get() = prefs.getBoolean("ongoing_countdown", false)
        set(value) = prefs.edit().putBoolean("ongoing_countdown", value).apply()

    /** Kullanıcının seçtiği ezan ses dosyası (content:// URI). Boşsa cihazın alarm sesi çalınır. */
    var adhanSoundUri: String?
        get() = prefs.getString("adhan_sound_uri", null)
        set(value) = prefs.edit().putString("adhan_sound_uri", value).apply()

    var adhanSoundName: String?
        get() = prefs.getString("adhan_sound_name", null)
        set(value) = prefs.edit().putString("adhan_sound_name", value).apply()

    var fridayReminderEnabled: Boolean
        get() = prefs.getBoolean("friday_reminder", true)
        set(value) = prefs.edit().putBoolean("friday_reminder", value).apply()

    var kandilReminderEnabled: Boolean
        get() = prefs.getBoolean("kandil_reminder", true)
        set(value) = prefs.edit().putBoolean("kandil_reminder", value).apply()

    var dailyVerseNotificationEnabled: Boolean
        get() = prefs.getBoolean("daily_verse_notif", true)
        set(value) = prefs.edit().putBoolean("daily_verse_notif", value).apply()

    var notificationPermissionAsked: Boolean
        get() = prefs.getBoolean("notif_perm_asked", false)
        set(value) = prefs.edit().putBoolean("notif_perm_asked", value).apply()

    // ------------------------------------------------------------------
    // Vakit düzeltmeleri
    // ------------------------------------------------------------------
    fun getPrayerAdjustments(): IntArray =
        IntArray(6) { i -> prefs.getInt("adjust_$i", 0) }

    fun setPrayerAdjustment(index: Int, minutes: Int) =
        prefs.edit().putInt("adjust_$index", minutes.coerceIn(-30, 30)).apply()

    var hijriOffset: Int
        get() = prefs.getInt("hijri_offset", 0)
        set(value) = prefs.edit().putInt("hijri_offset", value.coerceIn(-2, 2)).apply()

    // ------------------------------------------------------------------
    // Namaz takibi (gün gün)
    // ------------------------------------------------------------------
    fun getPrayersForDay(day: String): Set<String> =
        prefs.getStringSet("prayed_$day", emptySet())?.toSet() ?: emptySet()

    fun setPrayersForDay(day: String, set: Set<String>) {
        prefs.edit().putStringSet("prayed_$day", set).apply()
    }

    /** Son [days] günün kayıtları: tarih -> kılınan vakitler (bugün dahil, eskiden yeniye) */
    fun prayerHistory(days: Int): List<Pair<Calendar, Set<String>>> {
        val list = mutableListOf<Pair<Calendar, Set<String>>>()
        for (i in days - 1 downTo 0) {
            val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -i) }
            list += c to getPrayersForDay(dayKey(c))
        }
        return list
    }

    // ------------------------------------------------------------------
    // Günlük sayaçların sıfırlanması
    // ------------------------------------------------------------------
    var lastDailyResetDay: String
        get() = prefs.getString("last_daily_reset", "") ?: ""
        set(value) = prefs.edit().putString("last_daily_reset", value).apply()

    // ------------------------------------------------------------------
    // Zikir geçmişi ve özel zikirler
    // ------------------------------------------------------------------
    fun getDhikrForDay(day: String): Int = prefs.getInt("dhikr_$day", 0)
    fun setDhikrForDay(day: String, count: Int) = prefs.edit().putInt("dhikr_$day", count).apply()

    fun dhikrHistory(days: Int): List<Pair<Calendar, Int>> = (days - 1 downTo 0).map { i ->
        val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -i) }
        c to getDhikrForDay(dayKey(c))
    }

    /** Özel zikirler: [başlık, arapça, anlam, hedef] */
    fun getCustomDhikrs(): List<List<String>> {
        val raw = prefs.getString("custom_dhikrs", "[]") ?: "[]"
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                listOf(o.getString("t"), o.optString("a"), o.optString("m"), o.optInt("g", 33).toString())
            }
        } catch (_: Exception) { emptyList() }
    }

    fun addCustomDhikr(title: String, arabic: String, meaning: String, target: Int) {
        val arr = try { JSONArray(prefs.getString("custom_dhikrs", "[]")) } catch (_: Exception) { JSONArray() }
        arr.put(JSONObject().put("t", title).put("a", arabic).put("m", meaning).put("g", target))
        prefs.edit().putString("custom_dhikrs", arr.toString()).apply()
    }

    fun removeCustomDhikr(title: String) {
        val arr = try { JSONArray(prefs.getString("custom_dhikrs", "[]")) } catch (_: Exception) { JSONArray() }
        val out = JSONArray()
        for (i in 0 until arr.length()) if (arr.getJSONObject(i).getString("t") != title) out.put(arr.getJSONObject(i))
        prefs.edit().putString("custom_dhikrs", out.toString()).apply()
    }

    var selectedDhikrTitle: String
        get() = prefs.getString("dhikr_title", "Sübhanallah") ?: "Sübhanallah"
        set(value) = prefs.edit().putString("dhikr_title", value).apply()
    var selectedDhikrArabic: String
        get() = prefs.getString("dhikr_arabic", "سُبْحَانَ اللَّهِ") ?: ""
        set(value) = prefs.edit().putString("dhikr_arabic", value).apply()
    var selectedDhikrMeaning: String
        get() = prefs.getString("dhikr_meaning", "Allah her türlü eksiklikten münezzehtir.") ?: ""
        set(value) = prefs.edit().putString("dhikr_meaning", value).apply()

    // ------------------------------------------------------------------
    // Kur'an: yer imleri, okuma takibi
    // ------------------------------------------------------------------
    /** "sure:ayet" biçiminde */
    fun getBookmarks(): Set<String> = prefs.getStringSet("quran_bookmarks", emptySet())?.toSet() ?: emptySet()

    fun toggleBookmark(surah: Int, ayah: Int): Set<String> {
        val key = "$surah:$ayah"
        val set = getBookmarks().toMutableSet()
        if (!set.remove(key)) set.add(key)
        prefs.edit().putStringSet("quran_bookmarks", set).apply()
        return set
    }

    var lastReadPage: Int
        get() = prefs.getInt("last_read_page", 1)
        set(value) = prefs.edit().putInt("last_read_page", value).apply()

    /** Bugün okunan sayfalar (tekil sayfa numaraları) */
    fun getPagesReadForDay(day: String): Set<Int> =
        prefs.getStringSet("pages_$day", emptySet())?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()

    fun addPagesRead(day: String, pages: Collection<Int>) {
        val set = getPagesReadForDay(day).toMutableSet()
        if (set.addAll(pages)) prefs.edit().putStringSet("pages_$day", set.map { it.toString() }.toSet()).apply()
    }

    /** Hatim boyunca okunan tüm sayfalar (1..604) */
    fun getHatimPages(): Set<Int> =
        prefs.getStringSet("hatim_pages", emptySet())?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()

    fun addHatimPages(pages: Collection<Int>) {
        val set = getHatimPages().toMutableSet()
        if (set.addAll(pages)) prefs.edit().putStringSet("hatim_pages", set.map { it.toString() }.toSet()).apply()
    }

    fun resetHatimPages() = prefs.edit().remove("hatim_pages").apply()

    var completedHatimCount: Int
        get() = prefs.getInt("completed_hatims", 0)
        set(value) = prefs.edit().putInt("completed_hatims", value).apply()

    /** Ayet ayet okuyan kâri için everyayah klasörü */
    var ayahReciterKey: String
        get() = prefs.getString("ayah_reciter", "alafasy") ?: "alafasy"
        set(value) = prefs.edit().putString("ayah_reciter", value).apply()

    // ------------------------------------------------------------------
    // Premium (Play Billing doğrular; bu değer yalnızca önbellektir)
    // ------------------------------------------------------------------
    var premiumProductId: String?
        get() = prefs.getString("premium_product", null)
        set(value) = prefs.edit().putString("premium_product", value).apply()

    // ------------------------------------------------------------------
    // Yedekleme
    // ------------------------------------------------------------------
    /** Tüm ayarları JSON olarak dışa aktarır (premium durumu hariç; o Play'den geri yüklenir). */
    fun exportJson(): String {
        val root = JSONObject()
        root.put("app", "NurVakti").put("version", 2).put("exportedAt", System.currentTimeMillis())
        val data = JSONObject()
        for ((key, value) in prefs.all) {
            if (key.startsWith("is_premium") || key == "premium_product") continue
            val entry = JSONObject()
            when (value) {
                is Boolean -> entry.put("t", "b").put("v", value)
                is Int -> entry.put("t", "i").put("v", value)
                is Long -> entry.put("t", "l").put("v", value)
                is Float -> entry.put("t", "f").put("v", value.toDouble())
                is String -> entry.put("t", "s").put("v", value)
                is Set<*> -> entry.put("t", "set").put("v", JSONArray(value.map { it.toString() }))
                else -> continue
            }
            data.put(key, entry)
        }
        root.put("data", data)
        return root.toString()
    }

    /** Yedeği geri yükler. Başarılıysa geri yüklenen anahtar sayısını döner. */
    fun importJson(json: String): Int {
        val root = JSONObject(json)
        require(root.optString("app") == "NurVakti") { "Bu dosya bir NurVakti yedeği değil." }
        val data = root.getJSONObject("data")
        val editor = prefs.edit()
        var count = 0
        for (key in data.keys()) {
            if (key.startsWith("is_premium") || key == "premium_product") continue
            val e = data.getJSONObject(key)
            when (e.getString("t")) {
                "b" -> editor.putBoolean(key, e.getBoolean("v"))
                "i" -> editor.putInt(key, e.getInt("v"))
                "l" -> editor.putLong(key, e.getLong("v"))
                "f" -> editor.putFloat(key, e.getDouble("v").toFloat())
                "s" -> editor.putString(key, e.getString("v"))
                "set" -> {
                    val arr = e.getJSONArray("v")
                    editor.putStringSet(key, (0 until arr.length()).map { arr.getString(it) }.toSet())
                }
                else -> continue
            }
            count++
        }
        editor.commit()
        return count
    }
}
