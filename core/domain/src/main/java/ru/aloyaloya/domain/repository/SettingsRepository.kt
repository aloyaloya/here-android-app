package ru.aloyaloya.domain.repository

import kotlinx.coroutines.flow.StateFlow
import ru.aloyaloya.domain.model.AppTheme

/**
 * Репозиторий настроек приложения.
 */
interface SettingsRepository {

    val theme: StateFlow<AppTheme>

    val hapticsEnabled: StateFlow<Boolean>

    fun setTheme(theme: AppTheme)

    fun setHapticsEnabled(enabled: Boolean)
}
