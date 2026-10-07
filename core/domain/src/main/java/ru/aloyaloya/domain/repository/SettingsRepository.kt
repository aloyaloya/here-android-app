package ru.aloyaloya.domain.repository

import kotlinx.coroutines.flow.StateFlow
import ru.aloyaloya.domain.model.AppLanguage
import ru.aloyaloya.domain.model.AppTheme
import ru.aloyaloya.domain.model.DailyReminder

/**
 * Репозиторий настроек приложения.
 */
interface SettingsRepository {

    val theme: StateFlow<AppTheme>

    val hapticsEnabled: StateFlow<Boolean>

    val reminder: StateFlow<DailyReminder>

    val language: AppLanguage

    val onboardingCompleted: StateFlow<Boolean>

    val notificationsRequested: StateFlow<Boolean>

    fun setTheme(theme: AppTheme)

    fun setHapticsEnabled(enabled: Boolean)

    fun setReminder(reminder: DailyReminder)

    fun setLanguage(language: AppLanguage)

    fun completeOnboarding()

    fun markNotificationsRequested()
}
