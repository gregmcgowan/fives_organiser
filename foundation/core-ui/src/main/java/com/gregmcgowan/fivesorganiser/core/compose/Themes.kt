package com.gregmcgowan.fivesorganiser.core.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightThemeColors =
    lightColorScheme(
        primary = Green_500,
        onPrimary = Color.White,
        secondary = Green_300,
        onSecondary = Color.White,
        error = Red800,
    )

// TODO
private val DarkThemeColors =
    darkColorScheme(
        primary = Green_500,
        onPrimary = Color.Black,
        secondary = Green_300,
        onSecondary = Color.White,
        error = Red800,
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
