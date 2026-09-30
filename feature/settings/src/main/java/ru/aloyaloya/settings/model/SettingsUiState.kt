package ru.aloyaloya.settings.model

import ru.aloyaloya.domain.model.AppTheme

/**
 * Состояние экрана настроек.
 *
 * @property theme Выбранная тема.
 * @property hapticsEnabled Включен ли тактильный отклик.
 */
data class SettingsUiState(
    val theme: AppTheme,
    val hapticsEnabled: Boolean
)
