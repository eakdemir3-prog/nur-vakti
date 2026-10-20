package com.eavisionlabs.nurvakti.model

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameTurkish: String,
    val meaningTurkish: String,
    val totalVerses: Int,
    val revelationType: String, // "Mekki" or "Medeni"
    val revelationOrder: Int,
    val startPage: Int,
    val juzNumber: Int
)

data class Ayah(
    val surahNumber: Int,
    val ayahNumber: Int,
    val textArabic: String,
    val translationTr: String,
    val footnote: String? = null,
    val audioUrl: String? = null,
    var isFavorite: Boolean = false,
    var isBookmarked: Boolean = false
)
