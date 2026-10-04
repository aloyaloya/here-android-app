package ru.aloyaloya.settings.model

import ru.aloyaloya.domain.model.AppLanguage
import ru.aloyaloya.domain.model.AppTheme
import ru.aloyaloya.domain.model.DailyReminder

/**
 * Состояние экрана настроек.
 *
 * @property theme Выбранная тема.
 * @property language Выбранный язык.
 * @property hapticsEnabled Включен ли тактильный отклик.
 * @property reminder Вечернее напоминание.
 * @property memoryCount Сколько воспоминаний сохранено, или `null`, пока база не ответила.
 * @property backupStatus Состояние экспорта и импорта.
 */
data class SettingsUiState(
    val theme: AppTheme,
    val language: AppLanguage,
    val hapticsEnabled: Boolean,
    val reminder: DailyReminder,
    val memoryCount: Int?,
    val backupStatus: BackupStatus
)
