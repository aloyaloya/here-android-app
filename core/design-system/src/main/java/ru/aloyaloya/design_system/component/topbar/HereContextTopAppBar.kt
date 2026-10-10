package ru.aloyaloya.design_system.component.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.theme.HereSize

/**
 * Верхняя панель режима: стрелка выхода, название экрана и действия справа.
 *
 * @param title Название режима.
 * @param navigationContentDescription Описание выхода для программ чтения с экрана.
 * @param onNavigateBack Колбэк выхода из режима.
 * @param modifier Внешний [Modifier] панели.
 * @param scrolled Уехал ли контент под панель: тогда панель темнеет, отделяясь от него.
 * @param actions Кнопки справа.
 */
@Composable
fun HereContextTopAppBar(
    title: String,
    navigationContentDescription: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    scrolled: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val containerColor by animateTopAppBarContainerColor(scrolled)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .statusBarsPadding()
            .height(HereSize.TopAppBar.height)
            .padding(horizontal = HereSize.TopAppBar.contentPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TopAppBarAction(
            icon = R.drawable.ic_arrow_back,
            contentDescription = navigationContentDescription,
            onClick = onNavigateBack
        )

        TopAppBarTitleSection(
            title = title,
            modifier = Modifier
                .weight(1f)
                .padding(start = HereSize.TopAppBar.contentPadding)
        )

        actions()
    }
}
