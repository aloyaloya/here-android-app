package ru.aloyaloya.data.repository

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.aloyaloya.data.mapper.MemoryMapper.toDomain
import ru.aloyaloya.data.mapper.MemoryMapper.toEntity
import ru.aloyaloya.data.storage.MediaStorage
import ru.aloyaloya.database.dao.MemoryDao
import ru.aloyaloya.database.dao.MemoryMediaDao
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.model.MemoryMedia
import ru.aloyaloya.domain.repository.MemoryRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация [MemoryRepository] на основе локальной базы данных.
 */
@Singleton
class MemoryRepositoryImpl @Inject constructor(
    private val memoryDao: MemoryDao,
    private val memoryMediaDao: MemoryMediaDao,
    private val mediaStorage: MediaStorage
) : MemoryRepository {

    override fun observeAll(): Flow<List<Memory>> =
        memoryDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Memory?> =
        memoryDao.observeById(id).map { it?.toDomain() }

    override suspend fun create(memory: Memory): Long {
        val memoryId = memoryDao.insert(memory.toEntity())
        val media = persist(memory.media)

        if (media.isNotEmpty()) {
            memoryMediaDao.insertAll(media.map { it.toEntity(memoryId) })
        }
        return memoryId
    }

    override suspend fun update(memory: Memory) {
        val stored = memoryMediaDao.getByMemoryId(memory.id)
        val keptIds = memory.media.mapTo(mutableSetOf()) { it.id }
        val removed = stored.filterNot { it.id in keptIds }
        val added = persist(memory.media.filter { it.id == 0L })

        memoryDao.update(memory.toEntity())

        if (removed.isNotEmpty()) memoryMediaDao.deleteAll(removed)
        if (added.isNotEmpty()) memoryMediaDao.insertAll(added.map { it.toEntity(memory.id) })

        mediaStorage.delete(removed.map { it.uri })
    }

    override suspend fun delete(memory: Memory) {
        memoryDao.delete(memory.toEntity())
        mediaStorage.delete(memory.media.map { it.uri })
    }

    override suspend fun deleteAll() = withContext(NonCancellable) {
        memoryDao.deleteAll()
        mediaStorage.clear()
    }

    private suspend fun persist(media: List<MemoryMedia>): List<MemoryMedia> =
        media.mapNotNull { item -> mediaStorage.save(item.uri)?.let { item.copy(uri = it) } }
}
