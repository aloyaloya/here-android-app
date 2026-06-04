package ru.aloyaloya.map.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.aloyaloya.map.presentation.MapViewModel
import ru.aloyaloya.ui.di.ViewModelKey

/**
 * Dagger-модуль фичи карты.
 *
 * Содержит биндинги и провайдеры зависимостей,
 * необходимых для работы экрана карты.
 */
@Module
interface MapModule {

    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    fun bindsMapViewModel(vm: MapViewModel): ViewModel
}