package ru.aloyaloya.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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
