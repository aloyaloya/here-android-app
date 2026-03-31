package ru.aloyaloya.map.di

import androidx.lifecycle.ViewModelProvider
import dagger.Subcomponent

/**
 * Dagger-подкомпонент фичи карты.
 *
 * Компонент собирает зависимости модуля карты и
 * предоставляет фабрику `ViewModel` для экрана.
 */
@Subcomponent(
    modules = [
        MapModule::class
    ]
)
interface MapComponent {

    /**
     * Фабрика создания [MapComponent].
     */
    @Subcomponent.Factory
    interface Factory {
        fun create(): MapComponent
    }

    /** Фабрика для создания `ViewModel` через Dagger multibinding. */
    val viewModelFactory: ViewModelProvider.Factory
}