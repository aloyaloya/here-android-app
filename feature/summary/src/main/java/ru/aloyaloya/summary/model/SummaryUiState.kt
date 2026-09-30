package ru.aloyaloya.summary.model

import ru.aloyaloya.domain.model.Memory

/**
 * Состояние экрана [ru.aloyaloya.summary.presentation.SummaryScreen].
 */
sealed class SummaryUiState {
    data object Loading : SummaryUiState()
    data class Content(
        val period: SummaryPeriod,
        val memories: List<Memory>
    ) : SummaryUiState()
}
