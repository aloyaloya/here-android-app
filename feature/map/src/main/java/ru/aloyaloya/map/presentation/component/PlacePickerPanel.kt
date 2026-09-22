package ru.aloyaloya.map.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import ru.aloyaloya.design_system.component.button.HerePrimaryButton
import ru.aloyaloya.design_system.component.button.HereSecondaryButton
import ru.aloyaloya.design_system.extension.sheetShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.map.R

/**
 * Окошко режима выбора места: адрес точки под прицелом и выход из режима.
 *
 * @param address Адрес точки или `null`, если определить его не вышло.
 * @param resolving Идет ли сейчас запрос адреса.
 * @param onCancel Колбэк отмены выбора.
 * @param onConfirm Колбэк подтверждения выбранной точки.
 * @param modifier [Modifier], применяемый к окошку.
 */
@Composable
fun PlacePickerPanel(
    address: String?,
    resolving: Boolean,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors
    val sizes = HereSize.PlacePicker

    Column(
        verticalArrangement = Arrangement.spacedBy(sizes.panelSpacing),
        modifier = modifier
            .fillMaxWidth()
            .sheetShadow(HereShape.card)
            .clip(HereShape.card)
            .background(colors.surface)
            .padding(sizes.panelPadding)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(sizes.addressSpacing)) {
            Text(
                text = when {
                    resolving -> stringResource(R.string.place_picker_searching)
                    address != null -> address
                    else -> stringResource(R.string.place_picker_unknown)
                },
                style = MaterialTheme.typography.titleMedium,
                color = if (resolving) colors.textTertiary else colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = stringResource(R.string.place_picker_hint),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textTertiary
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(sizes.actionSpacing)) {
            HereSecondaryButton(
                text = stringResource(R.string.place_picker_cancel),
                onClick = onCancel,
                height = sizes.actionHeight,
                modifier = Modifier.weight(1f)
            )

            HerePrimaryButton(
                text = stringResource(R.string.place_picker_next),
                onClick = onConfirm,
                height = sizes.actionHeight,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
