package ru.aloyaloya.mapkit

import android.content.Context
import com.yandex.mapkit.MapKitFactory

/** Инициализация MapKit из [android.app.Application.onCreate]. */
object MapKitInitializer {
    fun init(context: Context) {
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(context.applicationContext)
    }
}
