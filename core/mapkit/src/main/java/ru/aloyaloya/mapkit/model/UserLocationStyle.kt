package ru.aloyaloya.mapkit.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Цвета маркера текущего положения.
 *
 * Модуль не знает про палитру приложения, поэтому цвета передает экран.
 *
 * @property fill Заливка стрелки и точки.
 * @property outline Обводка вокруг них: отделяет маркер от карты.
 * @property accuracy Заливка круга точности.
 */
@Immutable
data class UserLocationStyle(
    val fill: Color,
    val outline: Color,
    val accuracy: Color
)