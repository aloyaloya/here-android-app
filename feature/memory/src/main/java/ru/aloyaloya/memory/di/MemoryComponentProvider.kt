package ru.aloyaloya.memory.di

/**
 * Контракт для объектов, способных предоставить [MemoryComponent].
 *
 * Используется для доступа к зависимостям фичи воспоминаний из внешнего слоя.
 */
interface MemoryComponentProvider {

    /** Возвращает экземпляр [MemoryComponent] для фичи воспоминаний. */
    fun provideMemoryComponent(): MemoryComponent
}
