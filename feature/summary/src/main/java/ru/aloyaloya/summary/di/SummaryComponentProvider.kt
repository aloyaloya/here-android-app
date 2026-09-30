package ru.aloyaloya.summary.di

/**
 * Контракт для объектов, способных предоставить [SummaryComponent].
 *
 * Используется для доступа к зависимостям фичи итогов из внешнего слоя.
 */
interface SummaryComponentProvider {

    /** Возвращает экземпляр [SummaryComponent] для фичи итогов. */
    fun provideSummaryComponent(): SummaryComponent
}