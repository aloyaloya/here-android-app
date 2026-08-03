package ru.aloyaloya.here.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import dagger.BindsInstance
import dagger.Component
import ru.aloyaloya.analytic.di.AnalyticComponent
import ru.aloyaloya.calendar.di.CalendarComponent
import ru.aloyaloya.data.di.DataModule
import ru.aloyaloya.database.di.DatabaseModule
import ru.aloyaloya.here.HereApplication
import ru.aloyaloya.map.di.MapComponent
import ru.aloyaloya.memory.di.MemoryComponent
import javax.inject.Singleton

/**
 * Корневой Dagger-компонент приложения.
 *
 * Компонент собирает граф зависимостей уровня приложения,
 * предоставляет фабрику `ViewModel` и выполняет инъекцию
 * в [HereApplication].
 */
@Singleton
@Component(
    modules = [
        AppModule::class,
        DatabaseModule::class,
        DataModule::class
    ]
)
interface AppComponent {

    /**
     * Фабрика создания [AppComponent].
     *
     * @param context Контекст приложения, передаваемый в граф через [BindsInstance].
     */
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    /** Выполняет инъекцию зависимостей в экземпляр [HereApplication]. */
    fun inject(application: HereApplication)

    /** Фабрика для создания `ViewModel` через Dagger multibinding. */
    val viewModelFactory: ViewModelProvider.Factory

    /** Фабрика подкомпонента фичи карты. */
    val mapComponentFactory: MapComponent.Factory

    /** Фабрика подкомпонента фичи воспоминаний. */
    val memoryComponentFactory: MemoryComponent.Factory

    /** Фабрика подкомпонента фичи календаря. */
    val calendarComponentFactory: CalendarComponent.Factory

    /** Фабрика подкомпонента фичи аналитики. */
    val analyticComponentFactory: AnalyticComponent.Factory
}