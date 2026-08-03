package ru.aloyaloya.memory.di

import androidx.lifecycle.ViewModelProvider
import dagger.Subcomponent

/**
 * Dagger-подкомпонент фичи воспоминаний.
 *
 * Компонент собирает зависимости экранов воспоминания и
 * предоставляет фабрику `ViewModel` для них.
 */
@Subcomponent(
    modules = [
        MemoryModule::class,
    ]
)
interface MemoryComponent {

    /**
     * Фабрика создания [MemoryComponent].
     */
    @Subcomponent.Factory
    interface Factory {
        fun create(): MemoryComponent
    }

    /** Фабрика для создания `ViewModel` через Dagger multibinding. */
    val viewModelFactory: ViewModelProvider.Factory
}
