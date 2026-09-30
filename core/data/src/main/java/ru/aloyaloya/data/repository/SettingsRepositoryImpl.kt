package ru.aloyaloya.data.repository

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.aloyaloya.domain.model.AppTheme
import ru.aloyaloya.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Реализация [SettingsRepository] на `SharedPreferences`.
 */
class SettingsRepositoryImpl @Inject constructor(
    context: Context
) : SettingsRepository {

    private val preferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _theme = MutableStateFlow(readTheme())
    override val theme: StateFlow<AppTheme> = _theme.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(
        preferences.getBoolean(KEY_HAPTICS_ENABLED, true)
    )
    override val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    override fun setTheme(theme: AppTheme) {
        _theme.value = theme
        preferences.edit().putString(KEY_THEME_MODE, theme.name).apply()
    }

    override fun setHapticsEnabled(enabled: Boolean) {
        _hapticsEnabled.value = enabled
        preferences.edit().putBoolean(KEY_HAPTICS_ENABLED, enabled).apply()
    }

    private fun readTheme(): AppTheme {
        val stored = preferences.getString(KEY_THEME_MODE, null) ?: return AppTheme.AUTO

        return runCatching { AppTheme.valueOf(stored) }.getOrDefault(AppTheme.AUTO)
    }

    private companion object {
        const val PREFS_NAME = "here_preferences"
        const val KEY_THEME_MODE = "key_theme_mode"
        const val KEY_HAPTICS_ENABLED = "key_haptics_enabled"
    }
}
