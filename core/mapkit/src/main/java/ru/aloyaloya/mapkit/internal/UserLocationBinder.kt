package ru.aloyaloya.mapkit.internal

import android.content.Context
import android.location.Location
import androidx.compose.ui.graphics.toArgb
import com.yandex.mapkit.Animation
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
 * Камера наводится на пользователя один раз за сессию карты: иначе она сбрасывалась бы
 * при каждом возврате на экран. Дальше наведение - только по [moveToUserLocation].
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
    private var centered = false

    fun attach() {
        if (active) return
        active = true
        mapKit.resetLocationManagerToDefault()

        val layer = userLocationLayer ?: mapKit.createUserLocationLayer(mapView.mapWindow)
            .also { created ->
                created.setObjectListener(iconListener)
                userLocationLayer = created
            }

        layer.isVisible = true

        if (!centered) {
            centered = true
            moveToUserLocation(animated = false)
        }
    }

    /**
     * Наводит камеру на пользователя.
     *
     * Если слой уже знает положение, камера едет туда одним движением. Иначе сначала
     * берется последняя известная точка, следом запрашивается свежая.
     */
    fun moveToUserLocation(animated: Boolean = true) {
        userLocationLayer?.cameraPosition()?.target?.let { point ->
            cancelPendingMove()
            moveCamera(point, animated)
            return
        }

        LastKnownLocationReader.readBestPoint(appContext)?.let { moveCamera(it, animated) }

        val manager = locationManager ?: mapKit.createLocationManager().also {
            locationManager = it
        }

        locationListener?.let(manager::unsubscribe)

        val listener = object : LocationListener {
            private var done = false

            override fun onLocationUpdated(location: MapKitLocation) {
                if (done) return
                done = true
                moveCamera(location.position, animated)
                manager.unsubscribe(this)
            }

            override fun onLocationStatusUpdated(status: LocationStatus) = Unit
        }

        locationListener = listener
        manager.requestSingleUpdate(listener)
    }

    /** Отменяет ожидание свежего фикса: камеру уже ведет пользователь. */
    fun cancelPendingMove() {
        locationListener?.let { locationManager?.unsubscribe(it) }
        locationListener = null
    }

    /**
     * Отъехала ли камера от пользователя.
     */
    fun isAwayFromUser(target: Point): Boolean {
        val user = userLocationLayer?.cameraPosition()?.target ?: return false
        val distance = FloatArray(1)

        Location.distanceBetween(
            user.latitude,
            user.longitude,
            target.latitude,
            target.longitude,
            distance
        )

        return distance.first() > AWAY_DISTANCE_METERS
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

    private fun moveCamera(point: Point, animated: Boolean) {
        val position = CameraPosition(point, userLocationZoom, 0f, 0f)

        if (animated) {
            mapView.mapWindow.map.move(position, CAMERA_ANIMATION, null)
        } else {
            mapView.mapWindow.map.move(position)
        }
    }

    fun detach() {
        if (!active) return
        active = false
        cancelPendingMove()
        locationManager = null
        userLocationLayer?.isVisible = false
    }

    private companion object {
        val CAMERA_ANIMATION = Animation(Animation.Type.SMOOTH, 0.4f)

        /** С такого расстояния до пользователя кнопка возврата уже нужна. */
        const val AWAY_DISTANCE_METERS = 150f
    }
}
