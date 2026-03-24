package ru.aloyaloya.here.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import dagger.BindsInstance
import dagger.Component
import ru.aloyaloya.here.HereApplication
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
        AppModule::class
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
}