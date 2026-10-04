package ru.aloyaloya.settings.presentation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.settings.R

/**
 * Подтверждение удаления всех воспоминаний.
 *
 * @param memoryCount Сколько воспоминаний будет удалено.
 * @param onConfirmClick Колбэк подтверждения удаления.
 * @param onDismissRequest Колбэк закрытия диалога.
 * @param modifier [Modifier], применяемый к диалогу.
 */
@Composable
fun DeleteAllDialog(
    memoryCount: Int,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        shape = HereShape.dialog,
        containerColor = colors.surface,
        titleContentColor = colors.textPrimary,
        textContentColor = colors.textSecondary,
        title = {
            Text(
                text = stringResource(R.string.settings_delete_all_title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = pluralStringResource(
                    R.plurals.settings_delete_all_message,
                    memoryCount,
                    memoryCount
                ),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmClick) {
                Text(
                    text = stringResource(R.string.settings_delete_all_confirm),
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.danger
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = stringResource(R.string.settings_cancel),
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textSecondary
                )
            }
        }
    )
}
