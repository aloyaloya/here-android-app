package ru.aloyaloya.memory.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.aloyaloya.memory.presentation.NewMemoryViewModel
import ru.aloyaloya.ui.di.ViewModelKey

/**
 * Dagger-модуль фичи воспоминаний.
 *
 * Содержит биндинги и провайдеры зависимостей,
 * необходимых для работы экрана нового воспоминания.
 */
@Module
interface MemoryModule {

    @Binds
    @IntoMap
    @ViewModelKey(NewMemoryViewModel::class)
    fun bindsNewMemoryViewModel(vm: NewMemoryViewModel): ViewModel
}
