package ru.aloyaloya.design_system.component.emotion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Эмоция строкой: эмодзи и название на мягком тоне эмоции.
 *
 * @param emoji Эмодзи эмоции.
 * @param label Название эмоции.
 * @param color Мягкий тон эмоции из палитры: фон тега.
 * @param modifier [Modifier], применяемый к тегу.
 */
@Composable
fun EmotionTag(
    emoji: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HereSize.EmotionTag.spacing),
        modifier = modifier
            .background(color = color, shape = HereShape.chip)
            .padding(
                vertical = HereSize.EmotionTag.verticalPadding,
                horizontal = HereSize.EmotionTag.horizontalPadding
            )
    ) {
        Text(
            text = emoji,
            fontSize = HereSize.EmotionTag.emojiSize
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = HereTheme.colors.textPrimary
        )
    }
}
