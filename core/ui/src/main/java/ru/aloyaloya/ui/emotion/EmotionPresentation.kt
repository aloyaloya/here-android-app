package ru.aloyaloya.ui.emotion

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import ru.aloyaloya.design_system.theme.EmotionColor
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.ui.R

/**
 * Как эмоция выглядит на экранах.
 *
 * Домен знает только список эмоций, дизайн-система — только палитру. Сопоставление
 * лежит здесь, чтобы лист выбора, календарь и аналитика показывали эмоцию одинаково.
 */

/** Эмодзи эмоции. */
val Emotion.emoji: String
    get() = when (this) {
        Emotion.HAPPY -> "😊"
        Emotion.TENDER -> "🥰"
        Emotion.SURPRISED -> "😮"
        Emotion.CALM -> "😌"
        Emotion.SAD -> "😢"
        Emotion.ANGRY -> "😠"
    }

/** Название эмоции. */
val Emotion.labelResId: Int
    @StringRes
    get() = when (this) {
        Emotion.HAPPY -> R.string.emotion_happy
        Emotion.TENDER -> R.string.emotion_tender
        Emotion.SURPRISED -> R.string.emotion_surprised
        Emotion.CALM -> R.string.emotion_calm
        Emotion.SAD -> R.string.emotion_sad
        Emotion.ANGRY -> R.string.emotion_angry
    }

/** Пара цветов эмоции из палитры текущей темы. */
val Emotion.color: EmotionColor
    @Composable
    @ReadOnlyComposable
    get() = HereTheme.colors.emotions.let { emotions ->
        when (this) {
            Emotion.HAPPY -> emotions.happy
            Emotion.TENDER -> emotions.tender
            Emotion.SURPRISED -> emotions.surprised
            Emotion.CALM -> emotions.calm
            Emotion.SAD -> emotions.sad
            Emotion.ANGRY -> emotions.angry
        }
    }
