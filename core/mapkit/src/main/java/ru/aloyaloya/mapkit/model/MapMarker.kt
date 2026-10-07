package ru.aloyaloya.mapkit.model

import androidx.annotation.DrawableRes

/**
 * Метка на карте.
 *
 * @param id Идентификатор объекта, который метка показывает.
 * @param point Где стоит метка.
 * @param icon Как метка выглядит.
 */
data class MapMarker(
    val id: Long,
    val point: MapPoint,
    val icon: MapMarkerIcon
)

/**
 * Вид метки: картинка из ресурсов.
 *
 * Цвета берутся из ресурсов картинки, поэтому метки сами следуют за темой.
 *
 * @param image Картинка метки.
 */
data class MapMarkerIcon(
    @param:DrawableRes val image: Int
)
