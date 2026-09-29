package ru.aloyaloya.design_system.component.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import ru.aloyaloya.design_system.component.emotion.EmotionPin
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * Пин эмоции в дне календаря.
 *
 * @property emoji Эмодзи эмоции.
 * @property color Насыщенный тон эмоции.
 */
@Immutable
data class CalendarDayMark(
    val emoji: String,
    val color: Color
)

/**
 * Шапка недели и сетка дней месяца.
 *
 * @param month Показанный месяц.
 * @param selectedDate Выбранный день или `null`, если не выбран ни один.
 * @param markByDate Пин эмоции для дней с воспоминаниями.
 * @param onDayClick Колбэк нажатия на день.
 * @param modifier Внешний [Modifier] сетки.
 */
@Composable
fun HereMonthGrid(
    month: YearMonth,
    selectedDate: LocalDate?,
    markByDate: Map<LocalDate, CalendarDayMark>,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = remember(month) { monthGrid(month) }
    val today = remember { LocalDate.now() }

    Column(
        verticalArrangement = Arrangement.spacedBy(HereSize.Calendar.gridSpacing),
        modifier = modifier
    ) {
        WeekdayRow()

        days.chunked(DAYS_IN_WEEK).forEach { week ->
            Row(horizontalArrangement = Arrangement.spacedBy(HereSize.Calendar.gridSpacing)) {
                week.forEach { day ->
                    val inShownMonth = YearMonth.from(day) == month

                    DayCell(
                        day = day,
                        /** Дни соседних месяцев по краям сетки — заполнение, а не содержание. */
                        mark = if (inShownMonth) markByDate[day] else null,
                        inShownMonth = inShownMonth,
                        selected = day == selectedDate,
                        today = inShownMonth && day == today,
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
            day.dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.forLanguageTag("ru"))
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

/** Ячейка календаря — круг с числом дня или с пином эмоции. */
@Composable
private fun DayCell(
    day: LocalDate,
    mark: CalendarDayMark?,
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
            .clip(HereShape.pill)
            .then(if (selected) Modifier.background(colors.accent) else Modifier)
            .then(
                if (today && !selected) {
                    Modifier.border(
                        HereSize.Calendar.selectedBorder,
                        colors.accent,
                        HereShape.pill
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
    ) {
        /**
         * В дне с воспоминаниями пин говорит больше числа, поэтому число уступает ему
         * место. Обратно оно возвращается, когда день выбирают: на залитой акцентом
         * плитке пин все равно потерялся бы.
         */
        AnimatedVisibility(
            visible = !selected && mark != null,
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            EmotionPin(
                emoji = mark?.emoji.orEmpty(),
                color = mark?.color ?: Color.Transparent,
                size = HereSize.Calendar.pinSize,
                border = HereSize.Calendar.pinBorder,
                emojiSize = HereSize.Calendar.pinEmojiSize
            )
        }

        AnimatedVisibility(
            visible = selected || mark == null,
            enter = scaleIn(),
            exit = scaleOut()
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
}
