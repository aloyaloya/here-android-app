package ru.aloyaloya.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "memory_media",
    foreignKeys = [
        ForeignKey(
            entity = MemoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["memoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("memoryId")]
)
data class MemoryMediaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memoryId: Long,
    val uri: String,
    val type: MediaType
)
