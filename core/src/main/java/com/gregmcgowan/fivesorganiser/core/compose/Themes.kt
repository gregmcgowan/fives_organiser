package com.gregmcgowan.fivesorganiser.core.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightThemeColors = lightColors(
        primary = Green_500,
        primaryVariant = Green_500,
        onPrimary = Color.White,
        secondary = Green_300,
        secondaryVariant = Green_300,
        onSecondary = Color.White,
        error = Red800
)

//TODO
private val DarkThemeColors = darkColors(
        primary = Green_500,
        primaryVariant = Green_500,
        onPrimary = Color.Black,
        secondary = Green_300,
        onSecondary = Color.White,
        error = Red800
)


//@Composable
//val Colors.snackbarAction: Color
//    get() = if (isLight) Red300 else Red700

@Composable
fun AppTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit
) {
    MaterialTheme(
            colors = if (darkTheme) DarkThemeColors else LightThemeColors,
            content = content,
            // //TODO typography
            //typography =
    )
}
