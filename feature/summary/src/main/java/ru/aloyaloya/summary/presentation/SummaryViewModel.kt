package ru.aloyaloya.summary.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.model.dominantEmotion
import ru.aloyaloya.domain.repository.MemoryRepository
import ru.aloyaloya.summary.model.EmotionShare
import ru.aloyaloya.summary.model.SummaryPeriod
import ru.aloyaloya.summary.model.SummaryUiState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class SummaryViewModel @Inject constructor(
    memoryRepository: MemoryRepository
) : ViewModel() {

    private val period = MutableStateFlow(SummaryPeriod.MONTH)

    val uiState: StateFlow<SummaryUiState> =
        combine(memoryRepository.observeAll(), period) { memories, period ->
            val today = LocalDate.now()
            val inPeriod = memories.filter { memory ->
                period.contains(memory.happenedAt.toLocalDate(), today)
            }

            SummaryUiState.Content(
                period = period,
                memoryCount = inPeriod.size,
                dayCount = inPeriod.distinctBy { it.happenedAt.toLocalDate() }.size,
                dominantEmotion = inPeriod.dominantEmotion(),
                emotionShares = inPeriod
                    .groupingBy(Memory::emotion)
                    .eachCount()
                    .map { (emotion, count) -> EmotionShare(emotion, count) }
                    .sortedByDescending(EmotionShare::count)
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = SummaryUiState.Loading
            )

    fun onPeriodSelected(period: SummaryPeriod) {
        this.period.value = period
    }
}

private fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
