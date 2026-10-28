package ru.aloyaloya.memory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.model.MemoryMedia
import ru.aloyaloya.domain.repository.AddressRepository
import ru.aloyaloya.domain.repository.MemoryRepository
import ru.aloyaloya.memory.model.MemorySheet
import ru.aloyaloya.memory.model.MemoryUiState
import javax.inject.Inject

/** Радиус «здесь же»: как у мест в итогах. */
private const val NEARBY_RADIUS_METERS = 150f

class MemoryViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository,
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MemoryUiState>(MemoryUiState.Loading)
    val uiState: StateFlow<MemoryUiState> = _uiState.asStateFlow()

    private var memoryId: Long? = null

    private var addressRequested = false

    fun setMemoryId(memoryId: Long) {
        if (this.memoryId != null) return
        this.memoryId = memoryId

        viewModelScope.launch {
            combine(
                memoryRepository.observeById(memoryId),
                memoryRepository.observeNearby(memoryId, NEARBY_RADIUS_METERS)
            ) { memory, nearby -> memory to nearby }.collect { (memory, nearby) ->
                _uiState.update { state ->
                    when {
                        memory == null -> MemoryUiState.NotFound
                        state is MemoryUiState.Content -> state.copy(memory = memory, nearby = nearby)
                        else -> MemoryUiState.Content(memory = memory, nearby = nearby)
                    }
                }
                if (memory != null) resolveAddress(memory)
            }
        }
    }

    fun onDeleteClick() = showSheet(MemorySheet.DELETE)

    fun onSheetDismiss() = showSheet(null)

    fun onMediaClick(index: Int) = showMedia(index)

    fun onViewerDismiss() = showMedia(null)

    fun onMediaPicked(picked: List<MemoryMedia>) {
        val memory = (_uiState.value as? MemoryUiState.Content)?.memory ?: return
        if (picked.isEmpty()) return

        viewModelScope.launch {
            memoryRepository.update(memory.copy(media = memory.media + picked))
        }
    }

    fun onDeleteConfirm() {
        val memory = (_uiState.value as? MemoryUiState.Content)?.memory ?: return

        viewModelScope.launch {
            memoryRepository.delete(memory)
        }
    }

    private fun showMedia(index: Int?) {
        _uiState.update { state ->
            if (state is MemoryUiState.Content) state.copy(viewedMedia = index) else state
        }
    }

    private fun showSheet(sheet: MemorySheet?) {
        _uiState.update { state ->
            if (state is MemoryUiState.Content) state.copy(activeSheet = sheet) else state
        }
    }

    private fun resolveAddress(memory: Memory) {
        if (addressRequested) return
        addressRequested = true

        viewModelScope.launch {
            val address = addressRepository.resolve(
                latitude = memory.latitude,
                longitude = memory.longitude
            ) ?: return@launch

            _uiState.update { state ->
                if (state is MemoryUiState.Content) state.copy(address = address) else state
            }
        }
    }
}
