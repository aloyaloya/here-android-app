package ru.aloyaloya.memory.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import ru.aloyaloya.design_system.component.sheet.HereBottomSheet
import ru.aloyaloya.design_system.extension.calendarDayShadow
import ru.aloyaloya.design_system.extension.cardShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.memory.R
import ru.aloyaloya.memory.model.DAYS_IN_WEEK
import ru.aloyaloya.memory.model.monthGrid
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val RussianLocale = Locale.forLanguageTag("ru")
private val FullDateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy", RussianLocale)
private val MonthFormat = DateTimeFormatter.ofPattern("LLLL yyyy", RussianLocale)

/**
 * Лист выбора даты события.
 *
 * Пока лист открыт, выбранный день и показанный месяц живут только внутри него:
 * наружу дата уходит одним событием по кнопке подтверждения. Закрытие любым другим
 * способом ничего не меняет.
 *
 * @param initialDate Дата, с которой лист открывается.
 * @param onDismissRequest Колбэк закрытия листа без выбора.
 * @param onDateSelected Колбэк подтвержденной даты.
 */
@Composable
fun DateSheet(
    initialDate: LocalDate,
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    var selectedDate by rememberSaveable { mutableStateOf(initialDate) }
    var shownMonth by rememberSaveable { mutableStateOf(YearMonth.from(initialDate)) }

    HereBottomSheet(onDismissRequest = onDismissRequest) {
        Column(
            verticalArrangement = Arrangement.spacedBy(HereSize.Sheet.contentSpacing),
            modifier = Modifier.padding(
                start = HereSize.Sheet.horizontalPadding,
                end = HereSize.Sheet.horizontalPadding,
                bottom = HereSize.Sheet.bottomPadding
            )
        ) {
            SheetTitle(
                label = stringResource(R.string.date_sheet_label),
                value = FullDateFormat.format(selectedDate)
            )

            MonthHeader(
                month = shownMonth,
                onPreviousClick = { shownMonth = shownMonth.minusMonths(1) },
                onNextClick = { shownMonth = shownMonth.plusMonths(1) }
            )

            MonthGrid(
                month = shownMonth,
                selectedDate = selectedDate,
                onDayClick = { selectedDate = it }
            )

            SheetActions(
                onCancelClick = onDismissRequest,
                onConfirmClick = { onDateSelected(selectedDate) }
            )
        }
    }
}

/** Название месяца и стрелки перелистывания. */
@Composable
private fun MonthHeader(
    month: YearMonth,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = MonthFormat.format(month.atDay(1)).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = HereSize.Calendar.monthSize
            ),
            color = HereTheme.colors.textPrimary
        )

        Row(horizontalArrangement = Arrangement.spacedBy(HereSize.Calendar.navButtonSpacing)) {
            MonthNavButton(text = "‹", onClick = onPreviousClick)
            MonthNavButton(text = "›", onClick = onNextClick)
        }
    }
}

/** Круглая кнопка перелистывания месяца. */
@Composable
private fun MonthNavButton(
    text: String,
    onClick: () -> Unit
) {
    val colors = HereTheme.colors

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(HereSize.Calendar.navButtonSize)
            .cardShadow(HereShape.pill)
            .clip(HereShape.pill)
            .background(colors.surface)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = colors.textPrimary
        )
    }
}

/** Шапка недели и сетка дней месяца. */
@Composable
private fun MonthGrid(
    month: YearMonth,
    selectedDate: LocalDate,
    onDayClick: (LocalDate) -> Unit
) {
    val days = remember(month) { monthGrid(month) }
    val today = remember { LocalDate.now() }

    Column(verticalArrangement = Arrangement.spacedBy(HereSize.Calendar.gridSpacing)) {
        WeekdayRow()

        days.chunked(DAYS_IN_WEEK).forEach { week ->
            Row(horizontalArrangement = Arrangement.spacedBy(HereSize.Calendar.gridSpacing)) {
                week.forEach { day ->
                    DayCell(
                        day = day,
                        inShownMonth = YearMonth.from(day) == month,
                        selected = day == selectedDate,
                        today = day == today,
                        onClick = { onDayClick(day) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/** Строка с сокращенными названиями дней недели. */
@Composable
private fun WeekdayRow() {
    val weekdays = remember {
        monthGrid(YearMonth.of(2024, 1)).take(DAYS_IN_WEEK).map { day ->
            day.dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE, RussianLocale)
        }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(HereSize.Calendar.gridSpacing)) {
        weekdays.forEach { weekday ->
            Text(
                text = weekday,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = HereSize.Calendar.weekdaySize
                ),
                color = HereTheme.colors.textTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/** Ячейка календаря — круг с числом. */
@Composable
private fun DayCell(
    day: LocalDate,
    inShownMonth: Boolean,
    selected: Boolean,
    today: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors

    val textColor = when {
        selected -> colors.onAccent
        !inShownMonth -> colors.textQuaternary
        today -> colors.accent
        else -> colors.textPrimary
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .then(if (selected) Modifier.calendarDayShadow(HereShape.pill) else Modifier)
            .clip(HereShape.pill)
            .then(if (selected) Modifier.background(colors.accent) else Modifier)
            .then(
                if (today && !selected) {
                    Modifier.border(HereSize.Calendar.selectedBorder, colors.accent, HereShape.pill)
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = HereSize.Calendar.daySize,
                fontWeight = if (selected || today) FontWeight.Bold else FontWeight.SemiBold
            ),
            color = textColor
        )
    }
}