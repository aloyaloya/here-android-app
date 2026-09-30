package ru.aloyaloya.summary.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.aloyaloya.summary.presentation.SummaryViewModel
import ru.aloyaloya.ui.di.ViewModelKey

/**
 * Dagger-модуль фичи итогов.
 *
 * Содержит биндинги и провайдеры зависимостей,
 * необходимых для работы экрана итогов.
 */
@Module
interface SummaryModule {

    @Binds
    @IntoMap
    @ViewModelKey(SummaryViewModel::class)
    fun bindsSummaryViewModel(vm: SummaryViewModel): ViewModel
}
