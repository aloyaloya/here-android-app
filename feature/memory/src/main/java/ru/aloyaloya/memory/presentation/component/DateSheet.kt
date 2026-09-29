package ru.aloyaloya.memory.presentation.component

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import ru.aloyaloya.design_system.component.emotion.EmotionPin
import ru.aloyaloya.design_system.component.sheet.HereBottomSheet
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.memory.R
import ru.aloyaloya.memory.model.DAYS_IN_WEEK
import ru.aloyaloya.memory.model.monthGrid
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.emoji
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import ru.aloyaloya.design_system.R as DesignSystemR

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
                emotionByDate = emotionByDate,
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
            text = MonthFormat.format(
                month.atDay(1)
            ).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = HereSize.Calendar.monthSize
            ),
            color = HereTheme.colors.textPrimary
        )

        Row(horizontalArrangement = Arrangement.spacedBy(HereSize.Calendar.navButtonSpacing)) {
            MonthNavButton(
                icon = DesignSystemR.drawable.ic_chevron_left,
                contentDescription = stringResource(R.string.date_sheet_previous_month),
                onClick = onPreviousClick
            )
            MonthNavButton(
                icon = DesignSystemR.drawable.ic_chevron_right,
                contentDescription = stringResource(R.string.date_sheet_next_month),
                onClick = onNextClick
            )
        }
    }
}

/** Круглая кнопка перелистывания месяца. */
@Composable
private fun MonthNavButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    val colors = HereTheme.colors

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(HereSize.Calendar.navButtonSize)
            .clip(HereShape.pill)
            .background(colors.surfaceMuted)
            .clickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(icon),
            tint = colors.textPrimary,
            contentDescription = contentDescription,
            modifier = Modifier.size(HereSize.Calendar.navIconSize)
        )
    }
}

/** Шапка недели и сетка дней месяца. */
@Composable
private fun MonthGrid(
    month: YearMonth,
    selectedDate: LocalDate,
    emotionByDate: Map<LocalDate, Emotion>,
    onDayClick: (LocalDate) -> Unit
) {
    val days = remember(month) { monthGrid(month) }
    val today = remember { LocalDate.now() }

    Column(verticalArrangement = Arrangement.spacedBy(HereSize.Calendar.gridSpacing)) {
        WeekdayRow()

        days.chunked(DAYS_IN_WEEK).forEach { week ->
            Row(horizontalArrangement = Arrangement.spacedBy(HereSize.Calendar.gridSpacing)) {
                week.forEach { day ->
                    val inShownMonth = YearMonth.from(day) == month

                    DayCell(
                        day = day,
                        /** Дни соседних месяцев по краям сетки — заполнение, а не содержание. */
                        emotion = if (inShownMonth) emotionByDate[day] else null,
                        inShownMonth = inShownMonth,
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

/** Ячейка календаря — круг с числом дня или с пином эмоции его последнего воспоминания. */
@Composable
private fun DayCell(
    day: LocalDate,
    emotion: Emotion?,
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
            visible = !selected && emotion != null,
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            EmotionPin(
                emoji = emotion?.emoji.orEmpty(),
                color = emotion?.color?.solid ?: Color.Transparent,
                size = HereSize.Calendar.pinSize,
                border = HereSize.Calendar.pinBorder,
                emojiSize = HereSize.Calendar.pinEmojiSize
            )
        }

        AnimatedVisibility(
            visible = selected || emotion == null,
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