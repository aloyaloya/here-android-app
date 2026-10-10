package ru.aloyaloya.mapkit.internal

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import androidx.core.content.ContextCompat
import com.yandex.runtime.image.ImageProvider
import ru.aloyaloya.mapkit.model.MapMarkerIcon

/** Ширина метки в dp. */
private const val ICON_WIDTH_DP = 56f

/** Высота метки в долях ширины: слои персонажа 100×120. */
private const val ICON_HEIGHT_RATIO = 1.2f

/** Точка карты по высоте метки: центр тени под персонажем. */
private const val FOOT_Y = 104f / 120f

/** Сдвиг каждой следующей метки в стопке: вправо и вверх. */
private const val STACK_SHIFT_X_DP = 12f
private const val STACK_SHIFT_Y_DP = 5f

/**
 * Иконки меток.
 *
 * MapKit принимает готовые картинки, поэтому метка рисуется в bitmap.
 *
 * Рисовать заново на каждую метку дорого, а разных иконок мало — по одной на эмоцию,
 * поэтому готовые картинки складываются в кэш. Цвета картинок зависят от темы, поэтому
 * в ключе кэша есть и она.
 */
internal object MarkerIcons {

    /** Точка карты на одиночной метке. */
    val anchor = PointF(0.5f, FOOT_Y)

    fun bounds(x: Float, y: Float, density: Float): RectF {
        val width = ICON_WIDTH_DP * density
        val height = width * ICON_HEIGHT_RATIO
        val top = y - height * FOOT_Y
        return RectF(x - width / 2, top, x + width / 2, top + height)
    }

    private val cache = mutableMapOf<Pair<MapMarkerIcon, Boolean>, ImageProvider>()
    private val stackCache = mutableMapOf<Pair<List<MapMarkerIcon>, Boolean>, StackIcon>()

    fun get(context: Context, icon: MapMarkerIcon, dark: Boolean): ImageProvider =
        cache.getOrPut(icon to dark) {
            ImageProvider.fromBitmap(draw(context.themed(dark), listOf(icon)))
        }

    /**
     * Стопка меток для нескольких воспоминаний рядом.
     *
     * @param icons Метки от передней к задней.
     * @param dark Темная ли тема.
     */
    fun stack(context: Context, icons: List<MapMarkerIcon>, dark: Boolean): StackIcon =
        stackCache.getOrPut(icons to dark) {
            val back = icons.size - 1
            val iconHeight = ICON_WIDTH_DP * ICON_HEIGHT_RATIO
            val width = ICON_WIDTH_DP + back * STACK_SHIFT_X_DP
            val height = iconHeight + back * STACK_SHIFT_Y_DP
            StackIcon(
                image = ImageProvider.fromBitmap(draw(context.themed(dark), icons)),
                anchor = PointF(ICON_WIDTH_DP / 2 / width, 1 - iconHeight * (1 - FOOT_Y) / height)
            )
        }

    private fun draw(context: Context, icons: List<MapMarkerIcon>): Bitmap {
        val density = context.resources.displayMetrics.density
        val back = icons.size - 1
        val iconWidth = (ICON_WIDTH_DP * density).toInt()
        val iconHeight = (ICON_WIDTH_DP * ICON_HEIGHT_RATIO * density).toInt()
        val shiftX = (STACK_SHIFT_X_DP * density).toInt()
        val shiftY = (STACK_SHIFT_Y_DP * density).toInt()
        val bitmap = Bitmap.createBitmap(
            iconWidth + back * shiftX,
            iconHeight + back * shiftY,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        icons.asReversed().forEachIndexed { index, icon ->
            val layer = back - index
            val left = layer * shiftX
            val top = (back - layer) * shiftY
            ContextCompat.getDrawable(context, icon.image)?.run {
                setBounds(left, top, left + iconWidth, top + iconHeight)
                draw(canvas)
            }
        }

        return bitmap
    }

    /** Контекст с темой карты: тема приложения не всегда совпадает с системной. */
    private fun Context.themed(dark: Boolean): Context {
        val configuration = Configuration(resources.configuration)
        val night = if (dark) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
        configuration.uiMode =
            night or (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv())
        return createConfigurationContext(configuration)
    }
}

/** Картинка стопки и якорь: точка карты приходится на тень передней метки. */
internal class StackIcon(
    val image: ImageProvider,
    val anchor: PointF
)
