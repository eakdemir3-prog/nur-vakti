package com.eavisionlabs.nurvakti.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NurVaktiDarkColorScheme = darkColorScheme(
    primary = GoldChampagne,
    onPrimary = EmeraldDarkest,
    primaryContainer = EmeraldSecondary,
    onPrimaryContainer = GoldLight,
    secondary = GoldLight,
    onSecondary = EmeraldDarkest,
    secondaryContainer = EmeraldCardElevated,
    onSecondaryContainer = CreamWhite,
    tertiary = GoldMuted,
    onTertiary = EmeraldDarkest,
    background = EmeraldDarkest,
    onBackground = CreamWhite,
    surface = EmeraldCard,
    onSurface = CreamWhite,
    surfaceVariant = EmeraldDeep,
    onSurfaceVariant = SoftGrey,
    outline = EmeraldBorder,
    outlineVariant = EmeraldSecondary
)

val NurVaktiLightColorScheme = lightColorScheme(
    primary = GoldAccentLight,
    onPrimary = Color.White,
    primaryContainer = StoneLight,
    onPrimaryContainer = EmeraldTextDark,
    secondary = EmeraldTextDark,
    onSecondary = Color.White,
    secondaryContainer = StoneLight,
    onSecondaryContainer = EmeraldTextDark,
    tertiary = GoldMuted,
    onTertiary = Color.White,
    background = IvoryBackground,
    onBackground = EmeraldTextDark,
    surface = CardLight,
    onSurface = EmeraldTextDark,
    surfaceVariant = StoneLight,
    onSurfaceVariant = SecondaryTextLight,
    outline = CardBorderLight,
    outlineVariant = StoneLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) NurVaktiDarkColorScheme else NurVaktiLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
