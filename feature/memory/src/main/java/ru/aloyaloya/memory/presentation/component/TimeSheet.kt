package ru.aloyaloya.memory.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import ru.aloyaloya.design_system.component.picker.HereTimeWheel
import ru.aloyaloya.design_system.component.sheet.HereBottomSheet
import ru.aloyaloya.design_system.component.sheet.HereSheetActions
import ru.aloyaloya.design_system.component.sheet.HereSheetTitle
import ru.aloyaloya.design_system.format.TimeFormat
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.memory.R
import java.time.LocalTime

/** Шаг барабана минут: до минуты воспоминание уточнять незачем. */
private const val MINUTE_STEP = 10

/** Время, которое предлагают чипы. */
private val QuickTimes = listOf(
    LocalTime.of(9, 0),
    LocalTime.of(13, 0),
    LocalTime.of(21, 0)
)

/**
 * Лист выбора времени события.
 *
 * Как и лист даты, наружу отдает значение только по кнопке подтверждения.
 *
 * Минуты крутятся с шагом [MINUTE_STEP], поэтому время открытия округляется вниз:
 * барабан не умеет показать значение, которого в нем нет.
 *
 * @param initialTime Время, с которого лист открывается.
 * @param onDismissRequest Колбэк закрытия листа без выбора.
 * @param onTimeSelected Колбэк подтвержденного времени.
 */
@Composable
fun TimeSheet(
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
                label = stringResource(R.string.time_sheet_label),
                value = TimeFormat.format(selectedTime)
            )

            HereTimeWheel(
                time = selectedTime,
                onTimeChange = { selectedTime = it },
                minuteStep = MINUTE_STEP
            )

            QuickTimeChips(
                selectedTime = selectedTime,
                onTimeClick = { selectedTime = it }
            )

            HereSheetActions(
                onCancelClick = onDismissRequest,
                onConfirmClick = { onTimeSelected(selectedTime) }
            )
        }
    }
}

/** Чипы частого времени */
@Composable
private fun QuickTimeChips(
    selectedTime: LocalTime,
    onTimeClick: (LocalTime) -> Unit
) {
    val now = remember { LocalTime.now() }
    val roundedNow =
        remember(now) { LocalTime.of(now.hour, now.minute / MINUTE_STEP * MINUTE_STEP) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(HereSize.TimeWheel.chipSpacing),
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        QuickTimeChip(
            text = stringResource(R.string.time_sheet_now),
            selected = selectedTime == roundedNow,
            onClick = { onTimeClick(roundedNow) }
        )

        QuickTimes.forEach { time ->
            QuickTimeChip(
                text = TimeFormat.format(time),
                selected = selectedTime == time,
                onClick = { onTimeClick(time) }
            )
        }
    }
}

/** Один чип частого времени. */
@Composable
private fun QuickTimeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = HereTheme.colors

    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            fontSize = HereSize.TimeWheel.chipSize,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
        ),
        color = if (selected) colors.accent else colors.textSecondary,
        modifier = Modifier
            .clip(HereShape.tile)
            .background(if (selected) colors.accentContainer else colors.surfaceMuted)
            .clickable(onClick = onClick)
            .padding(
                vertical = HereSize.TimeWheel.chipVerticalPadding,
                horizontal = HereSize.TimeWheel.chipHorizontalPadding
            )
    )
}
