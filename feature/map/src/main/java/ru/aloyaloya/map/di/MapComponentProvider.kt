package ru.aloyaloya.map.di

/**
 * Контракт для объектов, способных предоставить [MapComponent].
 *
 * Используется для доступа к зависимостям фичи карты из внешнего слоя.
 */
interface MapComponentProvider {

    /** Возвращает экземпляр [MapComponent] для фичи карты. */
    fun provideMapComponent(): MapComponent
}