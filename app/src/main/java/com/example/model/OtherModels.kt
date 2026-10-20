package com.example.model

data class DhikrItem(
    val id: String,
    val arabic: String,
    val turkishTitle: String,
    val pronunciation: String,
    val meaning: String,
    val targetCount: Int = 33,
    val currentCount: Int = 0,
    val virtue: String = "",
    val isCustom: Boolean = false
)

data class DuaCategory(
    val id: String,
    val titleTr: String,
    val iconKey: String
)

data class DuaItem(
    val id: String,
    val categoryId: String,
    val titleTr: String,
    val arabicText: String,
    val pronunciationTr: String,
    val meaningTr: String,
    val source: String,
    val isFavorite: Boolean = false
)

data class AsmaItem(
    val number: Int,
    val arabic: String,
    val nameTr: String,
    val meaningTr: String,
    val virtueTr: String,
    val isFavorite: Boolean = false
)

data class ReligiousDayItem(
    val id: String,
    val title: String,
    val hijriDate: String,
    val gregorianDate: String,
    val daysRemaining: Int,
    val description: String
)

data class HatimRecord(
    val id: String,
    val title: String,
    val startDate: String,
    val completedJuzs: Set<Int> = emptySet(),
    val targetEndDate: String = ""
)
