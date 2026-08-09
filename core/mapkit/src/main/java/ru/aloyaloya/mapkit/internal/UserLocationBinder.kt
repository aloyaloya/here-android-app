package ru.aloyaloya.mapkit.internal

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import ru.aloyaloya.mapkit.model.UserLocationStyle
import com.yandex.mapkit.location.Location as MapKitLocation

/**
 * Геолокация: last-known, слой пользователя, один фикс MapKit.
 *
 * @param style Текущие цвета маркера или `null`, если маркер не показывается. Читается
 * лямбдой, а не значением: слой с одним ID можно создать только раз, поэтому биндер
 * переживает смену темы и просто перекрашивает маркер.
 */
internal class UserLocationBinder(
    private val mapView: MapView,
    private val userLocationZoom: Float,
    private val appContext: Context,
    private val style: () -> UserLocationStyle?
) {
    private val mapKit get() = MapKitFactory.getInstance()

    private var userLocationLayer: UserLocationLayer? = null
    private var userLocationView: UserLocationView? = null
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var active = false

    fun attach() {
        if (active) return
        active = true
        mapKit.resetLocationManagerToDefault()

        LastKnownLocationReader.readBestPoint(appContext)?.let { moveCamera(it) }

        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow).apply {
            setObjectListener(iconListener)
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

    /** Перекрашивает маркер под текущую тему. Без маркера на карте не делает ничего. */
    fun applyStyle() {
        userLocationView?.let(::applyStyle)
    }

    /**
     * Подменяет иконки маркера на свои, как только MapKit создает его на карте.
     */
    private val iconListener = object : UserLocationObjectListener {
        override fun onObjectAdded(view: UserLocationView) {
            userLocationView = view
            applyStyle(view)
        }

        override fun onObjectRemoved(view: UserLocationView) {
            userLocationView = null
        }

        override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) = Unit
    }

    /**
     * `arrow` показывается с известным курсом, `pin` — без него; MapKit сам переключается
     * между ними, поэтому задаются обе.
     */
    private fun applyStyle(view: UserLocationView) {
        val style = style() ?: return
        view.arrow.setIcon(UserLocationIcons.arrow(appContext, style))
        view.pin.setIcon(UserLocationIcons.pin(appContext, style))
        view.accuracyCircle.fillColor = style.accuracy.toArgb()
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
        userLocationView = null
    }
}
