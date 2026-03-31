package ru.aloyaloya.analytic.di

/**
 * Контракт для объектов, способных предоставить [AnalyticComponent].
 *
 * Используется для доступа к зависимостям фичи аналитики из внешнего слоя.
 */
interface AnalyticComponentProvider {

    /** Возвращает экземпляр [AnalyticComponent] для фичи аналитики. */
    fun provideCalendarComponent(): AnalyticComponent
}