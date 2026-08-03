package ru.aloyaloya.mapkit.internal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.ui.graphics.toArgb
import com.yandex.runtime.image.ImageProvider
import ru.aloyaloya.mapkit.model.UserLocationStyle

/** Сторона иконки в dp: в нее должны поместиться маркер, обводка и тень. */
private const val ICON_SIZE_DP = 48f

private const val ARROW_TIP_DP = 8f
private const val ARROW_BASE_DP = 36f
private const val ARROW_NOTCH_DP = 29f
private const val ARROW_HALF_WIDTH_DP = 11f

private const val DOT_RADIUS_DP = 8f
private const val OUTLINE_WIDTH_DP = 3f

private const val SHADOW_RADIUS_DP = 4f
private const val SHADOW_OFFSET_DP = 2f
private const val SHADOW_COLOR = 0x40000000

/**
 * Иконки маркера текущего положения.
 *
 * MapKit принимает готовые картинки, поэтому маркер рисуется в bitmap: так его цвета
 * можно взять из темы приложения, а не держать по набору drawable на каждую тему.
 */
internal object UserLocationIcons {

    /** Стрелка: показывается, когда известно направление. MapKit сам поворачивает ее по курсу. */
    fun arrow(context: Context, style: UserLocationStyle): ImageProvider =
        ImageProvider.fromBitmap(
            draw(context, style) { canvas, density, fill, outline ->
                val path = arrowPath(density)
                canvas.drawPath(path, outline)
                canvas.drawPath(path, fill)
            }
        )

    /** Точка: показывается, пока направление неизвестно. */
    fun pin(context: Context, style: UserLocationStyle): ImageProvider =
        ImageProvider.fromBitmap(
            draw(context, style) { canvas, density, fill, outline ->
                val center = ICON_SIZE_DP / 2 * density
                val radius = DOT_RADIUS_DP * density
                canvas.drawCircle(center, center, radius, outline)
                canvas.drawCircle(center, center, radius, fill)
            }
        )

    /**
     * Готовит холст и кисти, общие для обеих иконок.
     *
     * Обводка рисуется первой и шире заливки, поэтому выступает из-под нее ровным кантом.
     * Тень висит на кисти обводки: у заливки она осталась бы под самим маркером.
     */
    private fun draw(
        context: Context,
        style: UserLocationStyle,
        body: (canvas: Canvas, density: Float, fill: Paint, outline: Paint) -> Unit
    ): Bitmap {
        val density = context.resources.displayMetrics.density
        val size = (ICON_SIZE_DP * density).toInt()
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.style = Paint.Style.FILL
            color = style.fill.toArgb()
        }

        val outline = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.style = Paint.Style.FILL_AND_STROKE
            strokeWidth = OUTLINE_WIDTH_DP * density * 2
            strokeJoin = Paint.Join.ROUND
            color = style.outline.toArgb()
            setShadowLayer(
                SHADOW_RADIUS_DP * density,
                0f,
                SHADOW_OFFSET_DP * density,
                SHADOW_COLOR
            )
        }

        body(Canvas(bitmap), density, fill, outline)
        return bitmap
    }

    /** Стрелка-«змей»: острие вверх, хвост с выемкой. */
    private fun arrowPath(density: Float): Path {
        val centerX = ICON_SIZE_DP / 2 * density
        val halfWidth = ARROW_HALF_WIDTH_DP * density

        return Path().apply {
            moveTo(centerX, ARROW_TIP_DP * density)
            lineTo(centerX + halfWidth, ARROW_BASE_DP * density)
            lineTo(centerX, ARROW_NOTCH_DP * density)
            lineTo(centerX - halfWidth, ARROW_BASE_DP * density)
            close()
        }
    }
}
