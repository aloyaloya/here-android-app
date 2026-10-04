package ru.aloyaloya.design_system.component.pager

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Индикатор страниц: точки, текущая вытянута в пилюлю.
 *
 * @param pageCount Число страниц.
 * @param currentPage Индекс текущей страницы.
 * @param modifier [Modifier], применяемый к индикатору.
 */
@Composable
fun HerePagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HereSize.PagerIndicator.spacing),
        modifier = modifier
    ) {
        repeat(pageCount) { page ->
            PagerDot(active = page == currentPage)
        }
    }
}

/**
 * Точка индикатора.
 *
 * @param active Текущая ли это страница.
 */
@Composable
private fun PagerDot(active: Boolean) {
    val colors = HereTheme.colors
    val sizes = HereSize.PagerIndicator

    val width by animateDpAsState(
        targetValue = if (active) sizes.activeWidth else sizes.dotSize,
        label = "pager-dot-width"
    )

    val color by animateColorAsState(
        targetValue = if (active) colors.textPrimary else colors.outlineStrong,
        label = "pager-dot-color"
    )

    Box(
        modifier = Modifier
            .width(width)
            .height(sizes.dotSize)
            .background(color = color, shape = HereShape.pill)
    )
}
