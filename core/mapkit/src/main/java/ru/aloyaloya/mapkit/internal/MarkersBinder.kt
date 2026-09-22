package ru.aloyaloya.mapkit.internal

import android.content.Context
import android.location.Location
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Cluster
import com.yandex.mapkit.map.ClusterListener
import com.yandex.mapkit.map.ClusterTapListener
import com.yandex.mapkit.map.MapObjectTapListener
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
        val stack = MarkerIcons.stack(context, cluster.markers().stackIcons())
        cluster.appearance.setIcon(stack.image, stack.style)
        cluster.addClusterTapListener(clusterTapListener)
    }

    private val collection = map.mapObjects.addClusterizedPlacemarkCollection(clusterListener)

    init {
        collection.addTapListener(tapListener)
    }

    fun apply(markers: List<MapMarker>) {
        if (markers == current) return

        collection.clear()
        markers.forEach { marker ->
            collection.addPlacemark().apply {
                geometry = Point(marker.point.latitude, marker.point.longitude)
                setIcon(MarkerIcons.get(context, marker.icon))
                userData = marker
            }
        }
        collection.clusterPlacemarks(CLUSTER_RADIUS, maxZoom.toInt())
        current = markers
    }

    /** Подводит камеру так, чтобы метки стопки поместились на экран с запасом по краям. */
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

    /**
     * Метки для стопки: сперва разные эмоции от частой к редкой, потом повторы.
     * Так стопка из одинаковых эмоций все равно выглядит стопкой.
     */
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
        /** Метки ближе этого расстояния на экране склеиваются. */
        const val CLUSTER_RADIUS = 40.0

        /** Больше слоев стопка не рисует. */
        const val STACK_SIZE = 3

        /** Ближе этого точки считаются одним местом. */
        const val SAME_PLACE_METERS = 15f

        /** Отступ от точного вписывания, чтобы крайние метки не резались краем экрана. */
        const val FIT_ZOOM_MARGIN = 0.8f

        val CAMERA_ANIMATION = Animation(Animation.Type.SMOOTH, 0.4f)
    }
}
