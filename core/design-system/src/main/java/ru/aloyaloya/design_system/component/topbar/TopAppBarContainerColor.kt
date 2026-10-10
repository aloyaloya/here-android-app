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
 */
@Composable
internal fun animateTopAppBarContainerColor(scrolled: Boolean): State<Color> =
    animateColorAsState(
        targetValue = if (scrolled) HereTheme.colors.surfaceMuted else HereTheme.colors.background,
        animationSpec = tween(HereMotion.Duration.medium),
        label = "top-app-bar-container"
    )
