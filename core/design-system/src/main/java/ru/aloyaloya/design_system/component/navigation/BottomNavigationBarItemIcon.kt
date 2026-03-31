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
import androidx.compose.ui.res.dimensionResource
import ru.aloyaloya.design_system.R

/**
 * Кастомный контент иконки элемента нижней панели навигации для приложения Here.
 *
 * Компонент отображает иконку и текстовую подпись в вертикальной компоновке, а также
 * анимирует переключение между выбранным и невыбранным состояниями через [Crossfade].
 * Цвет иконки и текста подбирается из текущей темы [MaterialTheme] в зависимости от
 * состояния выбора.
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
    val selectedColor = MaterialTheme.colorScheme.onSecondaryFixed
    val unselectedColor = MaterialTheme.colorScheme.onSecondary

    Crossfade(targetState = selected, label = "bottom-navigation-icon") { isSelected ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.bottom_nav_item_icon_label_spacing)
            )
        ) {
            Icon(
                modifier = Modifier.size(dimensionResource(R.dimen.bottom_nav_item_icon_size)),
                painter = if (isSelected) selectedPainter else unselectedPainter,
                tint = if (isSelected) selectedColor else unselectedColor,
                contentDescription = label
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) selectedColor else unselectedColor,
            )
        }
    }
}