package ru.aloyaloya.mapkit.internal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
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

    fun get(context: Context, icon: MapMarkerIcon): ImageProvider =
        cache.getOrPut(icon) { ImageProvider.fromBitmap(draw(context, icon)) }

    private fun draw(context: Context, icon: MapMarkerIcon): Bitmap {
        val density = context.resources.displayMetrics.density
        val size = (ICON_SIZE_DP * density).toInt()
        val center = ICON_SIZE_DP / 2 * density
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

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

        canvas.drawCircle(center, center, CIRCLE_RADIUS_DP * density, outline)
        canvas.drawCircle(center, center, (CIRCLE_RADIUS_DP - OUTLINE_WIDTH_DP) * density, fill)
        canvas.drawEmoji(icon.emoji, center, density)

        return bitmap
    }

    /**
     * Рисует эмодзи по центру круга.
     *
     * Текст рисуется от базовой линии, поэтому центр строки приходится считать
     * по метрикам шрифта.
     */
    private fun Canvas.drawEmoji(emoji: String, center: Float, density: Float) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = EMOJI_SIZE_DP * density
            textAlign = Paint.Align.CENTER
        }
        val metrics = paint.fontMetrics
        val baseline = center - (metrics.ascent + metrics.descent) / 2

        drawText(emoji, center, baseline, paint)
    }
}
