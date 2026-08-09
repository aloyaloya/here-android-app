package ru.aloyaloya.design_system.component.emotion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.aloyaloya.design_system.extension.cardShadow
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Пин воспоминания: эмодзи эмоции в круге на фоне ее цвета.
 *
 * Так воспоминание помечается на карте и в превью выбранного места.
 *
 * @param emoji Эмодзи эмоции.
 * @param color Насыщенный тон эмоции из палитры.
 * @param modifier [Modifier], применяемый к пину.
 */
@Composable
fun EmotionPin(
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(HereSize.EmotionPin.size)
            .cardShadow(CircleShape)
            .background(color = color, shape = CircleShape)
            .border(
                width = HereSize.EmotionPin.border,
                color = HereTheme.colors.surface,
                shape = CircleShape
            )
    ) {
        Text(
            text = emoji,
            fontSize = HereSize.EmotionPin.emojiSize
        )
    }
}