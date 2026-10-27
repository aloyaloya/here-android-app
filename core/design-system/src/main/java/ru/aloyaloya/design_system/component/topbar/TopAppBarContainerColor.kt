package ru.aloyaloya.design_system.component.topbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import ru.aloyaloya.design_system.theme.HereMotion
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Фон верхней панели: темнеет, когда контент уезжает под нее.
 *
 * @param scrolled Уехал ли контент под панель.
 * @param overMedia Панель поверх снимка: пока контент не уехал, фон прозрачный.
 */
@Composable
internal fun animateTopAppBarContainerColor(
    scrolled: Boolean,
    overMedia: Boolean = false
): State<Color> =
    animateColorAsState(
        targetValue = when {
            scrolled -> HereTheme.colors.surfaceMuted
            overMedia -> Color.Transparent
            else -> HereTheme.colors.background
        },
        animationSpec = tween(HereMotion.Duration.medium),
        label = "top-app-bar-container"
    )

/**
 * Цвет значков и заголовка панели: поверх снимка белый.
 *
 * @param scrolled Уехал ли контент под панель.
 * @param overMedia Панель поверх снимка.
 */
@Composable
internal fun animateTopAppBarContentColor(
    scrolled: Boolean,
    overMedia: Boolean
): State<Color> =
    animateColorAsState(
        targetValue = if (overMedia && !scrolled) Color.White else HereTheme.colors.textPrimary,
        animationSpec = tween(HereMotion.Duration.medium),
        label = "top-app-bar-content"
    )
