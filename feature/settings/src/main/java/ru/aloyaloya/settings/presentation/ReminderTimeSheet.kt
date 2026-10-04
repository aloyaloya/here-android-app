package ru.aloyaloya.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.component.picker.HereTimeWheel
import ru.aloyaloya.design_system.component.sheet.HereBottomSheet
import ru.aloyaloya.design_system.component.sheet.HereSheetActions
import ru.aloyaloya.design_system.component.sheet.HereSheetTitle
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.settings.R
import java.time.LocalTime

private const val MINUTE_STEP = 5

/**
 * Лист выбора времени напоминания.
 *
 * @param initialTime Время, с которого лист открывается.
 * @param onDismissRequest Колбэк закрытия листа без выбора.
 * @param onTimeSelected Колбэк подтвержденного времени.
 */
@Composable
fun ReminderTimeSheet(
    initialTime: LocalTime,
    onDismissRequest: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    var selectedTime by rememberSaveable {
        mutableStateOf(
            LocalTime.of(
                initialTime.hour,
                initialTime.minute / MINUTE_STEP * MINUTE_STEP
            )
        )
    }

    HereBottomSheet(onDismissRequest = onDismissRequest) {
        Column(
            verticalArrangement = Arrangement.spacedBy(HereSize.Sheet.contentSpacing),
            modifier = Modifier.padding(
                start = HereSize.Sheet.horizontalPadding,
                end = HereSize.Sheet.horizontalPadding,
                bottom = HereSize.Sheet.bottomPadding
            )
        ) {
            HereSheetTitle(
                label = stringResource(R.string.settings_reminder_time_title),
                value = selectedTime.format(ReminderTimeFormat)
            )

            HereTimeWheel(
                time = selectedTime,
                onTimeChange = { selectedTime = it },
                minuteStep = MINUTE_STEP
            )

            HereSheetActions(
                onCancelClick = onDismissRequest,
                onConfirmClick = { onTimeSelected(selectedTime) }
            )
        }
    }
}
