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
import ru.aloyaloya.mapkit.internal.MarkersBinder
import ru.aloyaloya.mapkit.internal.UserLocationBinder
import ru.aloyaloya.mapkit.model.MapPoint

@Stable
class YandexMapState {

    internal var mapView: MapView? = null
    internal var locationBinder: UserLocationBinder? = null
    internal var markersBinder: MarkersBinder? = null

    var awayFromUser: Boolean by mutableStateOf(false)
        internal set

    val cameraTarget: MapPoint?
        get() = mapView?.mapWindow?.map?.cameraPosition?.target
            ?.let { MapPoint(it.latitude, it.longitude) }

    fun moveTo(point: MapPoint, zoom: Float) {
        val map = mapView?.mapWindow?.map ?: return
        locationBinder?.skipCentering()
        map.move(CameraPosition(Point(point.latitude, point.longitude), zoom, 0f, 0f))
    }

    fun dropMarker(id: Long, delayMillis: Long = 0) {
        markersBinder?.drop(id, delayMillis)
    }

    fun moveToUserLocation() {
        locationBinder?.moveToUserLocation()
    }
}

@Composable
fun rememberYandexMapState(): YandexMapState = remember { YandexMapState() }