package ru.aloyaloya.design_system.component.emotion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize

/**
 * Иконка воспоминания: эмодзи эмоции на ее мягком тоне.
 *
 * @param emoji Эмодзи эмоции.
 * @param color Мягкий тон эмоции из палитры.
 * @param modifier [Modifier], применяемый к иконке.
 */
@Composable
fun EmotionBadge(
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(HereSize.EmotionBadge.size)
            .background(color = color, shape = HereShape.tile)
    ) {
        Text(
            text = emoji,
            fontSize = HereSize.EmotionBadge.emojiSize
        )
    }
}
