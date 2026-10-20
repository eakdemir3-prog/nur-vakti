package com.example.data

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("nurvakti_prefs", Context.MODE_PRIVATE)

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
}
