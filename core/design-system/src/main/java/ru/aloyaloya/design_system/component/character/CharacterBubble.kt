package ru.aloyaloya.design_system.component.character

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
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
 * @param borderColor Цвет обводки или `null` — без нее.
 */
@Composable
fun CharacterBubble(
    text: String,
    containerColor: Color,
    contentColor: Color,
    tail: BubbleTail,
    modifier: Modifier = Modifier,
    tailInset: Dp? = null,
    borderColor: Color? = null
) {
    val protrusion = HereSize.CharacterBubble.tailSize / sqrt(2f)
    val borderWidth = if (borderColor != null) HereSize.CharacterBubble.borderWidth else 0.dp

    Box(
        modifier = modifier
            .drawWithContent {
                drawContent()

                val half = protrusion.toPx()
                val stroke = borderWidth.toPx()
                val ltr = layoutDirection == LayoutDirection.Ltr
                val alongX = tailInset?.toPx()?.let { if (ltr) it else size.width - it } ?: (size.width / 2)
                val alongY = tailInset?.toPx() ?: (size.height / 2)
                val startSign = if (ltr) -1f else 1f
                val (base, out) = when (tail) {
                    BubbleTail.TOP -> Offset(alongX, half) to Offset(0f, -1f)
                    BubbleTail.BOTTOM -> Offset(alongX, size.height - half) to Offset(0f, 1f)
                    BubbleTail.START -> Offset(if (ltr) half else size.width - half, alongY) to Offset(startSign, 0f)
                    BubbleTail.END -> Offset(if (ltr) size.width - half else half, alongY) to Offset(-startSign, 0f)
                }
                val across = Offset(out.y, out.x) * half
                val apex = base + out * half

                val fill = Path().apply {
                    moveTo(base.x + across.x - out.x * stroke, base.y + across.y - out.y * stroke)
                    lineTo(apex.x, apex.y)
                    lineTo(base.x - across.x - out.x * stroke, base.y - across.y - out.y * stroke)
                    close()
                }
                drawPath(fill, containerColor)

                if (borderColor != null) {
                    val edges = Path().apply {
                        moveTo(base.x + across.x, base.y + across.y)
                        lineTo(apex.x, apex.y)
                        lineTo(base.x - across.x, base.y - across.y)
                    }
                    drawPath(edges, borderColor, style = Stroke(width = stroke, join = StrokeJoin.Round))
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
                .then(
                    if (borderColor != null) {
                        Modifier.border(borderWidth, borderColor, HereShape.bubble)
                    } else {
                        Modifier
                    }
                )
                .padding(
                    vertical = HereSize.CharacterBubble.verticalPadding,
                    horizontal = HereSize.CharacterBubble.horizontalPadding
                )
        )
    }
}
