package com.gregmcgowan.fivesorganiser.core.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightThemeColors =
    lightColorScheme(
        primary = GREEN,
        onPrimary = WHITE,
        secondary = GOLD_1,
        onSecondary = WHITE,
        background = WHITE_2,
        onBackground = BLACK_1,
        surface = WHITE,
        onSurface = BLACK_1,
        error = RED_1,
        onError = WHITE,
    )

// TODO
private val DarkThemeColors =
    darkColorScheme(
        primary = GREEN_1,
        onPrimary = Color(0xFF00390F),
        secondary = GOLD_2,
        onSecondary = Color(0xFF4A3900),
        background = Color(0xFF121412),
        onBackground = WHITE_1,
        surface = BLACK_1,
        onSurface = WHITE_1,
        error = Color(0xFFF2B8B5),
        onError = RED_2,
    )

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkThemeColors else LightThemeColors,
        content = content,
        // //TODO typography
        // typography =
    )
}
