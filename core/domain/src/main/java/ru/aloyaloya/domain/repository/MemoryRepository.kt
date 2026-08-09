package ru.aloyaloya.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.aloyaloya.domain.model.Memory

/**
 * Репозиторий для работы с воспоминаниями.
 *
 * Абстрагирует источник данных от фичей приложения.
 */
interface MemoryRepository {

    /** Возвращает все воспоминания, от недавних событий к давним. */
    fun observeAll(): Flow<List<Memory>>

    /** Возвращает воспоминание по [id], или `null` если не найдено. */
    fun observeById(id: Long): Flow<Memory?>

    /** Создаёт воспоминание и возвращает присвоенный идентификатор. */
    suspend fun create(memory: Memory): Long

    /** Обновляет данные существующего воспоминания. */
    suspend fun update(memory: Memory)

    /** Удаляет воспоминание и все его медиафайлы. */
    suspend fun delete(memory: Memory)
}
