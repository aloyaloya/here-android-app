package ru.aloyaloya.calendar.di

/**
 * Контракт для объектов, способных предоставить [CalendarComponent].
 *
 * Используется для доступа к зависимостям фичи календаря из внешнего слоя.
 */
interface CalendarComponentProvider {

    /** Возвращает экземпляр [CalendarComponent] для фичи календаря. */
    fun provideCalendarComponent(): CalendarComponent
}