package ru.aloyaloya.calendar.model

import ru.aloyaloya.domain.model.Memory

/**
 * Состояние экрана [ru.aloyaloya.calendar.presentation.CalendarScreen].
 */
sealed class CalendarUiState {
    data object Loading : CalendarUiState()
    data class Content(val memories: List<Memory>) : CalendarUiState()
}
