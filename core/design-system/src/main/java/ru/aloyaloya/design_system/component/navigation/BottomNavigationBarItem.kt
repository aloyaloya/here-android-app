package ru.aloyaloya.design_system.component.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter

/** Масштаб элемента в нажатом состоянии. */
private const val PRESSED_SCALE = 1.06f

/**
 * Кастомный элемент нижней панели навигации для приложения Here.
 *
 * Это расширение для [RowScope]: элемент занимает равную долю ширины панели,
 * показывает иконку с подписью и увеличивается при нажатии. Ripple отключен —
 * обратная связь дается масштабом и сменой цвета.
 *
 * @param selectedPainter [Painter] иконки для выбранного состояния.
 * @param unselectedPainter [Painter] иконки для невыбранного состояния.
 * @param label Текстовая подпись элемента навигации.
 * @param selected Флаг, показывающий, выбран ли элемент в текущий момент.
 * @param onClick Колбэк, вызываемый при нажатии на элемент.
 * @param modifier [Modifier], применяемый к контейнеру элемента.
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
        if (isPressed) PRESSED_SCALE else 1f
    }

    Box(
        modifier = modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin.Center
            },
        contentAlignment = Alignment.Center
    ) {
        BottomNavigationBarItemIcon(
            selected = selected,
            selectedPainter = selectedPainter,
            unselectedPainter = unselectedPainter,
            label = label
        )
    }
}
