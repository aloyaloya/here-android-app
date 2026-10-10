package ru.aloyaloya.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.repository.MemoryRepository
import ru.aloyaloya.map.model.MapUiState
import ru.aloyaloya.mapkit.model.YandexMapConfig
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val mapConfig: YandexMapConfig,
    memoryRepository: MemoryRepository
) : ViewModel() {

    private var knownIds: Set<Long>? = null
    private val newMemoryId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<MapUiState> =
        combine(
            memoryRepository.observeAll().onEach(::detectNewMemory),
            newMemoryId
        ) { memories, newId ->
            MapUiState.Content(
                mapConfig = mapConfig,
                memories = memories,
                newMemory = memories.find { it.id == newId }
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = MapUiState.Loading
            )

    fun onNewMemoryShown() {
        newMemoryId.value = null
    }

    private fun detectNewMemory(memories: List<Memory>) {
        val ids = memories.map { it.id }.toSet()
        val added = knownIds?.let { known -> ids - known }.orEmpty()

        if (added.size == 1) newMemoryId.value = added.single()
        knownIds = ids
    }
}
