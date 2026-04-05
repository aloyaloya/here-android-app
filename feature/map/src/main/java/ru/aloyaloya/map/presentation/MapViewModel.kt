package ru.aloyaloya.map.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.aloyaloya.mapkit.model.YandexMapConfig
import javax.inject.Inject

// TODO: последняя камера из БД вместо дефолтного конфига
data class MapUiState(
    val isLoading: Boolean = false,
    val mapConfig: YandexMapConfig,
)

class MapViewModel @Inject constructor(
    mapConfig: YandexMapConfig,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState(mapConfig = mapConfig))
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()
}
