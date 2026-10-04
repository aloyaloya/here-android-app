package ru.aloyaloya.design_system.component.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Верхняя панель приложения с заголовком текущего раздела и кнопкой настроек.
 *
 * Панель во всю ширину и без скруглений: это край экрана, а не плашка поверх него.
 * Фон непрозрачный и уходит под статус-бар, поэтому контент под панель не просвечивает.
 *
 * @param title Заголовок текущего раздела.
 * @param onSettingsClick Колбэк нажатия на кнопку настроек.
 * @param modifier Внешний [Modifier] панели.
 */
@Composable
fun TopAppBar(
    title: String,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(HereTheme.colors.background)
            .statusBarsPadding()
            .height(HereSize.TopAppBar.height)
            .padding(
                start = HereSize.TopAppBar.titlePadding,
                end = HereSize.TopAppBar.contentPadding
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TopAppBarTitleSection(
            title = title,
            modifier = Modifier.weight(1f)
        )
        TopAppBarActions(onSettingsClick = onSettingsClick)
    }
}