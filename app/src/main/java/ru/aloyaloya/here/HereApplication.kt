package ru.aloyaloya.here

import android.app.Application
import ru.aloyaloya.here.di.AppComponent
import ru.aloyaloya.here.di.DaggerAppComponent

class HereApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.factory().create(this)

        appComponent.inject(this)
    }
}