package com.comixa.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ComixaDarkColorScheme = darkColorScheme(
    primary = Color(0xFF00D47B),
    onPrimary = Color(0xFF001B12),
    primaryContainer = Color(0xFF063E2B),
    onPrimaryContainer = Color(0xFF9BFFD9),
    secondary = Color(0xFFA78BFA),
    onSecondary = Color(0xFF1C1233),
    secondaryContainer = Color(0xFF2A1F49),
    onSecondaryContainer = Color(0xFFE6DCFF),
    tertiary = Color(0xFF7DD3FC),
    onTertiary = Color(0xFF001923),
    tertiaryContainer = Color(0xFF073244),
    onTertiaryContainer = Color(0xFFCDEFFF),
    background = Color(0xFF0B0F12),
    surface = Color(0xFF0E131A),
    onBackground = Color(0xFFE6EBF2),
    onSurface = Color(0xFFE6EBF2),
    surfaceVariant = Color(0xFF1A212B),
    onSurfaceVariant = Color(0xFFA8B3C2),
    outline = Color(0xFF3A4654),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF311000),
)

val ComixaLightColorScheme = lightColorScheme(
    primary = Color(0xFF007A4B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF9BFFD9),
    onPrimaryContainer = Color(0xFF001B12),
    secondary = Color(0xFF5C3D9E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE6DCFF),
    onSecondaryContainer = Color(0xFF1C1233),
    tertiary = Color(0xFF007FB8),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFCDEFFF),
    onTertiaryContainer = Color(0xFF001923),
    background = Color(0xFFF4FAF7),
    surface = Color(0xFFF4FAF7),
    onBackground = Color(0xFF0B1512),
    onSurface = Color(0xFF0B1512),
    surfaceVariant = Color(0xFFD9E5DC),
    onSurfaceVariant = Color(0xFF3F4D44),
    outline = Color(0xFF6F7E74),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
)

@Composable
fun ComixaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) ComixaDarkColorScheme else ComixaLightColorScheme,
        typography = ComixaTypography,
        content = content,
    )
}
