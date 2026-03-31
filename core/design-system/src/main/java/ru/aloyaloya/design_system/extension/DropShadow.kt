package ru.aloyaloya.design_system.extension

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Добавляет к содержимому эффект отбрасываемой тени (drop shadow).
 *
 * Тень рисуется за контентом через [drawBehind]: цвет, размытие, смещение и «разнос»
 * настраиваются параметрами. Размытие выполняется на стороне Canvas
 * ([BlurMaskFilter]); контур тени задаётся [Shape] и обычно совпадает с формой виджета.
 *
 * @param shape Контур тени; как правило, совпадает с формой элемента (например, круг).
 * @param color Цвет тени; по умолчанию полупрозрачный чёрный.
 * @param blur Радиус размытия: чем больше, тем мягче тень.
 * @param offsetX Смещение тени по горизонтали (положительные значения — вправо).
 * @param offsetY Смещение тени по вертикали (положительные значения — вниз).
 * @param spread Расширение контура тени относительно границ контента.
 * @return [Modifier] с нарисованной за контентом тенью.
 */
fun Modifier.dropShadow(
    shape: Shape,
    color: Color = Color.Black.copy(0.1f),
    blur: Dp = 8.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 4.dp,
    spread: Dp = 0.dp,
) = this.drawBehind {

    val spreadPx = spread.toPx()
    val shadowSize = Size(
        width = size.width + spreadPx * 2,
        height = size.height + spreadPx * 2
    )

    val shadowOutline = shape.createOutline(
        size = shadowSize,
        layoutDirection = layoutDirection,
        density = this
    )

    val paint = Paint().apply {
        this.color = color
        if (blur.toPx() > 0) {
            asFrameworkPaint().maskFilter = BlurMaskFilter(
                blur.toPx(),
                BlurMaskFilter.Blur.NORMAL
            )
        }
    }

    if (blur.toPx() > 0) {
        paint.asFrameworkPaint().apply {
            maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
        }
    }

    drawIntoCanvas { canvas ->
        canvas.save()
        canvas.translate(offsetX.toPx(), offsetY.toPx())
        canvas.drawOutline(shadowOutline, paint)
        canvas.restore()
    }
}