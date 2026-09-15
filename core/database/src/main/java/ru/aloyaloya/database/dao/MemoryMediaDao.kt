package ru.aloyaloya.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.aloyaloya.database.entity.MemoryMediaEntity

/**
 * DAO для работы с медиафайлами воспоминаний.
 */
@Dao
interface MemoryMediaDao {

    /** Возвращает медиафайлы воспоминания. */
    @Query("SELECT * FROM memory_media WHERE memoryId = :memoryId")
    suspend fun getByMemoryId(memoryId: Long): List<MemoryMediaEntity>

    /** Добавляет медиафайл и возвращает присвоенный идентификатор. */
    @Insert
    suspend fun insert(media: MemoryMediaEntity): Long

    /** Добавляет список медиафайлов за одну транзакцию. */
    @Insert
    suspend fun insertAll(media: List<MemoryMediaEntity>)

    /** Удаляет медиафайл. */
    @Delete
    suspend fun delete(media: MemoryMediaEntity)

    /** Удаляет список медиафайлов за одну транзакцию. */
    @Delete
    suspend fun deleteAll(media: List<MemoryMediaEntity>)
}
