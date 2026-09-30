package ru.aloyaloya.summary.model

import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.domain.model.Memory

/**
 * Состояние экрана [ru.aloyaloya.summary.presentation.SummaryScreen].
 */
sealed class SummaryUiState {
    data object Loading : SummaryUiState()
    data class Content(
        val period: SummaryPeriod,
        val memoryCount: Int,
        val dayCount: Int,
        val dominantEmotion: Emotion?,
        val emotionShares: List<EmotionShare>,
        val places: List<MoodPlace>,
        val recall: Memory?,
        val canRecallAnother: Boolean
    ) : SummaryUiState()
}

data class EmotionShare(
    val emotion: Emotion,
    val count: Int
)
