package ru.aloyaloya.map.model

import ru.aloyaloya.mapkit.model.YandexMapConfig

/**
 * Состояние UI экрана [ru.aloyaloya.map.presentation.MapScreen].
 *
 * Описывает возможные состояния экрана в течение его жизненного цикла:
 * загрузку данных и отображение готового содержимого.
 */
sealed class MapUiState {
    data object Loading : MapUiState()
    data class Content(val mapConfig: YandexMapConfig) : MapUiState()
}