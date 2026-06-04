package ru.aloyaloya.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MemoryWithMedia(
    @Embedded val memory: MemoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "memoryId"
    )
    val media: List<MemoryMediaEntity>
)
