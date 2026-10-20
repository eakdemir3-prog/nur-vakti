package com.eavisionlabs.nurvakti.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private fun schemeFor(p: NurPalette): ColorScheme = if (p.isDark) darkColorScheme(
    primary = p.gold,
    onPrimary = p.darkest,
    primaryContainer = p.secondary,
    onPrimaryContainer = p.goldLight,
    secondary = p.goldLight,
    onSecondary = p.darkest,
    secondaryContainer = p.cardElevated,
    onSecondaryContainer = p.cream,
    tertiary = p.goldMuted,
    onTertiary = p.darkest,
    background = p.darkest,
    onBackground = p.cream,
    surface = p.card,
    onSurface = p.cream,
    surfaceVariant = p.deep,
    onSurfaceVariant = p.softGrey,
    surfaceContainer = p.card,
    surfaceContainerHigh = p.cardElevated,
    surfaceContainerHighest = p.cardElevated,
    outline = p.border,
    outlineVariant = p.secondary
) else lightColorScheme(
    primary = p.gold,
    onPrimary = Color.White,
    primaryContainer = p.deep,
    onPrimaryContainer = p.cream,
    secondary = p.cream,
    onSecondary = Color.White,
    secondaryContainer = p.deep,
    onSecondaryContainer = p.cream,
    tertiary = p.goldMuted,
    onTertiary = Color.White,
    background = p.darkest,
    onBackground = p.cream,
    surface = p.card,
    onSurface = p.cream,
    surfaceVariant = p.deep,
    onSurfaceVariant = p.softGrey,
    surfaceContainer = p.card,
    surfaceContainerHigh = p.cardElevated,
    surfaceContainerHighest = p.cardElevated,
    outline = p.border,
    outlineVariant = p.deep
)

/** themeMode: "system", "dark", "light", "kiswa" */
fun paletteFor(themeMode: String, systemDark: Boolean): NurPalette = when (themeMode) {
    "light" -> Palettes.Light
    "kiswa" -> Palettes.Kiswa
    "system" -> if (systemDark) Palettes.Dark else Palettes.Light
    else -> Palettes.Dark
}

@Composable
fun MyApplicationTheme(
    themeMode: String = "dark",
    content: @Composable () -> Unit
) {
    val palette = paletteFor(themeMode, isSystemInDarkTheme())
    CompositionLocalProvider(LocalNurPalette provides palette) {
        MaterialTheme(colorScheme = schemeFor(palette), typography = Typography, content = content)
    }
}
