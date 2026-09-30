package ru.aloyaloya.settings.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.aloyaloya.settings.presentation.SettingsViewModel
import ru.aloyaloya.ui.di.ViewModelKey

/**
 * Dagger-модуль фичи настроек.
 *
 * Содержит биндинги и провайдеры зависимостей,
 * необходимых для работы экрана настроек.
 */
@Module
interface SettingsModule {

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    fun bindsSettingsViewModel(vm: SettingsViewModel): ViewModel
}
