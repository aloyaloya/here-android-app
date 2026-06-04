package ru.aloyaloya.design_system.component.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import ru.aloyaloya.design_system.R

/**
 * Верхняя панель приложения с заголовком текущего раздела и action-кнопкой темы.
 *
 * @param painter Иконка текущего раздела.
 * @param title Заголовок текущего раздела.
 * @param darkTheme Текущее состояние темы для анимированного switcher'а.
 * @param onThemeChange Колбэк переключения темы.
 * @param onOptionsNavigate Зарезервированный колбэк для дополнительных действий.
 * @param modifier Внешний [Modifier] панели.
 */
@Composable
fun TopAppBar(
    painter: Painter,
    title: String,
    darkTheme: Boolean,
    onThemeChange: () -> Unit,
    onOptionsNavigate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .statusBarsPadding()
            .padding(horizontal = dimensionResource(R.dimen.medium_padding))
            .padding(
                top = dimensionResource(R.dimen.small_padding),
                bottom = dimensionResource(R.dimen.medium_padding)
            )
            .background(MaterialTheme.colorScheme.primary),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TopAppBarTitleSection(
            painter = painter,
            title = title,
            modifier = Modifier.weight(1f)
        )
        TopAppBarActions(
            darkTheme = darkTheme,
            onThemeChange = onThemeChange,
            onOptionsNavigate = onOptionsNavigate
        )
    }
}