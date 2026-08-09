package ru.aloyaloya.design_system.component.topbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Панель модального экрана: заголовок по центру и текстовые действия по краям.
 *
 * В отличие от [TopAppBar] панель встроена в поток экрана и без фона: под ней идет
 * контент, который скроллится, а не карта.
 *
 * @param title Заголовок экрана.
 * @param modifier Внешний [Modifier] панели.
 * @param navigation Действие слева, обычно отмена.
 * @param action Действие справа, обычно подтверждение.
 */
@Composable
fun HereModalTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigation: @Composable () -> Unit = {},
    action: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = HereSize.ModalTopBar.horizontalPadding)
            .padding(
                top = HereSize.ModalTopBar.topPadding,
                bottom = HereSize.ModalTopBar.bottomPadding
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navigation()
            action()
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = HereTheme.colors.textPrimary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

/**
 * Текстовое действие в [HereModalTopBar].
 *
 * @param text Подпись действия.
 * @param onClick Колбэк нажатия.
 * @param modifier [Modifier], применяемый к подписи.
 * @param accent Выделять ли действие акцентным цветом. Так помечается основное действие.
 * @param enabled Активно ли действие.
 */
@Composable
fun HereModalTopBarAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Boolean = false,
    enabled: Boolean = true
) {
    val colors = HereTheme.colors

    val color = when {
        !enabled -> colors.textTertiary
        accent -> colors.accent
        else -> colors.textSecondary
    }

    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = color,
        modifier = modifier.clickable(enabled = enabled, onClick = onClick)
    )
}
