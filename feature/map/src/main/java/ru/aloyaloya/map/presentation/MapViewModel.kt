package ru.aloyaloya.map.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.aloyaloya.map.model.MapUiState
import ru.aloyaloya.mapkit.model.YandexMapConfig
import javax.inject.Inject

class MapViewModel @Inject constructor(
    mapConfig: YandexMapConfig
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MapUiState.Content(mapConfig = mapConfig)
    )
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()
}
