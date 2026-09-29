package ru.aloyaloya.calendar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.aloyaloya.calendar.model.CalendarUiState
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.repository.MemoryRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class CalendarViewModel @Inject constructor(
    memoryRepository: MemoryRepository
) : ViewModel() {

    val uiState: StateFlow<CalendarUiState> =
        memoryRepository.observeAll()
            .map { memories ->
                val emotionByDate = memories
                    .groupBy { memory -> memory.happenedAt.toLocalDate() }
                    .mapValues { (_, dayMemories) ->
                        dayMemories.maxBy(Memory::happenedAt).emotion
                    }

                CalendarUiState.Content(emotionByDate)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = CalendarUiState.Loading
            )
}

private fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
