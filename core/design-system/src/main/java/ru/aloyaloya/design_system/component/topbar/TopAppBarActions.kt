package ru.aloyaloya.design_system.component.topbar

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Блок действий в правой части верхней панели.
 *
 * Пока в нём одна кнопка — вход в настройки.
 *
 * @param onSettingsClick Колбэк нажатия на кнопку настроек.
 * @param modifier [Modifier], применяемый к [Row].
 */
@Composable
fun TopAppBarActions(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        TopAppBarAction(
            icon = R.drawable.ic_settings,
            contentDescription = stringResource(R.string.settings_content_description),
            onClick = onSettingsClick
        )
    }
}

/**
 * Кнопка-иконка верхней панели.
 *
 * @param icon Иконка кнопки.
 * @param contentDescription Описание действия для программ чтения с экрана.
 * @param onClick Колбэк нажатия.
 * @param modifier [Modifier], применяемый к кнопке.
 */
@Composable
fun TopAppBarAction(
    @DrawableRes icon: Int,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(HereSize.TopAppBar.actionSize)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(icon),
            tint = HereTheme.colors.textPrimary,
            contentDescription = contentDescription,
            modifier = Modifier.size(HereSize.TopAppBar.actionIconSize)
        )
    }
}
