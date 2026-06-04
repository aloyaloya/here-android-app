package ru.aloyaloya.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.aloyaloya.database.dao.MemoryDao
import ru.aloyaloya.database.dao.MemoryMediaDao
import ru.aloyaloya.database.entity.MemoryEntity
import ru.aloyaloya.database.entity.MemoryMediaEntity

@Database(
    entities = [MemoryEntity::class, MemoryMediaEntity::class],
    version = 1
)
abstract class HereDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
    abstract fun memoryMediaDao(): MemoryMediaDao
}
