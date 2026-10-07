package ru.aloyaloya.design_system.component.emotion

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import ru.aloyaloya.design_system.component.character.CharacterEmotion
import ru.aloyaloya.design_system.component.character.EmotionCharacter
import ru.aloyaloya.design_system.theme.EmotionColor
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Компактная плитка эмоции: только персонаж, без подписи.
 *
 * В отличие от [EmotionTile] используется там, где эмоции стоят рядом одним рядом
 * и место есть только под персонажа. Выбранная плитка встает на насыщенный тон,
 * обводится акцентом, а ее персонаж оживает.
 *
 * @param character Персонаж эмоции.
 * @param color Пара цветов эмоции из палитры.
 * @param selected Выбрана ли плитка.
 * @param onClick Колбэк нажатия.
 * @param modifier [Modifier], применяемый к плитке.
 */
@Composable
fun EmotionChip(
    character: CharacterEmotion,
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
        EmotionCharacter(
            emotion = character,
            size = HereSize.EmotionChip.characterSize,
            animated = selected,
            shadow = false
        )
    }
}