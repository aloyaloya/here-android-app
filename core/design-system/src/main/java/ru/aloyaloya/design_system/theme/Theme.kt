package ru.aloyaloya.design_system.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val LightScheme = lightColorScheme(
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceMuted,
    onSurfaceVariant = LightTextSecondary,
    outlineVariant = LightOutline,
    primary = LightAccent,
    onPrimary = LightOnAccent,
    error = LightDanger,
    errorContainer = LightDangerContainer
)

private val DarkScheme = darkColorScheme(
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceMuted,
    onSurfaceVariant = DarkTextSecondary,
    outlineVariant = DarkOutline,
    primary = DarkAccent,
    onPrimary = DarkOnAccent,
    error = DarkDanger,
    errorContainer = DarkDangerContainer
)

/**
 * Тема приложения Here.
 *
 * Кроме Material-схемы провайдит палитру [HereColors] через [LocalHereColors]:
 * из нее берут цвета собственные компоненты приложения.
 *
 * @param darkTheme Темная ли тема. По умолчанию берется из конфигурации.
 * @param content Контент, отрисовываемый в этой теме.
 */
@Composable
fun HereTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalHereColors provides if (darkTheme) DarkHereColors else LightHereColors
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkScheme else LightScheme,
            typography = Typography,
            shapes = HereShapes,
            content = content
        )
    }
}

/** Короткий доступ к палитре текущей темы: `HereTheme.colors.accent`. */
object HereTheme {
    val colors: HereColors
        @Composable
        @ReadOnlyComposable
        get() = LocalHereColors.current
}