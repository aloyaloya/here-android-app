package ru.aloyaloya.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.aloyaloya.domain.repository.AddressRepository
import ru.aloyaloya.domain.repository.MemoryRepository
import ru.aloyaloya.map.model.MapUiState
import ru.aloyaloya.map.model.PlacePicking
import ru.aloyaloya.mapkit.model.MapPoint
import ru.aloyaloya.mapkit.model.YandexMapConfig
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val mapConfig: YandexMapConfig,
    private val addressRepository: AddressRepository,
    memoryRepository: MemoryRepository
) : ViewModel() {

    private val picking = MutableStateFlow<PlacePicking?>(null)
    private var addressJob: Job? = null

    val uiState: StateFlow<MapUiState> =
        combine(memoryRepository.observeAll(), picking) { memories, picking ->
            MapUiState.Content(
                mapConfig = mapConfig,
                memories = memories,
                picking = picking
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = MapUiState.Loading
            )

    fun startPicking() {
        picking.value = PlacePicking()
    }

    fun cancelPicking() {
        addressJob?.cancel()
        picking.value = null
    }

    fun onPickPointChanged(point: MapPoint) {
        addressJob?.cancel()
        picking.value = PlacePicking()
        addressJob = viewModelScope.launch {
            val address = addressRepository.resolve(point.latitude, point.longitude)
            picking.update { current ->
                current?.copy(address = address, resolving = false)
            }
        }
    }
}
