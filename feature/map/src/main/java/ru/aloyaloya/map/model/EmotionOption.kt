package ru.aloyaloya.map.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import ru.aloyaloya.design_system.theme.EmotionColor
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.map.R

/**
 * Эмоция в листе выбора на карте.
 *
 * Пока это UI-модель: воспоминания еще негде хранить, поэтому со списком
 * `Emotion` из базы она не связана. Названия эмоций сопоставим при работе с хранилищем.
 *
 * @property emoji Эмодзи эмоции.
 * @property labelResId Идентификатор строкового ресурса с названием эмоции.
 */
enum class EmotionOption(
    val emoji: String,
    @StringRes val labelResId: Int
) {
    HAPPY(emoji = "😊", labelResId = R.string.emotion_happy),
    TENDER(emoji = "🥰", labelResId = R.string.emotion_tender),
    EXCITED(emoji = "🤩", labelResId = R.string.emotion_excited),
    CALM(emoji = "😌", labelResId = R.string.emotion_calm),
    SAD(emoji = "😢", labelResId = R.string.emotion_sad),
    ANGRY(emoji = "😠", labelResId = R.string.emotion_angry)
}

/** Пара цветов эмоции из палитры текущей темы. */
val EmotionOption.color: EmotionColor
    @Composable
    @ReadOnlyComposable
    get() = HereTheme.colors.emotions.let { emotions ->
        when (this) {
            EmotionOption.HAPPY -> emotions.happy
            EmotionOption.TENDER -> emotions.tender
            EmotionOption.EXCITED -> emotions.excited
            EmotionOption.CALM -> emotions.calm
            EmotionOption.SAD -> emotions.sad
            EmotionOption.ANGRY -> emotions.angry
        }
    }