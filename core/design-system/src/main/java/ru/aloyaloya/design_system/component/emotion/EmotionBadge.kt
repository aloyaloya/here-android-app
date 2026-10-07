package ru.aloyaloya.design_system.component.emotion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.aloyaloya.design_system.component.character.CharacterEmotion
import ru.aloyaloya.design_system.component.character.EmotionCharacter
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize

/**
 * Иконка воспоминания: статичный персонаж эмоции на ее мягком тоне.
 *
 * @param character Персонаж эмоции.
 * @param color Мягкий тон эмоции из палитры.
 * @param modifier [Modifier], применяемый к иконке.
 */
@Composable
fun EmotionBadge(
    character: CharacterEmotion,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(HereSize.EmotionBadge.size)
            .background(color = color, shape = HereShape.tile)
    ) {
        EmotionCharacter(
            emotion = character,
            size = HereSize.EmotionBadge.characterSize,
            animated = false
        )
    }
}
