package ru.aloyaloya.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.aloyaloya.domain.repository.MemoryRepository
import ru.aloyaloya.map.model.MapUiState
import ru.aloyaloya.mapkit.model.YandexMapConfig
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val mapConfig: YandexMapConfig,
    memoryRepository: MemoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Loading)
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            memoryRepository.observeAll().collect { memories ->
                _uiState.value = MapUiState.Content(
                    mapConfig = mapConfig,
                    memories = memories
                )
            }
        }
    }
}
