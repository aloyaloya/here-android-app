package ru.aloyaloya.design_system.component.topbar

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.theme.HereSize

/**
 * Верхняя панель режима: значок выхода, название экрана и действия справа.
 *
 * @param title Название режима.
 * @param navigationContentDescription Описание выхода для программ чтения с экрана.
 * @param onNavigateBack Колбэк выхода из режима.
 * @param modifier Внешний [Modifier] панели.
 * @param navigationIcon Значок выхода: стрелка назад или крестик модального экрана.
 * @param scrolled Уехал ли контент под панель: тогда панель темнеет, отделяясь от него.
 * @param overMedia Панель поверх снимка: прозрачная и с белыми значками, пока контент не уехал.
 * @param actions Кнопки справа, цвет значков — из [LocalContentColor].
 */
@Composable
fun HereContextTopAppBar(
    title: String,
    navigationContentDescription: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes navigationIcon: Int = R.drawable.ic_arrow_back,
    scrolled: Boolean = false,
    overMedia: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val containerColor by animateTopAppBarContainerColor(scrolled, overMedia)
    val contentColor by animateTopAppBarContentColor(scrolled, overMedia)

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
            icon = navigationIcon,
            contentDescription = navigationContentDescription,
            onClick = onNavigateBack,
            tint = contentColor
        )

        TopAppBarTitleSection(
            title = title,
            color = contentColor,
            modifier = Modifier
                .weight(1f)
                .padding(start = HereSize.TopAppBar.contentPadding)
        )

        CompositionLocalProvider(LocalContentColor provides contentColor) {
            actions()
        }
    }
}
