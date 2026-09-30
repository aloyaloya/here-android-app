package ru.aloyaloya.mapkit.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import ru.aloyaloya.mapkit.internal.UserLocationBinder
import ru.aloyaloya.mapkit.model.MapPoint

@Stable
class YandexMapState {

    internal var mapView: MapView? = null
    internal var locationBinder: UserLocationBinder? = null

    /** Отъехала ли камера от пользователя. Без фикса геолокации всегда `false`. */
    var awayFromUser: Boolean by mutableStateOf(false)
        internal set

    /** Центр камеры или `null`, пока карта не показана. */
    val cameraTarget: MapPoint?
        get() = mapView?.mapWindow?.map?.cameraPosition?.target
            ?.let { MapPoint(it.latitude, it.longitude) }

    /**
     * Ставит камеру на точку.
     */
    fun moveTo(point: MapPoint, zoom: Float) {
        val map = mapView?.mapWindow?.map ?: return
        locationBinder?.skipCentering()
        map.move(CameraPosition(Point(point.latitude, point.longitude), zoom, 0f, 0f))
    }

    /** Ведет камеру к пользователю. Без разрешения на геолокацию ничего не делает. */
    fun moveToUserLocation() {
        locationBinder?.moveToUserLocation()
    }
}

@Composable
fun rememberYandexMapState(): YandexMapState = remember { YandexMapState() }