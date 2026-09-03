package ru.aloyaloya.mapkit.internal

import android.content.Context
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import ru.aloyaloya.mapkit.model.MapMarker

/**
 * Держит метки на карте.
 *
 * Метки живут в своей коллекции, чтобы их можно было очистить, не задев остальные
 * объекты карты.
 */
internal class MarkersBinder(
    private val mapView: MapView,
    private val context: Context
) {

    private val collection = mapView.mapWindow.map.mapObjects.addCollection()

    private var current: List<MapMarker> = emptyList()

    fun apply(markers: List<MapMarker>) {
        if (markers == current) return

        collection.clear()
        markers.forEach { marker ->
            collection.addPlacemark().apply {
                geometry = Point(marker.point.latitude, marker.point.longitude)
                setIcon(MarkerIcons.get(context, marker.icon))
            }
        }
        current = markers
    }
}
