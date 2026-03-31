package ru.aloyaloya.design_system.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    onPrimaryFixed = DarkOnPrimaryFixed,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondary = DarkOnSecondary,
    onSecondaryFixed = DarkOnSecondaryFixed,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    onTertiaryFixed = DarkOnTertiaryFixed,
    tertiaryContainer = DarkTertiaryContainer
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    onPrimaryFixed = LightOnPrimaryFixed,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondary = LightOnSecondary,
    onSecondaryFixed = LightOnSecondaryFixed,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    onTertiaryFixed = LightOnTertiaryFixed,
    tertiaryContainer = LightTertiaryContainer
)

@Composable
fun HereTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}