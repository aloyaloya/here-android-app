package ru.aloyaloya.here

import android.app.Application
import ru.aloyaloya.here.di.AppComponent
import ru.aloyaloya.here.di.DaggerAppComponent
import ru.aloyaloya.ui.di.ComponentProvider
import kotlin.reflect.KClass

/**
 * Класс приложения Here.
 *
 * Создает корневой DI-компонент при старте приложения
 * и выполняет инъекцию зависимостей в `Application`.
 */
class HereApplication : ComponentProvider, Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.factory().create(this)

        appComponent.inject(this)
    }

    /**
     * Предоставляет feature-компонент по ключу для модулей верхнего уровня.
     *
     * Маршрутизирует запросы к фабрикам подкомпонентов из [appComponent].
     *
     * @param key Ключ компонента: `map`, `calendar` или `analytic`.
     * @param clazz Ожидаемый тип компонента.
     * @return Созданный экземпляр компонента типа [T].
     * @throws IllegalArgumentException Если передан неизвестный ключ.
     */
    override fun <T : Any> provideComponent(key: String, clazz: KClass<T>): T {
        return when (key) {
            "map" -> appComponent.mapComponentFactory.create() as T
            "calendar" -> appComponent.calendarComponentFactory.create() as T
            "analytic" -> appComponent.analyticComponentFactory.create() as T
            else -> throw IllegalArgumentException("Unknown component key: $key")
        }
    }
}