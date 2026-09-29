package ru.aloyaloya.memory.presentation.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.memory.R

/**
 * Подтверждение удаления воспоминания.
 *
 * @param title Название воспоминания, которое собираются удалить.
 * @param onConfirmClick Колбэк подтверждения удаления.
 * @param onDismissRequest Колбэк закрытия диалога.
 * @param modifier [Modifier], применяемый к диалогу.
 */
@Composable
fun DeleteMemoryDialog(
    title: String,
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
                text = stringResource(R.string.memory_delete_title, title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = stringResource(R.string.memory_delete_message),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmClick) {
                Text(
                    text = stringResource(R.string.memory_delete_confirm),
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.danger
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = stringResource(R.string.sheet_cancel),
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textSecondary
                )
            }
        }
    )
}
