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
import ru.aloyaloya.design_system.extension.overlayShadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
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
 * @param size Диаметр пина: на карточке места он мельче, чем на экране воспоминания.
 * @param border Толщина обводки цветом поверхности.
 * @param emojiSize Кегль эмодзи внутри пина.
 */
@Composable
fun EmotionPin(
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = HereSize.EmotionPin.size,
    border: Dp = HereSize.EmotionPin.border,
    emojiSize: TextUnit = HereSize.EmotionPin.emojiSize
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .overlayShadow(CircleShape)
            .background(color = color, shape = CircleShape)
            .border(
                width = border,
                color = HereTheme.colors.surface,
                shape = CircleShape
            )
    ) {
        Text(
            text = emoji,
            fontSize = emojiSize
        )
    }
}