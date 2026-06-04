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

@Dao
interface MemoryDao {

    @Transaction
    @Query("SELECT * FROM memories ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<MemoryWithMedia>>

    @Transaction
    @Query("SELECT * FROM memories WHERE id = :id")
    fun observeById(id: Long): Flow<MemoryWithMedia?>

    @Insert
    suspend fun insert(memory: MemoryEntity): Long

    @Update
    suspend fun update(memory: MemoryEntity)

    @Delete
    suspend fun delete(memory: MemoryEntity)
}
