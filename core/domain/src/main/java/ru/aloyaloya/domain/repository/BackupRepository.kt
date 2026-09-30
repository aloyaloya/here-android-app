package ru.aloyaloya.domain.repository

/**
 * Резервная копия воспоминаний в zip-архиве.
 */
interface BackupRepository {

    /** Записывает все воспоминания и их медиа в [destination] и возвращает их число. */
    suspend fun export(destination: String): Int

    /** Добавляет воспоминания из [source], пропуская уже сохраненные, и возвращает число добавленных. */
    suspend fun import(source: String): Int
}
