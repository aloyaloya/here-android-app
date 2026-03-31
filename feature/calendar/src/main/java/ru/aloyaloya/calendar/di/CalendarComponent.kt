package ru.aloyaloya.calendar.di

import androidx.lifecycle.ViewModelProvider
import dagger.Subcomponent

/**
 * Dagger-подкомпонент фичи календаря.
 *
 * Компонент собирает зависимости модуля календаря и
 * предоставляет фабрику `ViewModel` для экрана.
 */
@Subcomponent(
    modules = [
        CalendarModule::class
    ]
)
interface CalendarComponent {

    /**
     * Фабрика создания [CalendarComponent].
     */
    @Subcomponent.Factory
    interface Factory {
        fun create(): CalendarComponent
    }

    /** Фабрика для создания `ViewModel` через Dagger multibinding. */
    val viewModelFactory: ViewModelProvider.Factory
}