package ru.aloyaloya.here

import android.app.Application
import ru.aloyaloya.here.di.AppComponent
import ru.aloyaloya.here.di.DaggerAppComponent

/**
 * Класс приложения Here.
 *
 * Создает корневой DI-компонент при старте приложения
 * и выполняет инъекцию зависимостей в `Application`.
 */
class HereApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.factory().create(this)

        appComponent.inject(this)
    }
}