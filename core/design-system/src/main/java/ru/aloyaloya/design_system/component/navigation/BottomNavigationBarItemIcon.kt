package ru.aloyaloya.design_system.component.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import ru.aloyaloya.design_system.theme.HereMotion
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import kotlin.math.roundToInt

/**
 * Содержимое элемента нижней панели навигации: иконка и подпись под ней.
 *
 * У выбранного элемента под иконкой вырастает из центра овальный индикатор, как в
 * Material 3, а цвет иконки и подписи плавно перетекает в акцентный.
 *
 * @param selected Флаг, показывающий, выбран ли элемент в текущий момент.
 * @param selectedPainter [Painter] иконки для выбранного состояния.
 * @param unselectedPainter [Painter] иконки для невыбранного состояния.
 * @param label Текстовая подпись элемента навигации.
 */
@Composable
fun BottomNavigationBarItemIcon(
    selected: Boolean,
    selectedPainter: Painter,
    unselectedPainter: Painter,
    label: String
) {
    val indicatorProgress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "bottom-navigation-indicator"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) HereTheme.colors.accent else HereTheme.colors.textTertiary,
        animationSpec = tween(HereMotion.Duration.medium),
        label = "bottom-navigation-content"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HereSize.NavBar.itemIconLabelSpacing)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(
                width = HereSize.NavBar.indicatorWidth,
                height = HereSize.NavBar.indicatorHeight
            )
        ) {
            Box(
                modifier = Modifier
                    .layout { measurable, constraints ->
                        val width = (constraints.maxWidth * indicatorProgress)
                            .roundToInt()
                            .coerceAtLeast(0)
                        val placeable = measurable.measure(
                            Constraints.fixed(width, constraints.maxHeight)
                        )
                        layout(placeable.width, placeable.height) {
                            placeable.place(0, 0)
                        }
                    }
                    .graphicsLayer { alpha = indicatorProgress.coerceIn(0f, 1f) }
                    .clip(HereShape.pill)
                    .background(HereTheme.colors.accentContainer)
            )
            Icon(
                modifier = Modifier.size(HereSize.NavBar.itemIconSize),
                painter = if (selected) selectedPainter else unselectedPainter,
                tint = contentColor,
                contentDescription = label
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            color = contentColor
        )
    }
}
