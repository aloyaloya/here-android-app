package ru.aloyaloya.mapkit.model

import androidx.compose.ui.graphics.Color

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
 * Вид метки: эмодзи в круге с обводкой.
 *
 * Цвета модуль берет снаружи, чтобы метки следовали за темой приложения.
 *
 * @param emoji Эмодзи в центре метки.
 * @param fill Цвет круга.
 * @param outline Цвет обводки.
 */
data class MapMarkerIcon(
    val emoji: String,
    val fill: Color,
    val outline: Color
)
