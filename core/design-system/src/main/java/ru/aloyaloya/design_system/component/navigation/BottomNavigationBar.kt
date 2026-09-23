package ru.aloyaloya.design_system.component.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Нижняя панель навигации приложения Here.
 *
 * Панель прижата к низу экрана во всю ширину, без скруглений и тени.
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
            .background(HereTheme.colors.surface)
            .navigationBarsPadding()
            .height(HereSize.NavBar.height),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}