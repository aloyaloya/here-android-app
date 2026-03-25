package ru.aloyaloya.design_system.component.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import ru.aloyaloya.design_system.R

/**
 * Кастомный компонент нижней панели навигации для приложения Here.
 *
 * Этот composable предоставляет стилизованную нижнюю панель навигации на основе
 * Material Design 3 [NavigationBar] с параметрами внешнего вида, настроенными под Here.
 * По сути это обертка над стандартным [NavigationBar] с преднастроенными стилями.
 *
 * Панель использует цвет `surface` из текущей темы и отключает тональную высоту,
 * чтобы получить плоский современный вид, согласованный с визуальным стилем приложения.
 *
 * @param modifier [Modifier], применяемый к базовому компоненту [NavigationBar].
 * @param content Контент, который будет размещен внутри панели навигации.
 */
@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    NavigationBar(
        modifier = modifier.height(dimensionResource(R.dimen.bottom_nav_height)),
        containerColor = MaterialTheme.colorScheme.secondary,
        tonalElevation = dimensionResource(R.dimen.bottom_nav_tonal_elevation),
        content = content
    )
}