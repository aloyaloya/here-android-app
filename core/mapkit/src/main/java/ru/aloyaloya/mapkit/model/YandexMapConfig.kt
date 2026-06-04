package ru.aloyaloya.mapkit.model

/** Настройки зума карты. */
data class YandexMapConfig(
    val minZoom: Float = 4f,
    val maxZoom: Float = 19f,
    val userLocationZoom: Float = 15f,
) {
    companion object {
        val Default = YandexMapConfig()
    }
}