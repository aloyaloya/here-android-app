package ru.aloyaloya.memory.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.aloyaloya.memory.presentation.MemoryViewModel
import ru.aloyaloya.memory.presentation.MemoryFormViewModel
import ru.aloyaloya.ui.di.ViewModelKey

/**
 * Dagger-модуль фичи воспоминаний.
 *
 * Содержит биндинги и провайдеры зависимостей,
 * необходимых для работы экранов воспоминаний.
 */
@Module
interface MemoryModule {

    @Binds
    @IntoMap
    @ViewModelKey(MemoryFormViewModel::class)
    fun bindsMemoryFormViewModel(vm: MemoryFormViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MemoryViewModel::class)
    fun bindsMemoryViewModel(vm: MemoryViewModel): ViewModel
}
