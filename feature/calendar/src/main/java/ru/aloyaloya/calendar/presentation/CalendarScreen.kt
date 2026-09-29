package ru.aloyaloya.calendar.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.aloyaloya.calendar.model.CalendarUiState
import ru.aloyaloya.design_system.component.calendar.CalendarDayMark
import ru.aloyaloya.design_system.component.calendar.HereMonthGrid
import ru.aloyaloya.design_system.component.calendar.HereMonthHeader
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.emoji
import java.time.YearMonth

/**
 * Экран календаря: воспоминания по дням месяца.
 *
 * @param uiState Состояние экрана.
 * @param modifier Внешний [Modifier] экрана.
 */
@Composable
fun CalendarScreen(
    uiState: CalendarUiState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HereTheme.colors.background)
    ) {
        when (uiState) {
            CalendarUiState.Loading -> {
                CircularProgressIndicator(
                    color = HereTheme.colors.accent,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is CalendarUiState.Content -> CalendarContent(uiState)
        }
    }
}

/** Месяц с эмоциями в днях. */
@Composable
private fun CalendarContent(uiState: CalendarUiState.Content) {
    var shownMonth by rememberSaveable { mutableStateOf(YearMonth.now()) }

    val markByDate = uiState.emotionByDate.mapValues { (_, emotion) ->
        CalendarDayMark(emoji = emotion.emoji, color = emotion.color.solid)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(HereSpacing.xl),
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                top = HereSize.TopAppBar.height,
                bottom = HereSize.NavBar.height
            )
            .padding(
                horizontal = HereSpacing.screenHorizontal,
                vertical = HereSpacing.s
            )
    ) {
        HereMonthHeader(
            month = shownMonth,
            onPreviousClick = { shownMonth = shownMonth.minusMonths(1) },
            onNextClick = { shownMonth = shownMonth.plusMonths(1) }
        )

        HereMonthGrid(
            month = shownMonth,
            selectedDate = null,
            markByDate = markByDate,
            onDayClick = {}
        )
    }
}
