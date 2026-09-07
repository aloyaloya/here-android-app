package ru.aloyaloya.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.aloyaloya.domain.repository.MemoryRepository
import ru.aloyaloya.map.model.MapUiState
import ru.aloyaloya.mapkit.model.YandexMapConfig
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val mapConfig: YandexMapConfig,
    memoryRepository: MemoryRepository
) : ViewModel() {

    val uiState: StateFlow<MapUiState> = memoryRepository.observeAll()
        .map { memories ->
            MapUiState.Content(
                mapConfig = mapConfig,
                memories = memories
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MapUiState.Loading
        )
}
