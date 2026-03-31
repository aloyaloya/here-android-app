package ru.aloyaloya.analytic.di

import androidx.lifecycle.ViewModelProvider
import dagger.Subcomponent

/**
 * Dagger-подкомпонент фичи аналитики.
 *
 * Компонент собирает зависимости модуля календаря и
 * предоставляет фабрику `ViewModel` для экрана.
 */
@Subcomponent(
    modules = [
        AnalyticModule::class
    ]
)
interface AnalyticComponent {

    /**
     * Фабрика создания [AnalyticComponent].
     */
    @Subcomponent.Factory
    interface Factory {
        fun create(): AnalyticComponent
    }

    /** Фабрика для создания `ViewModel` через Dagger multibinding. */
    val viewModelFactory: ViewModelProvider.Factory
}