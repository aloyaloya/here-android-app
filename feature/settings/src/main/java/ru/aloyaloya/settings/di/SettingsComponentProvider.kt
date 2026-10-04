package ru.aloyaloya.settings.di

/**
 * Контракт для объектов, способных предоставить [SettingsComponent].
 *
 * Используется для доступа к зависимостям фичи настроек из внешнего слоя.
 */
interface SettingsComponentProvider {

    /** Возвращает экземпляр [SettingsComponent] для фичи настроек. */
    fun provideSettingsComponent(): SettingsComponent
}
