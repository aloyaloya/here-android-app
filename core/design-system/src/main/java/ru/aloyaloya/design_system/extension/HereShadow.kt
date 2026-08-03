package ru.aloyaloya.design_system.extension

import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Тень карточки.
 *
 * @param shape Форма элемента, к которому применяется тень.
 */
@Composable
fun Modifier.cardShadow(shape: Shape): Modifier =
    themedShadow(shape = shape, blur = 12.dp, offsetY = 2.dp, alpha = 0.04f)

/**
 * Тень верхней панели приложения.
 *
 * @param shape Форма элемента, к которому применяется тень.
 */
@Composable
fun Modifier.headerShadow(shape: Shape): Modifier =
    themedShadow(shape = shape, blur = 18.dp, offsetY = 4.dp, alpha = 0.08f)

/**
 * Тень нижней панели навигации.
 *
 * @param shape Форма элемента, к которому применяется тень.
 */
@Composable
fun Modifier.navBarShadow(shape: Shape): Modifier =
    themedShadow(shape = shape, blur = 30.dp, offsetY = 10.dp, alpha = 0.14f)

/**
 * Тень нижнего листа: падает вверх, на затемнённый экран.
 *
 * @param shape Форма элемента, к которому применяется тень.
 */
@Composable
fun Modifier.sheetShadow(shape: Shape): Modifier =
    themedShadow(shape = shape, blur = 40.dp, offsetY = (-12).dp, alpha = 0.14f)

/**
 * Тень FAB-кнопки, окрашенная в акцентный цвет.
 *
 * В отличие от нейтральных теней она хорошо видна и на темном фоне,
 * поэтому в темной теме не заменяется обводкой.
 *
 * @param shape Форма элемента, к которому применяется тень.
 */
@Composable
fun Modifier.fabShadow(shape: Shape): Modifier =
    dropShadow(
        shape = shape,
        color = HereTheme.colors.accent.copy(alpha = 0.42f),
        blur = 24.dp,
        offsetY = 10.dp
    )

/**
 * Тень основной кнопки, окрашенная в акцентный цвет.
 *
 * Как и у FAB, она видна в обеих темах, поэтому обводкой не заменяется.
 *
 * @param shape Форма элемента, к которому применяется тень.
 */
@Composable
fun Modifier.buttonShadow(shape: Shape): Modifier =
    dropShadow(
        shape = shape,
        color = HereTheme.colors.accent.copy(alpha = 0.35f),
        blur = 20.dp,
        offsetY = 8.dp
    )

/**
 * Мягкая тень в светлой теме и обводка в темной.
 *
 * На темном фоне черная тень почти не видна, поэтому вместо нее элемент
 * отделяется от фона линией цветом [ru.aloyaloya.design_system.theme.HereColors.outline].
 *
 * @param shape Форма элемента.
 * @param blur Радиус размытия тени.
 * @param offsetY Вертикальное смещение тени.
 * @param alpha Прозрачность черного цвета тени.
 */
@Composable
private fun Modifier.themedShadow(
    shape: Shape,
    blur: Dp,
    offsetY: Dp,
    alpha: Float
): Modifier {
    val colors = HereTheme.colors
    return if (colors.isDark) {
        border(width = 1.dp, color = colors.outline, shape = shape)
    } else {
        dropShadow(
            shape = shape,
            color = Color.Black.copy(alpha = alpha),
            blur = blur,
            offsetY = offsetY
        )
    }
}
