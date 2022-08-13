package com.zabster.safeappcompose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = primaryColor,
    primaryVariant = primaryDarkColor,
    secondary = secondaryColor,
)

private val LightColorPalette = lightColors(
    primary = primaryLightColor,
    primaryVariant = primaryDarkColor,
    secondary = secondaryColor,
    background = backgroundLight,
    surface = backgroundLight,
)

@Composable
fun SafeAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColorPalette
    else LightColorPalette

    val systemUiController = rememberSystemUiController()

    if (darkTheme) {
        systemUiController.setSystemBarsColor(
            color = DarkColorPalette.background
        )
    } else {
        systemUiController.setSystemBarsColor(
            color = LightColorPalette.background
        )
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}