package ru.aloyaloya.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.aloyaloya.database.dao.MemoryDao
import ru.aloyaloya.database.dao.MemoryMediaDao
import ru.aloyaloya.database.entity.MemoryEntity
import ru.aloyaloya.database.entity.MemoryMediaEntity

/**
 * Корневая база данных приложения.
 *
 * Содержит таблицы воспоминаний и их медиафайлов.
 * Экземпляр создаётся через Dagger как `@Singleton` в `DatabaseModule`.
 */
@Database(
    entities = [MemoryEntity::class, MemoryMediaEntity::class],
    version = 1
)
abstract class HereDatabase : RoomDatabase() {

    /** DAO для работы с воспоминаниями. */
    abstract fun memoryDao(): MemoryDao

    /** DAO для работы с медиафайлами воспоминаний. */
    abstract fun memoryMediaDao(): MemoryMediaDao
}
