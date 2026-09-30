package ru.aloyaloya.summary.di

import androidx.lifecycle.ViewModelProvider
import dagger.Subcomponent

/**
 * Dagger-подкомпонент фичи итогов.
 *
 * Компонент собирает зависимости модуля итогов и
 * предоставляет фабрику `ViewModel` для экрана.
 */
@Subcomponent(
    modules = [
        SummaryModule::class
    ]
)
interface SummaryComponent {

    /**
     * Фабрика создания [SummaryComponent].
     */
    @Subcomponent.Factory
    interface Factory {
        fun create(): SummaryComponent
    }

    /** Фабрика для создания `ViewModel` через Dagger multibinding. */
    val viewModelFactory: ViewModelProvider.Factory
}