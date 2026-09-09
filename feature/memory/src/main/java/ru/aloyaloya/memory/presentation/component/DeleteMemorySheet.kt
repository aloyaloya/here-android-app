package ru.aloyaloya.memory.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.component.button.HereDangerButton
import ru.aloyaloya.design_system.component.button.HereSecondaryButton
import ru.aloyaloya.design_system.component.sheet.HereBottomSheet
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.memory.R

/** Знак в квадрате-иконке: рисуется текстом, отдельной иконки под него не нужно. */
private const val WARNING_SYMBOL = "!"

/**
 * Подтверждение удаления воспоминания.
 *
 * @param title Название воспоминания, которое собираются удалить.
 * @param onConfirmClick Колбэк подтверждения удаления.
 * @param onDismissRequest Колбэк закрытия листа.
 * @param modifier [Modifier], применяемый к листу.
 */
@Composable
fun DeleteMemorySheet(
    title: String,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors

    HereBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HereSize.Sheet.contentSpacing),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HereSize.Sheet.horizontalPadding)
                .padding(bottom = HereSize.Sheet.bottomPadding)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(HereSize.ConfirmSheet.iconSize)
                    .clip(HereShape.card)
                    .background(colors.dangerContainer)
            ) {
                Text(
                    text = WARNING_SYMBOL,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = HereSize.ConfirmSheet.iconSymbolSize
                    ),
                    color = colors.danger
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(HereSize.ConfirmSheet.contentSpacing)
            ) {
                Text(
                    text = stringResource(R.string.memory_delete_title, title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.textPrimary
                )

                Text(
                    text = stringResource(R.string.memory_delete_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(HereSize.ConfirmSheet.actionSpacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                HereDangerButton(
                    text = stringResource(R.string.memory_delete_confirm),
                    onClick = onConfirmClick,
                    height = HereSize.Sheet.actionHeight
                )

                HereSecondaryButton(
                    text = stringResource(R.string.sheet_cancel),
                    onClick = onDismissRequest,
                    height = HereSize.Sheet.actionHeight
                )
            }
        }
    }
}
