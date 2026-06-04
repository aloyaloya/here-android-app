package ru.aloyaloya.mapkit.internal

import android.content.Context
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.location.Location as MapKitLocation

/** Геолокация: last-known, слой пользователя, один фикс MapKit. */
internal class UserLocationBinder(
    private val mapView: MapView,
    private val userLocationZoom: Float,
    private val appContext: Context,
) {
    private val mapKit get() = MapKitFactory.getInstance()

    private var userLocationLayer: UserLocationLayer? = null
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var active = false

    fun attach() {
        if (active) return
        active = true
        mapKit.resetLocationManagerToDefault()

        LastKnownLocationReader.readBestPoint(appContext)?.let { moveCamera(it) }

        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow).apply {
            isVisible = true
        }

        val manager = mapKit.createLocationManager()
        val listener = object : LocationListener {
            private var done = false

            override fun onLocationUpdated(location: MapKitLocation) {
                if (done) return
                done = true
                moveCamera(location.position)
                manager.unsubscribe(this)
            }

            override fun onLocationStatusUpdated(status: LocationStatus) = Unit
        }

        locationManager = manager
        locationListener = listener
        manager.requestSingleUpdate(listener)
    }

    private fun moveCamera(point: Point) {
        mapView.mapWindow.map.move(
            CameraPosition(point, userLocationZoom, 0f, 0f),
        )
    }

    fun detach() {
        if (!active) return
        active = false
        locationListener?.let { locationManager?.unsubscribe(it) }
        locationListener = null
        locationManager = null
        userLocationLayer?.isVisible = false
        userLocationLayer = null
    }
}
