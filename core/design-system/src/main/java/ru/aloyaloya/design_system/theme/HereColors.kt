package ru.aloyaloya.design_system.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Пара цветов одной эмоции.
 *
 * @property solid Насыщенный тон: заливка пина, столбца диаграммы, выбранной плитки.
 * @property soft Мягкий тон: фон плитки, ячейки календаря, квадрата-иконки.
 */
@Immutable
data class EmotionColor(
    val solid: Color,
    val soft: Color
)

/**
 * Палитра эмоций.
 *
 * Поля названы по смыслу эмоции, а не по значениям `Emotion`: дизайн-система не знает
 * про слой данных. Сопоставление эмоции с цветом делают фичи, которые ее отображают.
 */
@Immutable
data class EmotionColors(
    val happy: EmotionColor,
    val tender: EmotionColor,
    val surprised: EmotionColor,
    val calm: EmotionColor,
    val sad: EmotionColor,
    val angry: EmotionColor
)

/**
 * Палитра приложения Here.
 *
 * Material 3 [androidx.compose.material3.ColorScheme] остается для системных компонентов,
 * а свои экраны берут цвета отсюда. Из composable доступна через [HereTheme.colors].
 */
@Immutable
data class HereColors(
    val background: Color,
    val surface: Color,
    val surfaceMuted: Color,
    val outline: Color,

    /** Обводка заметнее [outline]: ею обведены элементы без заливки, вроде плашек даты и времени. */
    val outlineStrong: Color,

    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accent: Color,
    val onAccent: Color,
    val danger: Color,
    val dangerContainer: Color,
    val emotions: EmotionColors,

    /** Темная ли палитра. В темной теме тени заменяются обводкой цветом [outline]. */
    val isDark: Boolean
)

val LightHereColors = HereColors(
    background = LightBackground,
    surface = LightSurface,
    surfaceMuted = LightSurfaceMuted,
    outline = LightOutline,
    outlineStrong = LightOutlineStrong,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textTertiary = LightTextTertiary,
    accent = LightAccent,
    onAccent = LightOnAccent,
    danger = LightDanger,
    dangerContainer = LightDangerContainer,
    emotions = EmotionColors(
        happy = EmotionColor(LightHappySolid, LightHappySoft),
        tender = EmotionColor(LightTenderSolid, LightTenderSoft),
        surprised = EmotionColor(LightSurprisedSolid, LightSurprisedSoft),
        calm = EmotionColor(LightCalmSolid, LightCalmSoft),
        sad = EmotionColor(LightSadSolid, LightSadSoft),
        angry = EmotionColor(LightAngrySolid, LightAngrySoft)
    ),
    isDark = false
)

val DarkHereColors = HereColors(
    background = DarkBackground,
    surface = DarkSurface,
    surfaceMuted = DarkSurfaceMuted,
    outline = DarkOutline,
    outlineStrong = DarkOutlineStrong,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textTertiary = DarkTextTertiary,
    accent = DarkAccent,
    onAccent = DarkOnAccent,
    danger = DarkDanger,
    dangerContainer = DarkDangerContainer,
    emotions = EmotionColors(
        happy = EmotionColor(DarkHappySolid, DarkHappySoft),
        tender = EmotionColor(DarkTenderSolid, DarkTenderSoft),
        surprised = EmotionColor(DarkSurprisedSolid, DarkSurprisedSoft),
        calm = EmotionColor(DarkCalmSolid, DarkCalmSoft),
        sad = EmotionColor(DarkSadSolid, DarkSadSoft),
        angry = EmotionColor(DarkAngrySolid, DarkAngrySoft)
    ),
    isDark = true
)

/** Палитра текущей темы, ее провайдит [HereTheme]. */
val LocalHereColors = staticCompositionLocalOf { LightHereColors }