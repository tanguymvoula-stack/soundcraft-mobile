package com.soundcraft.studio.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Palette sombre moderne : fond quasi noir, surfaces étagées, un seul accent orange. */
object Palette {
    val Background = Color(0xFF0F1115)
    val Surface = Color(0xFF171A21)
    val SurfaceHigh = Color(0xFF212631)
    val Border = Color(0xFF2C323F)
    val Accent = Color(0xFFFF8A3D)
    val OnAccent = Color(0xFF1A0E04)
    val TextPrimary = Color(0xFFE8EAEF)
    val TextMuted = Color(0xFF9AA1B0)
    val Error = Color(0xFFFF6B6B)
}

@Composable
fun StudioTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Palette.Accent,
            onPrimary = Palette.OnAccent,
            background = Palette.Background,
            onBackground = Palette.TextPrimary,
            surface = Palette.Surface,
            onSurface = Palette.TextPrimary,
        ),
        content = content,
    )
}
