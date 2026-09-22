package ru.aloyaloya.map.model

import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.mapkit.model.YandexMapConfig

/**
 * Состояние экрана [ru.aloyaloya.map.presentation.MapScreen].
 *
 * Пока не пришли воспоминания, экран показывает загрузку: карта с пустыми метками
 * и карта с метками — разные кадры, и лучше не показывать первый.
 */
sealed class MapUiState {
    data object Loading : MapUiState()
    data class Content(
        val mapConfig: YandexMapConfig,
        val memories: List<Memory>,
        val picking: PlacePicking? = null
    ) : MapUiState()
}

/**
 * Режим выбора места: карта ездит под неподвижным прицелом.
 *
 * @param address Адрес точки под прицелом или `null`, если определить его не вышло.
 * @param resolving Идет ли сейчас запрос адреса.
 */
data class PlacePicking(
    val address: String? = null,
    val resolving: Boolean = true
)
