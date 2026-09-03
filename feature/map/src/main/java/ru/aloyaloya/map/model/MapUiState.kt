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
        val memories: List<Memory>
    ) : MapUiState()
}