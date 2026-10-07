package ru.aloyaloya.design_system.component.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.text.style.TextAlign
import ru.aloyaloya.design_system.extension.Entrance
import ru.aloyaloya.design_system.extension.entrance
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme

/** Реплика появляется после падения персонажа. */
private const val BUBBLE_DELAY_MILLIS = 700

/** Опора реплики: хвостик снизу. */
private val BubbleOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 1f)

/**
 * Пустое состояние: персонаж-«ты» по центру и реплика над ним.
 *
 * @param text Текст реплики.
 * @param modifier [Modifier], применяемый к блоку.
 */
@Composable
fun CharacterEmptyState(
    text: String,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        CharacterBubble(
            text = text,
            containerColor = colors.surface,
            contentColor = colors.textPrimary,
            tail = BubbleTail.BOTTOM,
            borderColor = colors.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .widthIn(max = HereSize.EmptyState.bubbleMaxWidth)
                .entrance(
                    kind = Entrance.BUBBLE,
                    delayMillis = BUBBLE_DELAY_MILLIS,
                    origin = BubbleOrigin
                )
        )

        EmotionCharacter(
            emotion = CharacterEmotion.YOU,
            size = HereSize.EmotionCharacter.small,
            modifier = Modifier
                .padding(top = HereSpacing.xs)
                .entrance(kind = Entrance.DROP)
        )
    }
}
