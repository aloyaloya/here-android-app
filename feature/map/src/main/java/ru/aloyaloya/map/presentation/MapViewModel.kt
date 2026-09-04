package ru.aloyaloya.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.repository.AddressRepository
import ru.aloyaloya.domain.repository.MemoryRepository
import ru.aloyaloya.map.model.MapUiState
import ru.aloyaloya.map.model.SelectedMemory
import ru.aloyaloya.mapkit.model.YandexMapConfig
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val mapConfig: YandexMapConfig,
    private val addressRepository: AddressRepository,
    memoryRepository: MemoryRepository
) : ViewModel() {

    private val memories = MutableStateFlow<List<Memory>?>(null)

    private val selectedId = MutableStateFlow<Long?>(null)

    private val addresses = MutableStateFlow(emptyMap<Long, String>())

    val uiState: StateFlow<MapUiState> = combine(
        memories,
        selectedId,
        addresses
    ) { memories, selectedId, addresses ->
        if (memories == null) {
            MapUiState.Loading
        } else {
            val selected = memories
                .firstOrNull { memory -> memory.id == selectedId }
                ?.let { memory ->
                    SelectedMemory(memory = memory, address = addresses[memory.id])
                }

            MapUiState.Content(
                mapConfig = mapConfig,
                memories = memories,
                selected = selected
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MapUiState.Loading
    )

    init {
        viewModelScope.launch {
            memoryRepository.observeAll().collect { memories.value = it }
        }
    }

    fun onMarkerClick(memoryId: Long) {
        selectedId.value = memoryId

        val memory = memories.value?.firstOrNull { it.id == memoryId } ?: return
        if (addresses.value.containsKey(memoryId)) return

        viewModelScope.launch {
            val address = addressRepository.resolve(
                latitude = memory.latitude,
                longitude = memory.longitude
            ) ?: return@launch

            addresses.update { it + (memoryId to address) }
        }
    }

    fun onMemorySheetDismiss() {
        selectedId.value = null
    }
}
