package ru.aloyaloya.design_system.component.emotion

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import ru.aloyaloya.design_system.theme.EmotionColor
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Плитка эмоции: эмодзи и подпись на мягком тоне этой эмоции.
 *
 * Выбранная плитка обводится акцентным цветом.
 *
 * @param emoji Эмодзи эмоции.
 * @param label Название эмоции.
 * @param color Пара цветов эмоции из палитры.
 * @param selected Выбрана ли плитка.
 * @param onClick Колбэк нажатия.
 * @param modifier [Modifier], применяемый к плитке.
 */
@Composable
fun EmotionTile(
    emoji: String,
    label: String,
    color: EmotionColor,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (selected) HereTheme.colors.accent else Color.Transparent,
        label = "emotion-tile-border"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HereSize.EmotionTile.contentSpacing),
        modifier = modifier
            .clip(HereShape.cardLarge)
            .background(color.soft)
            .border(
                width = HereSize.EmotionTile.selectedBorder,
                color = borderColor,
                shape = HereShape.cardLarge
            )
            .clickable(onClick = onClick)
            .padding(
                vertical = HereSize.EmotionTile.verticalPadding,
                horizontal = HereSize.EmotionTile.horizontalPadding
            )
    ) {
        Text(
            text = emoji,
            fontSize = HereSize.EmotionTile.emojiSize
        )
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = HereTheme.colors.textPrimary
        )
    }
}