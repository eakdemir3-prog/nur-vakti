package com.eavisionlabs.nurvakti.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Uygulama paleti. Ekranlarda kullanılan renk adları (EmeraldDarkest, GoldChampagne vb.)
 * seçili temaya (Koyu / Açık / Kâbe Örtüsü) göre değer alır.
 */
@Immutable
data class NurPalette(
    val darkest: Color,
    val deep: Color,
    val secondary: Color,
    val card: Color,
    val cardElevated: Color,
    val border: Color,
    val gold: Color,
    val goldLight: Color,
    val goldMuted: Color,
    val goldContainer: Color,
    val cream: Color,
    val softGrey: Color,
    val muted: Color,
    val isDark: Boolean
)

object Palettes {
    val Dark = NurPalette(
        darkest = Color(0xFF0B211C), deep = Color(0xFF123B30), secondary = Color(0xFF1C5140),
        card = Color(0xFF17352D), cardElevated = Color(0xFF1D4238), border = Color(0xFF265A4A),
        gold = Color(0xFFD8B875), goldLight = Color(0xFFEAD8A6), goldMuted = Color(0xFF9E8148), goldContainer = Color(0xFF2B2515),
        cream = Color(0xFFF7F4EC), softGrey = Color(0xFFA9B5AF), muted = Color(0xFF7F9188), isDark = true
    )
    val Light = NurPalette(
        darkest = Color(0xFFF9F7F1), deep = Color(0xFFF0ECE1), secondary = Color(0xFFE4DDCB),
        card = Color(0xFFFFFFFF), cardElevated = Color(0xFFF4F0E4), border = Color(0xFFDDD7C3),
        gold = Color(0xFF875E14), goldLight = Color(0xFF6E4C0F), goldMuted = Color(0xFF9E8148), goldContainer = Color(0xFFF3E6C4),
        cream = Color(0xFF13362C), softGrey = Color(0xFF56655E), muted = Color(0xFF6F7D76), isDark = false
    )
    /** Kâbe örtüsü (Kiswa) — siyah & altın (Pro) */
    val Kiswa = NurPalette(
        darkest = Color(0xFF0A0A0A), deep = Color(0xFF141414), secondary = Color(0xFF1F1A10),
        card = Color(0xFF171511), cardElevated = Color(0xFF221E16), border = Color(0xFF3A3220),
        gold = Color(0xFFD4AF37), goldLight = Color(0xFFE8D48A), goldMuted = Color(0xFFA4862A), goldContainer = Color(0xFF2A2312),
        cream = Color(0xFFF5EFE0), softGrey = Color(0xFFB3AA98), muted = Color(0xFF8C8473), isDark = true
    )
}

val LocalNurPalette = staticCompositionLocalOf { Palettes.Dark }

val EmeraldDarkest: Color @Composable @ReadOnlyComposable get() = LocalNurPalette.current.darkest
val EmeraldDeep: Color @Composable @ReadOnlyComposable get() = LocalNurPalette.current.deep
val EmeraldSecondary: Color @Composable @ReadOnlyComposable get() = LocalNurPalette.current.secondary
val EmeraldCard: Color @Composable @ReadOnlyComposable get() = LocalNurPalette.current.card
val EmeraldCardElevated: Color @Composable @ReadOnlyComposable get() = LocalNurPalette.current.cardElevated
val EmeraldBorder: Color @Composable @ReadOnlyComposable get() = LocalNurPalette.current.border
val GoldChampagne: Color @Composable @ReadOnlyComposable get() = LocalNurPalette.current.gold
val GoldLight: Color @Composable @ReadOnlyComposable get() = LocalNurPalette.current.goldLight
val GoldMuted: Color @Composable @ReadOnlyComposable get() = LocalNurPalette.current.goldMuted
val GoldContainer: Color @Composable @ReadOnlyComposable get() = LocalNurPalette.current.goldContainer
val CreamWhite: Color @Composable @ReadOnlyComposable get() = LocalNurPalette.current.cream
val SoftGrey: Color @Composable @ReadOnlyComposable get() = LocalNurPalette.current.softGrey
val MutedText: Color @Composable @ReadOnlyComposable get() = LocalNurPalette.current.muted

// Sabit renkler (her temada aynı)
val IvoryBackground = Color(0xFFF9F7F1)
val StoneLight = Color(0xFFF0ECE1)
val EmeraldTextDark = Color(0xFF13362C)
val GoldAccentLight = Color(0xFF875E14)
val SecondaryTextLight = Color(0xFF56655E)
val CardLight = Color(0xFFFFFFFF)
val CardBorderLight = Color(0xFFDDD7C3)

// Kur'an okuma — sepya
val SepiaBackground = Color(0xFFFBF0D9)
val SepiaCard = Color(0xFFF4E4C1)
val SepiaText = Color(0xFF3E2723)
val SepiaSecondary = Color(0xFF6D4C41)
val SepiaGold = Color(0xFF9C6B1F)
