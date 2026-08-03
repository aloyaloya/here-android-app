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
import androidx.compose.ui.draw.clip
import ru.aloyaloya.design_system.extension.headerShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/** Прозрачность панели: контент под ней слегка просвечивает. */
private const val SURFACE_ALPHA = 0.94f

/**
 * Верхняя панель приложения с заголовком текущего раздела и action-кнопкой темы.
 *
 * Панель не встроена в поток экрана, а лежит поверх контента, поэтому у неё
 * скругление по краям, полупрозрачный фон и тень.
 *
 * @param title Заголовок текущего раздела.
 * @param darkTheme Текущее состояние темы для анимированного switcher'а.
 * @param onThemeChange Колбэк переключения темы.
 * @param modifier Внешний [Modifier] панели.
 */
@Composable
fun TopAppBar(
    title: String,
    darkTheme: Boolean,
    onThemeChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .statusBarsPadding()
            .padding(
                horizontal = HereSize.TopAppBar.horizontalMargin,
                vertical = HereSize.TopAppBar.topMargin
            )
            .fillMaxWidth()
            .height(HereSize.TopAppBar.height)
            .headerShadow(HereShape.pill)
            .clip(HereShape.pill)
            .background(HereTheme.colors.surface.copy(alpha = SURFACE_ALPHA))
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
        TopAppBarActions(
            darkTheme = darkTheme,
            onThemeChange = onThemeChange
        )
    }
}