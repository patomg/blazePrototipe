package com.blaze.fitness.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = BlazeOrange,
    onPrimary = Color.White,
    secondary = BlazeNavy,
    background = BlazeSurface,
    surface = BlazeSurface,
    error = BlazeDanger,
)

private val DarkColors = darkColorScheme(
    primary = BlazeOrange,
    onPrimary = BlazeNavy,
    secondary = BlazeOnDark,
    background = BlazeNavy,
    surface = BlazeNavyLight,
    error = BlazeDanger,
)

@Composable
fun BlazeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BlazeTypography,
        content = content,
    )
}
