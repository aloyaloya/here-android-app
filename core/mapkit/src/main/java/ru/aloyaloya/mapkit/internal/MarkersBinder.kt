package ru.aloyaloya.mapkit.internal

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PointF
import android.location.Location
import android.view.animation.OvershootInterpolator
import androidx.core.animation.doOnEnd
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Cluster
import com.yandex.mapkit.map.ClusterListener
import com.yandex.mapkit.map.ClusterTapListener
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import ru.aloyaloya.mapkit.model.MapMarker

/**
 * Держит метки на карте.
 *
 * Метки, которые на текущем зуме наезжают друг на друга, MapKit склеивает в стопку.
 * По нажатию на стопку камера приближается, пока она не распадется. Если все метки
 * стоят в одной точке, приближение не поможет, и тогда стопка отдается наружу.
 *
 * @param maxZoom Предел зума карты: дальше приближать стопку некуда.
 * @param onMarkerClick Колбэк нажатия на метку, отдает [MapMarker.id].
 * @param onClusterClick Колбэк нажатия на стопку в одной точке, отдает [MapMarker.id] всех меток.
 */
internal class MarkersBinder(
    private val mapView: MapView,
    private val context: Context,
    private val maxZoom: Float,
    private val onMarkerClick: (Long) -> Unit,
    private val onClusterClick: (List<Long>) -> Unit
) {

    private val map get() = mapView.mapWindow.map

    private var current: List<MapMarker> = emptyList()
    private var dark = false

    private val placemarks = mutableListOf<PlacemarkMapObject>()
    private val clusters = mutableMapOf<Cluster, PointF>()
    private val resize = IconResize { applyScale() }

    private var dropId: Long? = null
    private var dropOffset: Float? = null
    private var dropAnimator: ValueAnimator? = null

    private val tapListener = MapObjectTapListener { mapObject, _ ->
        val marker = mapObject.userData as? MapMarker
        if (marker == null) {
            false
        } else {
            onMarkerClick(marker.id)
            true
        }
    }

    private val clusterTapListener = ClusterTapListener { cluster ->
        val markers = cluster.markers()

        if (markers.isSamePlace() || map.cameraPosition.zoom >= maxZoom) {
            onClusterClick(markers.map { it.id })
        } else {
            zoomTo(markers)
        }
        true
    }

    private val clusterListener = ClusterListener { cluster ->
        val stack = MarkerIcons.stack(context, cluster.markers().stackIcons(), dark)
        cluster.appearance.setIcon(stack.image, scaled(stack.anchor))
        clusters.keys.removeAll { !it.isValid }
        clusters[cluster] = stack.anchor
        cluster.addClusterTapListener(clusterTapListener)
    }

    private val collection = map.mapObjects.addClusterizedPlacemarkCollection(clusterListener)

    init {
        collection.addTapListener(tapListener)
    }

    /**
     * Ставит метки на карту.
     *
     * @param markers Метки.
     * @param dark Темная ли тема: от нее зависят цвета иконок.
     */
    fun apply(markers: List<MapMarker>, dark: Boolean) {
        if (markers == current && dark == this.dark) return

        this.dark = dark

        collection.clear()
        placemarks.clear()
        clusters.clear()
        markers.forEach { marker ->
            placemarks += collection.addPlacemark().apply {
                geometry = Point(marker.point.latitude, marker.point.longitude)
                setIcon(MarkerIcons.get(context, marker.icon, dark))
                userData = marker
                setIconStyle(markerStyle(marker.id))
            }
        }
        collection.clusterPlacemarks(CLUSTER_RADIUS, maxZoom.toInt())
        current = markers
    }

    /**
     * Роняет метку сверху на ее точку. До падения метка спрятана.
     *
     * @param id [MapMarker.id] метки. Если метки еще нет, она упадет, как только появится.
     * @param delayMillis Задержка перед падением.
     */
    fun drop(id: Long, delayMillis: Long) {
        dropAnimator?.removeAllListeners()
        dropAnimator?.cancel()
        dropId = id
        dropOffset = null
        applyDrop()

        dropAnimator = ValueAnimator.ofFloat(DROP_HEIGHT, 0f).apply {
            startDelay = delayMillis
            duration = DROP_MILLIS
            interpolator = OvershootInterpolator()
            addUpdateListener { animator ->
                dropOffset = animator.animatedValue as Float
                applyDrop()
            }
            doOnEnd {
                dropOffset = 0f
                applyDrop()
                dropId = null
                dropAnimator = null
            }
            start()
        }
    }

    /** Прячет метки, сжимая их в точку, и показывает, раздувая обратно. */
    fun setVisible(visible: Boolean) {
        collection.isVisible = true
        resize.animateTo(visible) { collection.isVisible = visible }
    }

    private fun applyScale() {
        placemarks.forEach { it.setIconStyle(markerStyle(it.markerId)) }
        clusters.keys.removeAll { !it.isValid }
        clusters.forEach { (cluster, anchor) -> cluster.appearance.setIconStyle(scaled(anchor)) }
    }

    private fun applyDrop() {
        placemarks.find { it.markerId == dropId }?.let { it.setIconStyle(markerStyle(dropId)) }
    }

    private fun markerStyle(id: Long?): IconStyle {
        if (id == null || id != dropId) return scaled()

        val offset = dropOffset
        val anchor = PointF(MarkerIcons.anchor.x, MarkerIcons.anchor.y + (offset ?: 0f))
        return scaled(anchor).setVisible(offset != null)
    }

    private val PlacemarkMapObject.markerId get() = (userData as? MapMarker)?.id

    private fun scaled(anchor: PointF = MarkerIcons.anchor) =
        IconStyle().setAnchor(anchor).setScale(resize.scale)

    private fun zoomTo(markers: List<MapMarker>) {
        val box = BoundingBox(
            Point(markers.minOf { it.point.latitude }, markers.minOf { it.point.longitude }),
            Point(markers.maxOf { it.point.latitude }, markers.maxOf { it.point.longitude })
        )
        val fit = map.cameraPosition(Geometry.fromBoundingBox(box))
        val position = CameraPosition(
            fit.target,
            (fit.zoom - FIT_ZOOM_MARGIN).coerceAtMost(maxZoom),
            0f,
            0f
        )

        map.move(position, CAMERA_ANIMATION, null)
    }

    private fun Cluster.markers(): List<MapMarker> =
        placemarks.mapNotNull { it.userData as? MapMarker }

    private fun List<MapMarker>.stackIcons() =
        map { it.icon }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .map { it.key }
            .let { distinct -> distinct + List(size) { distinct.first() } }
            .take(minOf(size, STACK_SIZE))

    private fun List<MapMarker>.isSamePlace(): Boolean {
        val first = first().point
        val distance = FloatArray(1)

        return all { marker ->
            Location.distanceBetween(
                first.latitude,
                first.longitude,
                marker.point.latitude,
                marker.point.longitude,
                distance
            )
            distance.first() <= SAME_PLACE_METERS
        }
    }

    private companion object {

        const val CLUSTER_RADIUS = 40.0

        const val STACK_SIZE = 3

        const val SAME_PLACE_METERS = 15f

        const val FIT_ZOOM_MARGIN = 0.8f

        const val DROP_HEIGHT = 1f

        const val DROP_MILLIS = 500L

        val CAMERA_ANIMATION = Animation(Animation.Type.SMOOTH, 0.4f)
    }
}
