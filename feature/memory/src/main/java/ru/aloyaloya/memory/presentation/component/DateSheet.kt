package ru.aloyaloya.memory.presentation.component

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
import ru.aloyaloya.design_system.component.calendar.CalendarDayMark
import ru.aloyaloya.design_system.component.calendar.HereMonthGrid
import ru.aloyaloya.design_system.component.calendar.HereMonthHeader
import ru.aloyaloya.design_system.component.sheet.HereBottomSheet
import ru.aloyaloya.design_system.component.sheet.HereSheetActions
import ru.aloyaloya.design_system.component.sheet.HereSheetTitle
import ru.aloyaloya.design_system.format.FullDateFormat
import ru.aloyaloya.design_system.format.currentLocale
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.memory.R
import ru.aloyaloya.ui.emotion.character
import java.time.LocalDate
import java.time.YearMonth

/**
 * Лист выбора даты события.
 *
 * Пока лист открыт, выбранный день и показанный месяц живут только внутри него:
 * наружу дата уходит одним событием по кнопке подтверждения. Закрытие любым другим
 * способом ничего не меняет.
 *
 * @param initialDate Дата, с которой лист открывается.
 * @param emotionByDate Эмоция последнего воспоминания каждого дня.
 * @param onDismissRequest Колбэк закрытия листа без выбора.
 * @param onDateSelected Колбэк подтвержденной даты.
 */
@Composable
fun DateSheet(
    initialDate: LocalDate,
    emotionByDate: Map<LocalDate, Emotion>,
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val fullDateFormat = FullDateFormat.withLocale(currentLocale())

    var selectedDate by rememberSaveable { mutableStateOf(initialDate) }
    var shownMonth by rememberSaveable { mutableStateOf(YearMonth.from(initialDate)) }
    val markByDate = emotionByDate.mapValues { (_, emotion) ->
        CalendarDayMark(character = emotion.character)
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
                label = stringResource(R.string.date_sheet_label),
                value = fullDateFormat.format(selectedDate)
            )

            HereMonthHeader(
                month = shownMonth,
                onPreviousClick = { shownMonth = shownMonth.minusMonths(1) },
                onNextClick = { shownMonth = shownMonth.plusMonths(1) }
            )

            HereMonthGrid(
                month = shownMonth,
                selectedDate = selectedDate,
                markByDate = markByDate,
                onDayClick = { selectedDate = it }
            )

            HereSheetActions(
                onCancelClick = onDismissRequest,
                onConfirmClick = { onDateSelected(selectedDate) }
            )
        }
    }
}
