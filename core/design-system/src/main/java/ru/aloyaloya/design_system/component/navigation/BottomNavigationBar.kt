package ru.aloyaloya.design_system.component.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import ru.aloyaloya.design_system.extension.navBarShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Кастомный компонент нижней панели навигации для приложения Here.
 *
 * Панель не прижата к краю экрана, а лежит поверх контента с отступами от краёв
 * и собственным скруглением. Material Design 3 [androidx.compose.material3.NavigationBar]
 * для этого не подошел: у него своя минимальная высота и свои window insets.
 * Отступы от краёв экрана задает вызывающая сторона через [modifier].
 *
 * @param modifier [Modifier], применяемый к контейнеру панели.
 * @param content Контент, который будет размещен внутри панели навигации.
 */
@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(HereSize.NavBar.height)
            .navBarShadow(HereShape.navBar)
            .clip(HereShape.navBar)
            .background(HereTheme.colors.surface),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}