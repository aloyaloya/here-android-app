package ru.aloyaloya.database.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Воспоминание вместе со всеми прикреплёнными медиафайлами.
 *
 * Используется для получения полных данных одним запросом через [androidx.room.Relation].
 *
 * @property memory Основные данные воспоминания.
 * @property media Список медиафайлов, прикреплённых к воспоминанию.
 */
data class MemoryWithMedia(
    @Embedded val memory: MemoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "memoryId"
    )
    val media: List<MemoryMediaEntity>
)
