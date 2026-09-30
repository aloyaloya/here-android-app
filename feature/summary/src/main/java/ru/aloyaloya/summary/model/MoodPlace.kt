package ru.aloyaloya.summary.model

import ru.aloyaloya.domain.model.Emotion

/**
 * Место настроения: воспоминания, оставленные рядом друг с другом.
 *
 * @property latitude Широта самого свежего воспоминания места.
 * @property longitude Долгота самого свежего воспоминания места.
 * @property memoryCount Сколько воспоминаний здесь оставлено.
 * @property dominantEmotion Что здесь чувствовалось чаще всего.
 * @property address Адрес места или `null`, пока он не найден или если найти не вышло.
 */
data class MoodPlace(
    val latitude: Double,
    val longitude: Double,
    val memoryCount: Int,
    val dominantEmotion: Emotion,
    val address: String?
)
