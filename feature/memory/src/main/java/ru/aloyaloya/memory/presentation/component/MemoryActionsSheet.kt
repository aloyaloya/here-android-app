package ru.aloyaloya.memory.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.component.sheet.HereBottomSheet
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.memory.R
import ru.aloyaloya.design_system.R as DesignSystemR

/**
 * Меню действий над воспоминанием.
 *
 * @param onEditClick Колбэк выбора редактирования.
 * @param onDeleteClick Колбэк выбора удаления.
 * @param onDismissRequest Колбэк закрытия листа.
 * @param modifier [Modifier], применяемый к листу.
 */
@Composable
fun MemoryActionsSheet(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    HereBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HereSize.Sheet.horizontalPadding)
                .padding(bottom = HereSize.Sheet.bottomPadding)
        ) {
            MemoryAction(
                icon = DesignSystemR.drawable.ic_edit,
                text = stringResource(R.string.memory_action_edit),
                color = HereTheme.colors.textPrimary,
                onClick = onEditClick
            )

            MemoryAction(
                icon = DesignSystemR.drawable.ic_trash,
                text = stringResource(R.string.memory_action_delete),
                color = HereTheme.colors.danger,
                onClick = onDeleteClick
            )
        }
    }
}

/** Пункт меню: иконка и подпись одним цветом. */
@Composable
private fun MemoryAction(
    @DrawableRes icon: Int,
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HereSize.SheetAction.iconSpacing),
        modifier = modifier
            .fillMaxWidth()
            .height(HereSize.SheetAction.height)
            .clickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(HereSize.SheetAction.iconSize)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
    }
}
