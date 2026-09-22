package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = MinimalPrimaryLight,
    onPrimary = MinimalOnPrimaryLight,
    primaryContainer = MinimalPrimaryContainerLight,
    onPrimaryContainer = MinimalOnPrimaryContainerLight,
    secondary = MinimalSecondaryLight,
    onSecondary = MinimalOnPrimaryLight,
    secondaryContainer = MinimalSecondaryContainerLight,
    onSecondaryContainer = MinimalOnSecondaryContainerLight,
    tertiary = MinimalPrimaryLight,
    background = MinimalBgLight,
    onBackground = MinimalOnBgLight,
    surface = MinimalSurfaceLight,
    onSurface = MinimalOnBgLight,
    surfaceVariant = MinimalSurfaceVariantLight,
    onSurfaceVariant = MinimalOnSurfaceVariantLight,
    outline = MinimalOutlineLight,
    outlineVariant = MinimalOutlineVariantLight,
    error = MinimalError,
    onError = MinimalOnPrimaryLight
)

private val DarkColorScheme = darkColorScheme(
    primary = MinimalPrimaryDark,
    onPrimary = MinimalOnPrimaryDark,
    primaryContainer = MinimalPrimaryContainerDark,
    onPrimaryContainer = MinimalOnPrimaryContainerDark,
    secondary = MinimalSecondaryDark,
    onSecondary = MinimalOnPrimaryDark,
    secondaryContainer = MinimalSecondaryContainerDark,
    onSecondaryContainer = MinimalOnSecondaryContainerDark,
    tertiary = MinimalPrimaryDark,
    background = MinimalBgDark,
    onBackground = MinimalOnBgDark,
    surface = MinimalSurfaceDark,
    onSurface = MinimalOnBgDark,
    surfaceVariant = MinimalSurfaceVariantDark,
    onSurfaceVariant = MinimalOnSurfaceVariantDark,
    outline = MinimalOutlineDark,
    outlineVariant = MinimalOutlineVariantDark,
    error = MinimalError,
    onError = MinimalOnPrimaryLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
