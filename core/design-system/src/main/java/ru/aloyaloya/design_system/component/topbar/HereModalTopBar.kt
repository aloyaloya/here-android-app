package ru.aloyaloya.design_system.component.topbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
 * Панель модального экрана: крестик и заголовок слева.
 *
 * В отличие от [TopAppBar] панель встроена в поток экрана и без фона.
 *
 * @param title Заголовок экрана.
 * @param onCloseClick Колбэк закрытия экрана.
 * @param modifier Внешний [Modifier] панели.
 */
@Composable
fun HereModalTopBar(
    title: String,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HereSize.ModalTopBar.titleSpacing),
        modifier = modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = HereSize.ModalTopBar.horizontalPadding)
            .padding(
                top = HereSize.ModalTopBar.topPadding,
                bottom = HereSize.ModalTopBar.bottomPadding
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(HereSize.ModalTopBar.closeSize)
                .clip(CircleShape)
                .clickable(onClick = onCloseClick)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.modal_close_content_description),
                tint = HereTheme.colors.textPrimary,
                modifier = Modifier.size(HereSize.ModalTopBar.closeIconSize)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = HereTheme.colors.textPrimary
        )
    }
}
