package ru.aloyaloya.mapkit.internal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import androidx.compose.ui.graphics.toArgb
import com.yandex.runtime.image.ImageProvider
import ru.aloyaloya.mapkit.model.MapMarkerIcon

/** Сторона иконки в dp: в нее должны поместиться круг, обводка и тень. */
private const val ICON_SIZE_DP = 58f

private const val CIRCLE_RADIUS_DP = 23f
private const val OUTLINE_WIDTH_DP = 4f
private const val EMOJI_SIZE_DP = 28f

private const val SHADOW_RADIUS_DP = 4f
private const val SHADOW_OFFSET_DP = 2f
private const val SHADOW_COLOR = 0x40000000

/** Сдвиг каждой следующей метки в стопке: вправо и вверх. */
private const val STACK_SHIFT_X_DP = 12f
private const val STACK_SHIFT_Y_DP = 5f

/**
 * Иконки меток.
 *
 * MapKit принимает готовые картинки, поэтому метка рисуется в bitmap.
 *
 * Рисовать заново на каждую метку дорого, а разных иконок мало — по одной на эмоцию,
 * поэтому готовые картинки складываются в кэш. Ключ — сама иконка целиком: цвета
 * меняются вместе с темой, и картинку тогда нужно рисовать заново.
 */
internal object MarkerIcons {

    private val cache = mutableMapOf<MapMarkerIcon, ImageProvider>()
    private val stackCache = mutableMapOf<List<MapMarkerIcon>, StackIcon>()

    fun get(context: Context, icon: MapMarkerIcon): ImageProvider =
        cache.getOrPut(icon) { ImageProvider.fromBitmap(draw(context, listOf(icon))) }

    /**
     * Стопка меток для нескольких воспоминаний рядом.
     *
     * @param icons Метки от передней к задней.
     */
    fun stack(context: Context, icons: List<MapMarkerIcon>): StackIcon =
        stackCache.getOrPut(icons) {
            val back = icons.size - 1
            val width = ICON_SIZE_DP + back * STACK_SHIFT_X_DP
            val height = ICON_SIZE_DP + back * STACK_SHIFT_Y_DP
            StackIcon(
                image = ImageProvider.fromBitmap(draw(context, icons)),
                anchor = PointF(ICON_SIZE_DP / 2 / width, 1 - ICON_SIZE_DP / 2 / height)
            )
        }

    private fun draw(context: Context, icons: List<MapMarkerIcon>): Bitmap {
        val density = context.resources.displayMetrics.density
        val back = icons.size - 1
        val width = ((ICON_SIZE_DP + back * STACK_SHIFT_X_DP) * density).toInt()
        val height = ((ICON_SIZE_DP + back * STACK_SHIFT_Y_DP) * density).toInt()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val half = ICON_SIZE_DP / 2 * density

        icons.asReversed().forEachIndexed { index, icon ->
            val layer = back - index
            canvas.drawMarker(
                icon = icon,
                x = half + layer * STACK_SHIFT_X_DP * density,
                y = height - half - layer * STACK_SHIFT_Y_DP * density,
                density = density
            )
        }

        return bitmap
    }

    private fun Canvas.drawMarker(icon: MapMarkerIcon, x: Float, y: Float, density: Float) {
        val outline = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = icon.outline.toArgb()
            setShadowLayer(
                SHADOW_RADIUS_DP * density,
                0f,
                SHADOW_OFFSET_DP * density,
                SHADOW_COLOR
            )
        }
        val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = icon.fill.toArgb()
        }

        drawCircle(x, y, CIRCLE_RADIUS_DP * density, outline)
        drawCircle(x, y, (CIRCLE_RADIUS_DP - OUTLINE_WIDTH_DP) * density, fill)
        drawEmoji(icon.emoji, x, y, density)
    }

    /**
     * Рисует эмодзи по центру круга.
     *
     * Текст рисуется от базовой линии, поэтому центр строки приходится считать
     * по метрикам шрифта.
     */
    private fun Canvas.drawEmoji(emoji: String, x: Float, y: Float, density: Float) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = EMOJI_SIZE_DP * density
            textAlign = Paint.Align.CENTER
        }
        val metrics = paint.fontMetrics
        val baseline = y - (metrics.ascent + metrics.descent) / 2

        drawText(emoji, x, baseline, paint)
    }
}

/** Картинка стопки и якорь: точка карты приходится на центр передней метки. */
internal class StackIcon(
    val image: ImageProvider,
    val anchor: PointF
)
