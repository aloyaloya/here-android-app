package ru.aloyaloya.design_system.component.character

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import kotlin.math.sqrt

/** Сторона пузыря, с которой торчит хвостик. */
enum class BubbleTail { TOP, BOTTOM, START, END }

/**
 * Реплика персонажа: пузырь с хвостиком к нему.
 *
 * @param text Текст реплики.
 * @param containerColor Фон пузыря и хвостика.
 * @param contentColor Цвет текста.
 * @param tail Сторона хвостика.
 */
@Composable
fun CharacterBubble(
    text: String,
    containerColor: Color,
    contentColor: Color,
    tail: BubbleTail,
    modifier: Modifier = Modifier
) {
    val tailSize = HereSize.CharacterBubble.tailSize
    val protrusion = tailSize / sqrt(2f)

    Box(
        modifier = modifier
            .drawBehind {
                val half = protrusion.toPx()
                val start = if (layoutDirection == LayoutDirection.Ltr) half else size.width - half
                val end = if (layoutDirection == LayoutDirection.Ltr) size.width - half else half
                val center = when (tail) {
                    BubbleTail.TOP -> Offset(size.width / 2, half)
                    BubbleTail.BOTTOM -> Offset(size.width / 2, size.height - half)
                    BubbleTail.START -> Offset(start, size.height / 2)
                    BubbleTail.END -> Offset(end, size.height / 2)
                }
                val side = tailSize.toPx()
                rotate(degrees = 45f, pivot = center) {
                    drawRect(
                        color = containerColor,
                        topLeft = center - Offset(side / 2, side / 2),
                        size = Size(side, side)
                    )
                }
            }
            .padding(
                top = if (tail == BubbleTail.TOP) protrusion else 0.dp,
                bottom = if (tail == BubbleTail.BOTTOM) protrusion else 0.dp,
                start = if (tail == BubbleTail.START) protrusion else 0.dp,
                end = if (tail == BubbleTail.END) protrusion else 0.dp
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            modifier = Modifier
                .background(color = containerColor, shape = HereShape.bubble)
                .padding(
                    vertical = HereSize.CharacterBubble.verticalPadding,
                    horizontal = HereSize.CharacterBubble.horizontalPadding
                )
        )
    }
}
