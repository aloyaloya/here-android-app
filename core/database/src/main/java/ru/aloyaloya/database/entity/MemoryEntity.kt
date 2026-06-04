package ru.aloyaloya.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность воспоминания в базе данных.
 *
 * Хранит основные данные точки на карте: геолокацию, описание и эмоцию.
 * Медиафайлы хранятся отдельно в [MemoryMediaEntity].
 *
 * @property id Уникальный идентификатор, генерируется автоматически.
 * @property title Заголовок воспоминания.
 * @property description Подробное описание.
 * @property latitude Широта точки на карте.
 * @property longitude Долгота точки на карте.
 * @property emotion Эмоция, связанная с воспоминанием.
 * @property createdAt Время создания в миллисекундах (Unix timestamp).
 */
@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val emotion: Emotion,
    val createdAt: Long
)
