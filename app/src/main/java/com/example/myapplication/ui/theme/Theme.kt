package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColors = lightColorScheme(
    primary = Mint, onPrimary = Night,
    primaryContainer = MintSoft, onPrimaryContainer = Mint,
    secondary = Sky, onSecondary = Night, tertiary = Coral,
    background = Snow, onBackground = Ink,
    surface = Color.White, onSurface = Ink,
    surfaceVariant = Color(0xFFEEF2F6), onSurfaceVariant = Muted,
    outline = Line
)

@Composable
fun MyApplicationTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AppColors, typography = Typography, content = content)
}
