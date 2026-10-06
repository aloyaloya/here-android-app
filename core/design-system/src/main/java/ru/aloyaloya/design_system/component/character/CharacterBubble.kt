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
import androidx.compose.ui.unit.Dp
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
 * @param tailInset Центр хвостика от начала стороны или `null` — по центру.
 */
@Composable
fun CharacterBubble(
    text: String,
    containerColor: Color,
    contentColor: Color,
    tail: BubbleTail,
    modifier: Modifier = Modifier,
    tailInset: Dp? = null
) {
    val tailSize = HereSize.CharacterBubble.tailSize
    val protrusion = tailSize / sqrt(2f)

    Box(
        modifier = modifier
            .drawBehind {
                val half = protrusion.toPx()
                val ltr = layoutDirection == LayoutDirection.Ltr
                val start = if (ltr) half else size.width - half
                val end = if (ltr) size.width - half else half
                val alongX = tailInset?.toPx()?.let { if (ltr) it else size.width - it } ?: (size.width / 2)
                val alongY = tailInset?.toPx() ?: (size.height / 2)
                val center = when (tail) {
                    BubbleTail.TOP -> Offset(alongX, half)
                    BubbleTail.BOTTOM -> Offset(alongX, size.height - half)
                    BubbleTail.START -> Offset(start, alongY)
                    BubbleTail.END -> Offset(end, alongY)
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
            style = MaterialTheme.typography.bodyLarge,
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
