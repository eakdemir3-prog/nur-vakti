package com.eavisionlabs.nurvakti

import com.eavisionlabs.nurvakti.data.PrayerCalculationEngine
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/**
 * Referans değerler adhan-js 4.x "Turkey" (Diyanet) yöntemiyle üretilmiştir.
 * Kotlin motoru aynı algoritmayı kullandığı için en fazla 1 dakika fark kabul edilir.
 */
class PrayerEngineTest {

    private data class Case(val name: String, val lat: Double, val lng: Double, val y: Int, val m: Int, val d: Int, val expected: LongArray)

    private val cases = listOf(
        Case("Istanbul", 41.0082, 28.9784, 2026, 1, 15, longArrayOf(1768449000000L, 1768454400000L, 1768472280000L, 1768481160000L, 1768489620000L, 1768494720000L)),
        Case("Istanbul", 41.0082, 28.9784, 2026, 6, 21, longArrayOf(1782001440000L, 1782008700000L, 1782036660000L, 1782051060000L, 1782064020000L, 1782070680000L)),
        Case("Ankara", 39.9334, 32.8597, 2026, 10, 20, longArrayOf(1792463640000L, 1792468620000L, 1792489080000L, 1792499940000L, 1792508940000L, 1792513620000L)),
        Case("Van", 38.5012, 43.373, 2026, 3, 20, longArrayOf(1773970920000L, 1773975780000L, 1773998340000L, 1774010700000L, 1774020300000L, 1774024920000L)),
        Case("Berlin", 52.52, 13.405, 2026, 6, 21, longArrayOf(1781996880500L, 1782009360000L, 1782040380000L, 1782056220000L, 1782070800000L, 1782083280500L)),
        Case("London", 51.5074, -0.1278, 2026, 12, 21, longArrayOf(1797832740000L, 1797839820000L, 1797854640000L, 1797860520000L, 1797868800000L, 1797875460000L)),
        Case("Mecca", 21.4225, 39.8262, 2026, 9, 1, longArrayOf(1788227280000L, 1788231420000L, 1788254760000L, 1788266940000L, 1788277440000L, 1788281280000L)),
    )

    @Test
    fun diyanetTimesMatchReference() {
        val names = listOf("İmsak", "Güneş", "Öğle", "İkindi", "Akşam", "Yatsı")
        for (c in cases) {
            val t = PrayerCalculationEngine.computeTimes(c.y, c.m, c.d, c.lat, c.lng, applyUserAdjust = false)
            for (i in 0 until 6) {
                val diffMin = abs(t[i] - c.expected[i]) / 60_000.0
                assertTrue("${c.name} ${c.y}-${c.m}-${c.d} ${names[i]}: fark $diffMin dk", diffMin <= 1.0)
            }
        }
    }

    @Test
    fun qiblaBearingIstanbul() {
        val bearing = PrayerCalculationEngine.calculateQiblaBearing(41.0082, 28.9784)
        assertTrue("Kıble açısı 145-160 arası olmalı: $bearing", bearing in 145f..160f)
    }
}
