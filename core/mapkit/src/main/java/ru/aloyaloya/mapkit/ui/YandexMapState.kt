package ru.aloyaloya.mapkit.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.yandex.mapkit.mapview.MapView
import ru.aloyaloya.mapkit.internal.UserLocationBinder
import ru.aloyaloya.mapkit.model.MapPoint

@Stable
class YandexMapState {

    internal var mapView: MapView? = null
    internal var locationBinder: UserLocationBinder? = null

    /** Центр камеры или `null`, пока карта не показана. */
    val cameraTarget: MapPoint?
        get() = mapView?.mapWindow?.map?.cameraPosition?.target
            ?.let { MapPoint(it.latitude, it.longitude) }

    /** Ведет камеру к пользователю. Без разрешения на геолокацию ничего не делает. */
    fun moveToUserLocation() {
        locationBinder?.moveToUserLocation()
    }
}

@Composable
fun rememberYandexMapState(): YandexMapState = remember { YandexMapState() }