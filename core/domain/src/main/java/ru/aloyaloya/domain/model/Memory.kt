package ru.aloyaloya.domain.model

/**
 * Воспоминание — точка на карте с описанием и эмоцией.
 *
 * @property id Уникальный идентификатор.
 * @property title Заголовок воспоминания.
 * @property description Подробное описание.
 * @property latitude Широта точки на карте.
 * @property longitude Долгота точки на карте.
 * @property emotion Эмоция, связанная с воспоминанием.
 * @property createdAt Когда запись создана, в миллисекундах (Unix timestamp).
 * @property happenedAt Когда событие произошло, в миллисекундах (Unix timestamp).
 * Задается пользователем: записать воспоминание можно и на следующий день.
 * @property media Список прикреплённых медиафайлов.
 */
data class Memory(
    val id: Long = 0,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val emotion: Emotion,
    val createdAt: Long,
    val happenedAt: Long,
    val media: List<MemoryMedia> = emptyList()
)
