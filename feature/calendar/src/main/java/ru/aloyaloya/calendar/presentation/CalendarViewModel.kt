package ru.aloyaloya.calendar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.aloyaloya.calendar.model.CalendarUiState
import ru.aloyaloya.domain.repository.MemoryRepository
import javax.inject.Inject

class CalendarViewModel @Inject constructor(
    memoryRepository: MemoryRepository
) : ViewModel() {

    val uiState: StateFlow<CalendarUiState> =
        memoryRepository.observeAll()
            .map { memories -> CalendarUiState.Content(memories) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = CalendarUiState.Loading
            )
}
