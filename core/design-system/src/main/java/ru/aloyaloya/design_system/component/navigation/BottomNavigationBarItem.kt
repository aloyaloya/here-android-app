package ru.aloyaloya.design_system.component.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

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
    NavigationBarItem(
        onClick = onClick,
        selected = selected,
        icon = {
            Crossfade(targetState = selected, label = "bottom-navigation-icon") { isSelected ->
                Icon(
                    modifier = Modifier.size(28.dp),
                    painter = if (isSelected) selectedPainter else unselectedPainter,
                    contentDescription = label
                )
            }
        },
        modifier = modifier,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.surfaceVariant,
            unselectedIconColor = MaterialTheme.colorScheme.surfaceTint,
            indicatorColor = Color.Transparent
        )
    )
}