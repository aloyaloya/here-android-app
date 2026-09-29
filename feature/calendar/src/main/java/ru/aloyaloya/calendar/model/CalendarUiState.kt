package ru.aloyaloya.calendar.model

import ru.aloyaloya.domain.model.Emotion
import java.time.LocalDate

/**
 * Состояние экрана [ru.aloyaloya.calendar.presentation.CalendarScreen].
 */
sealed class CalendarUiState {
    data object Loading : CalendarUiState()
    data class Content(val emotionByDate: Map<LocalDate, Emotion>) : CalendarUiState()
}
