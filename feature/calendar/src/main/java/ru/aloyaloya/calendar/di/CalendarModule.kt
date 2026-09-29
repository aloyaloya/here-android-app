package ru.aloyaloya.calendar.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.aloyaloya.calendar.presentation.CalendarViewModel
import ru.aloyaloya.ui.di.ViewModelKey

/**
 * Dagger-модуль фичи календаря.
 *
 * Содержит биндинги и провайдеры зависимостей,
 * необходимых для работы экрана календаря.
 */
@Module
interface CalendarModule {

    @Binds
    @IntoMap
    @ViewModelKey(CalendarViewModel::class)
    fun bindsCalendarViewModel(vm: CalendarViewModel): ViewModel
}
