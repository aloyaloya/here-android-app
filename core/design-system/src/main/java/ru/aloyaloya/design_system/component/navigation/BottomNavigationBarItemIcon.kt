package ru.aloyaloya.design_system.component.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Содержимое элемента нижней панели навигации: иконка и подпись под ней.
 *
 * Смена состояния анимируется через [Crossfade]. У выбранного элемента иконка
 * и подпись акцентного цвета, у невыбранного — приглушенного.
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
    val selectedColor = HereTheme.colors.accent
    val unselectedColor = HereTheme.colors.textTertiary

    Crossfade(targetState = selected, label = "bottom-navigation-icon") { isSelected ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HereSize.NavBar.itemIconLabelSpacing)
        ) {
            Icon(
                modifier = Modifier.size(HereSize.NavBar.itemIconSize),
                painter = if (isSelected) selectedPainter else unselectedPainter,
                tint = if (isSelected) selectedColor else unselectedColor,
                contentDescription = label
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) selectedColor else unselectedColor
            )
        }
    }
}