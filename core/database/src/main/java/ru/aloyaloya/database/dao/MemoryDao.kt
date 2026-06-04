package ru.aloyaloya.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.aloyaloya.database.entity.MemoryEntity
import ru.aloyaloya.database.entity.MemoryWithMedia

/**
 * DAO для работы с воспоминаниями в базе данных.
 *
 * Все запросы, возвращающие [MemoryWithMedia], помечены [@Transaction][androidx.room.Transaction],
 * чтобы Room гарантировал консистентность данных при одновременном чтении
 * из двух таблиц.
 */
@Dao
interface MemoryDao {

    /** Возвращает все воспоминания с медиа, отсортированные по дате создания. */
    @Transaction
    @Query("SELECT * FROM memories ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<MemoryWithMedia>>

    /** Возвращает воспоминание с медиа по [id], или `null` если не найдено. */
    @Transaction
    @Query("SELECT * FROM memories WHERE id = :id")
    fun observeById(id: Long): Flow<MemoryWithMedia?>

    /** Добавляет воспоминание и возвращает присвоенный идентификатор. */
    @Insert
    suspend fun insert(memory: MemoryEntity): Long

    /** Обновляет данные существующего воспоминания. */
    @Update
    suspend fun update(memory: MemoryEntity)

    /** Удаляет воспоминание и все его медиафайлы (каскадное удаление). */
    @Delete
    suspend fun delete(memory: MemoryEntity)
}
