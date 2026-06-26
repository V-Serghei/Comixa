package com.comixa.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
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

@Composable
fun ComixaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
