package ru.aloyaloya.design_system.component.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.theme.HereShapes

/**
 * Кастомный элемент нижней панели навигации для приложения Here.
 *
 * Это расширение для [RowScope] предоставляет стилизованный элемент навигации
 * с кастомными цветами и внешним видом, соответствующим дизайн-системе Here.
 * Функция оборачивает стандартный [NavigationBarItem] с преднастроенными стилями
 * для иконки и цветовой схемы.
 *
 * @param selectedPainter [Painter] иконки для выбранного состояния.
 * @param unselectedPainter [Painter] иконки для невыбранного состояния.
 * @param label Текстовая подпись элемента навигации.
 * @param selected Флаг, показывающий, выбран ли элемент в текущий момент.
 * @param onClick Колбэк, вызываемый при нажатии на элемент.
 * @param modifier [Modifier], применяемый к базовому [NavigationBarItem].
 */
@Composable
fun RowScope.BottomNavigationBarItem(
    selectedPainter: Painter,
    unselectedPainter: Painter,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val transition = updateTransition(targetState = pressed, label = "bottom-nav-pressed")
    val scale by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                snap()
            } else {
                spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium,
                )
            }
        },
        label = "bottom-nav-item-scale",
    ) { isPressed ->
        if (isPressed) 1.06f else 1f
    }

    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        NavigationBarItem(
            onClick = onClick,
            selected = selected,
            interactionSource = interactionSource,
            icon = {
                BottomNavigationBarItemIcon(
                    selected = selected,
                    selectedPainter = selectedPainter,
                    unselectedPainter = unselectedPainter,
                    label = label
                )
            },
            modifier = modifier
                .padding(
                    horizontal = dimensionResource(R.dimen.bottom_nav_item_padding_horizontal),
                    vertical = dimensionResource(R.dimen.bottom_nav_item_padding_vertical)
                )
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin.Center
                }
                .clip(HereShapes.extraLarge)
                .background(
                    shape = HereShapes.extraLarge,
                    color = if (selected) MaterialTheme.colorScheme.secondaryContainer
                    else Color.Transparent
                ),
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )
    }
}
