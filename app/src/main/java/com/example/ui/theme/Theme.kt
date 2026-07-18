package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TarteelColorScheme = darkColorScheme(
    primary = RadiantEmerald,
    secondary = RadiantEmerald,
    tertiary = SecondarySilver,
    background = DeepSlate,
    surface = NavyGray,
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite,
    surfaceVariant = NavyGray,
    onSurfaceVariant = TextWhite
)

@Composable
fun TarteelRiseTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TarteelColorScheme,
        typography = Typography,
        content = content
    )
}
