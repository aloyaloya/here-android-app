package ru.aloyaloya.data.di

import dagger.Binds
import dagger.Module
import ru.aloyaloya.data.repository.MemoryRepositoryImpl
import ru.aloyaloya.domain.repository.MemoryRepository
import javax.inject.Singleton

/**
 * Dagger-модуль data-слоя.
 *
 * Связывает интерфейсы репозиториев из domain с их реализациями.
 */
@Module
interface DataModule {

    /** Привязывает [MemoryRepositoryImpl] к [MemoryRepository]. */
    @Binds
    @Singleton
    fun bindMemoryRepository(impl: MemoryRepositoryImpl): MemoryRepository
}
