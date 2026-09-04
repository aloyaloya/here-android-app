package ru.aloyaloya.memory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.repository.AddressRepository
import ru.aloyaloya.domain.repository.MemoryRepository
import ru.aloyaloya.memory.model.MemoryUiState
import javax.inject.Inject

class MemoryViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository,
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MemoryUiState>(MemoryUiState.Loading)
    val uiState: StateFlow<MemoryUiState> = _uiState.asStateFlow()

    private var memoryId: Long? = null

    private var addressRequested = false

    /**
     * Принимает идентификатор воспоминания и подписывается на него.
     */
    fun setMemoryId(memoryId: Long) {
        if (this.memoryId != null) return
        this.memoryId = memoryId

        viewModelScope.launch {
            memoryRepository.observeById(memoryId).collect { memory ->
                _uiState.update { state ->
                    when {
                        memory == null -> MemoryUiState.NotFound
                        state is MemoryUiState.Content -> state.copy(memory = memory)
                        else -> MemoryUiState.Content(memory = memory)
                    }
                }
                if (memory != null) resolveAddress(memory)
            }
        }
    }

    /**
     * Спрашивает адрес точки один раз за жизнь экрана.
     */
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
