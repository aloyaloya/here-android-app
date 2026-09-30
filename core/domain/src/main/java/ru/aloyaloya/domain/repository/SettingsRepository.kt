package ru.aloyaloya.domain.repository

import kotlinx.coroutines.flow.StateFlow
import ru.aloyaloya.domain.model.AppTheme
import ru.aloyaloya.domain.model.DailyReminder

/**
 * Репозиторий настроек приложения.
 */
interface SettingsRepository {

    val theme: StateFlow<AppTheme>

    val hapticsEnabled: StateFlow<Boolean>

    val reminder: StateFlow<DailyReminder>

    fun setTheme(theme: AppTheme)

    fun setHapticsEnabled(enabled: Boolean)

    fun setReminder(reminder: DailyReminder)
}
