package ru.aloyaloya.memory.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.component.button.HerePrimaryButton
import ru.aloyaloya.design_system.component.button.HereSecondaryButton
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.memory.R

/** Доли ширины кнопок: подтверждение заметно шире отказа. */
private const val CANCEL_WEIGHT = 1f
private const val CONFIRM_WEIGHT = 1.4f

/**
 * Шапка листа выбора: тихая подпись и крупное текущее значение под ней.
 *
 * @param label Подпись листа.
 * @param value Выбранное значение в готовом для показа виде.
 * @param modifier [Modifier], применяемый к шапке.
 */
@Composable
fun SheetTitle(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors

    Column(
        verticalArrangement = Arrangement.spacedBy(HereSize.Sheet.titleSpacing),
        modifier = modifier
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = HereSize.Sheet.titleLabelSize,
                letterSpacing = HereSize.Sheet.titleLabelLetterSpacing
            ),
            color = colors.textTertiary
        )

        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = HereSize.Sheet.titleValueSize
            ),
            color = colors.textPrimary
        )
    }
}

/**
 * Кнопки листа выбора: отказ и подтверждение.
 *
 * @param onCancelClick Колбэк отказа.
 * @param onConfirmClick Колбэк подтверждения.
 * @param modifier [Modifier], применяемый к ряду кнопок.
 */
@Composable
fun SheetActions(
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(HereSize.Sheet.actionSpacing),
        modifier = modifier.fillMaxWidth()
    ) {
        HereSecondaryButton(
            text = stringResource(R.string.sheet_cancel),
            onClick = onCancelClick,
            height = HereSize.Sheet.actionHeight,
            modifier = Modifier.weight(CANCEL_WEIGHT)
        )

        HerePrimaryButton(
            text = stringResource(R.string.sheet_confirm),
            onClick = onConfirmClick,
            height = HereSize.Sheet.actionHeight,
            modifier = Modifier.weight(CONFIRM_WEIGHT)
        )
    }
}