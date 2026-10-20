package com.eavisionlabs.nurvakti.model

enum class PrayerType(val code: String, val titleTr: String, val titleAr: String, val isFardh: Boolean) {
    IMSAK("Imsak", "İmsak", "الفجر", true),
    GUNES("Sunrise", "Güneş", "الشروق", false),
    OGLE("Dhuhr", "Öğle", "الظهر", true),
    IKINDI("Asr", "İkindi", "العصر", true),
    AKSAM("Maghrib", "Akşam", "المغرب", true),
    YATSI("Isha", "Yatsı", "العشاء", true)
}

enum class NotificationPreference {
    DISABLED,
    AZAN,
    NOTIFICATION_SOUND,
    SILENT,
    VIBRATE_ONLY
}

data class PrayerTimeItem(
    val type: PrayerType,
    val timeFormatted: String, // "05:14"
    val timestampMillis: Long,
    val isPassed: Boolean = false,
    val isCurrent: Boolean = false,
    val isNext: Boolean = false,
    val notificationPref: NotificationPreference = NotificationPreference.AZAN
)

data class DailyPrayerSchedule(
    val cityName: String,
    val districtName: String = "",
    val gregorianDateFormatted: String, // "18 Ekim 2026 Pazar"
    val hijriDateFormatted: String,    // "6 Cemaziyelahir 1448"
    val prayerTimes: List<PrayerTimeItem>,
    val nextPrayer: PrayerTimeItem?,
    val currentPrayer: PrayerTimeItem?,
    val remainingMillisToNext: Long
)

data class MonthlyDaySchedule(
    val dayNumber: Int,
    val gregorianDate: String,
    val hijriDate: String,
    val imsak: String,
    val gunes: String,
    val ogle: String,
    val ikindi: String,
    val aksam: String,
    val yatsi: String
)
