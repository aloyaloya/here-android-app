package ru.aloyaloya.settings.di

import androidx.lifecycle.ViewModelProvider
import dagger.Subcomponent

/**
 * Dagger-подкомпонент фичи настроек.
 *
 * Компонент собирает зависимости модуля настроек и
 * предоставляет фабрику `ViewModel` для экрана.
 */
@Subcomponent(
    modules = [
        SettingsModule::class
    ]
)
interface SettingsComponent {

    /**
     * Фабрика создания [SettingsComponent].
     */
    @Subcomponent.Factory
    interface Factory {
        fun create(): SettingsComponent
    }

    /** Фабрика для создания `ViewModel` через Dagger multibinding. */
    val viewModelFactory: ViewModelProvider.Factory
}
