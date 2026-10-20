package com.eavisionlabs.nurvakti.model

data class City(
    val id: String,
    val name: String,
    val country: String = "Türkiye",
    val latitude: Double,
    val longitude: Double,
    /** Eski alan; hesaplar artık [timeZoneId] üzerinden (yaz saati dahil) yapılır. */
    val timezoneOffsetHours: Double = 3.0,
    val isFavorite: Boolean = false,
    val timeZoneId: String = "Europe/Istanbul",
    /** İlçe ise bağlı olduğu il adı (ör. "İstanbul"), il merkezi için boş. */
    val province: String = ""
) {
    /** Ekranda gösterilecek tam ad: "Kadıköy, İstanbul" ya da "İstanbul". */
    val displayName: String get() = if (province.isNotBlank()) "$name, $province" else name
}
