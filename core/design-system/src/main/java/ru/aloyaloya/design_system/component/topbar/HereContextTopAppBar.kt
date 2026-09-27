package ru.aloyaloya.design_system.component.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Верхняя панель режима: стрелка выхода и название того, что сейчас происходит.
 *
 * Пока приложение в режиме, эта панель подменяет обычную [TopAppBar]. Так заголовок
 * перестает врать про раздел, а выход оказывается там, где его ищут в Android, —
 * слева вверху, а не кнопкой поверх содержимого.
 *
 * Кнопка выхода занимает 48dp при иконке в 24dp: нажимать удобно, а от края панель
 * отступает так, что иконка оптически садится на те же 16dp, что и заголовок обычной панели.
 *
 * @param title Название режима.
 * @param navigationContentDescription Описание выхода для программ чтения с экрана.
 * @param onNavigateBack Колбэк выхода из режима.
 * @param modifier Внешний [Modifier] панели.
 */
@Composable
fun HereContextTopAppBar(
    title: String,
    navigationContentDescription: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .statusBarsPadding()
            .height(HereSize.TopAppBar.height)
            .padding(horizontal = HereSize.TopAppBar.contentPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(HereSize.TopAppBar.actionSize)
                .clip(CircleShape)
                .clickable(onClick = onNavigateBack)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                tint = colors.textPrimary,
                contentDescription = navigationContentDescription,
                modifier = Modifier.size(HereSize.TopAppBar.actionIconSize)
            )
        }

        TopAppBarTitleSection(
            title = title,
            modifier = Modifier.padding(start = HereSize.TopAppBar.contentPadding)
        )
    }
}
