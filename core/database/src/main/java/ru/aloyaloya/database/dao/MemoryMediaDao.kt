package ru.aloyaloya.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import kotlinx.coroutines.flow.Flow
import ru.aloyaloya.database.entity.MemoryMediaEntity

@Dao
interface MemoryMediaDao {

    @Insert
    suspend fun insert(media: MemoryMediaEntity): Long

    @Insert
    suspend fun insertAll(media: List<MemoryMediaEntity>)

    @Delete
    suspend fun delete(media: MemoryMediaEntity)
}
