package ru.aloyaloya.here.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.aloyaloya.analytic.di.AnalyticComponent
import ru.aloyaloya.calendar.di.CalendarComponent
import ru.aloyaloya.here.MainViewModel
import ru.aloyaloya.map.di.MapComponent
import ru.aloyaloya.ui.di.DaggerVMFactory
import ru.aloyaloya.ui.di.ViewModelKey

/**
 * Dagger-модуль приложения.
 *
 * Содержит биндинги для фабрики `ViewModel` и регистрацию
 * `ViewModel` в multibinding-карту по ключу [ViewModelKey].
 */
@Module(
    includes = [],
    subcomponents = [
        MapComponent::class,
        CalendarComponent::class,
        AnalyticComponent::class
    ]
)
interface AppModule {

    /** Привязывает реализацию [DaggerVMFactory] к [ViewModelProvider.Factory]. */
    @Binds
    fun bindsViewModelFactory(daggerVMFactory: DaggerVMFactory): ViewModelProvider.Factory

    /** Регистрирует [MainViewModel] в карту `ViewModel` для фабрики. */
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun bindsMainViewModel(vm: MainViewModel): ViewModel
}