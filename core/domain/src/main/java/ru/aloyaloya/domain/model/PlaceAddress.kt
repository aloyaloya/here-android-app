package ru.aloyaloya.domain.model

/**
 * Адрес точки на карте.
 *
 * @property line Короткий адрес вида «Малая Бронная, 22».
 * @property locality Населенный пункт, если геокодер его знает.
 */
data class PlaceAddress(
    val line: String,
    val locality: String?
)
