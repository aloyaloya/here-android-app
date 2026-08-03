package ru.aloyaloya.design_system.component.emotion

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
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
 * Компактная плитка эмоции: только эмодзи, без подписи.
 *
 * В отличие от [EmotionTile] используется там, где эмоции стоят рядом одним рядом
 * и место есть только под эмодзи. Выбранная плитка встает на насыщенный тон
 * и обводится акцентом.
 *
 * @param emoji Эмодзи эмоции.
 * @param color Пара цветов эмоции из палитры.
 * @param selected Выбрана ли плитка.
 * @param onClick Колбэк нажатия.
 * @param modifier [Modifier], применяемый к плитке.
 */
@Composable
fun EmotionChip(
    emoji: String,
    color: EmotionColor,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background by animateColorAsState(
        targetValue = if (selected) color.solid else color.soft,
        label = "emotion-chip-background"
    )

    val borderColor by animateColorAsState(
        targetValue = if (selected) HereTheme.colors.accent else Color.Transparent,
        label = "emotion-chip-border"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(HereSize.EmotionChip.size)
            .clip(HereShape.tile)
            .background(background)
            .border(
                width = HereSize.EmotionChip.selectedBorder,
                color = borderColor,
                shape = HereShape.tile
            )
            .clickable(onClick = onClick)
    ) {
        Text(
            text = emoji,
            fontSize = HereSize.EmotionChip.emojiSize
        )
    }
}