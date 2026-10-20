package com.eavisionlabs.nurvakti.model

data class City(
    val id: String,
    val name: String,
    val country: String = "Türkiye",
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double = 3.0,
    val isFavorite: Boolean = false
)
