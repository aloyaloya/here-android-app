package ru.aloyaloya.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.aloyaloya.data.mapper.MemoryMapper.toDomain
import ru.aloyaloya.data.mapper.MemoryMapper.toEntity
import ru.aloyaloya.data.mapper.MemoryMapper.toMediaEntities
import ru.aloyaloya.database.dao.MemoryDao
import ru.aloyaloya.database.dao.MemoryMediaDao
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.repository.MemoryRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация [MemoryRepository] на основе локальной базы данных.
 */
@Singleton
class MemoryRepositoryImpl @Inject constructor(
    private val memoryDao: MemoryDao,
    private val memoryMediaDao: MemoryMediaDao
) : MemoryRepository {

    override fun observeAll(): Flow<List<Memory>> =
        memoryDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Memory?> =
        memoryDao.observeById(id).map { it?.toDomain() }

    override suspend fun create(memory: Memory): Long {
        val memoryId = memoryDao.insert(memory.toEntity())
        val mediaEntities = memory.toMediaEntities(memoryId)
        if (mediaEntities.isNotEmpty()) {
            memoryMediaDao.insertAll(mediaEntities)
        }
        return memoryId
    }

    override suspend fun update(memory: Memory) =
        memoryDao.update(memory.toEntity())

    override suspend fun delete(memory: Memory) =
        memoryDao.delete(memory.toEntity())
}
