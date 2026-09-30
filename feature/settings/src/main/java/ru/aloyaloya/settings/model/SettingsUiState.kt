package ru.aloyaloya.settings.model

import ru.aloyaloya.domain.model.AppTheme

/**
 * Состояние экрана настроек.
 *
 * @property theme Выбранная тема.
 * @property hapticsEnabled Включен ли тактильный отклик.
 * @property memoryCount Сколько воспоминаний сохранено, или `null`, пока база не ответила.
 */
data class SettingsUiState(
    val theme: AppTheme,
    val hapticsEnabled: Boolean,
    val memoryCount: Int?
)
