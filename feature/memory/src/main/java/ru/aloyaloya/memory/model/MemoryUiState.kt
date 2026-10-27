package ru.aloyaloya.memory.model

import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.model.PlaceAddress

/**
 * Состояние экрана [ru.aloyaloya.memory.presentation.MemoryScreen].
 */
sealed class MemoryUiState {

    data object Loading : MemoryUiState()

    data class Content(
        val memory: Memory,
        val address: PlaceAddress? = null,
        val nearby: List<Memory> = emptyList(),
        val activeSheet: MemorySheet? = null,
        val viewedMedia: Int? = null
    ) : MemoryUiState()

    data object NotFound : MemoryUiState()
}
