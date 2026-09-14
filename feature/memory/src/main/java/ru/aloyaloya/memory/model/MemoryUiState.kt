package ru.aloyaloya.memory.model

import ru.aloyaloya.domain.model.Memory

/**
 * Состояние экрана [ru.aloyaloya.memory.presentation.MemoryScreen].
 */
sealed class MemoryUiState {

    data object Loading : MemoryUiState()

    data class Content(
        val memory: Memory,
        val address: String? = null,
        val activeSheet: MemorySheet? = null,
        val viewedMedia: Int? = null
    ) : MemoryUiState()

    data object NotFound : MemoryUiState()
}
