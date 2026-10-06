package ru.aloyaloya.data.repository

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.aloyaloya.domain.model.AppLanguage
import ru.aloyaloya.domain.model.AppTheme
import ru.aloyaloya.domain.model.DailyReminder
import ru.aloyaloya.domain.repository.SettingsRepository
import java.time.LocalTime
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

    private val _reminder = MutableStateFlow(readReminder())
    override val reminder: StateFlow<DailyReminder> = _reminder.asStateFlow()

    // TODO: вернуть preferences.getBoolean(KEY_ONBOARDING_COMPLETED, false) после проверки онбординга
    private val _onboardingCompleted = MutableStateFlow(false)
    override val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    init {
        applyNightMode(_theme.value)
    }

    override val language: AppLanguage
        get() = when (AppCompatDelegate.getApplicationLocales()[0]?.language) {
            LANGUAGE_RUSSIAN -> AppLanguage.RUSSIAN
            LANGUAGE_ENGLISH -> AppLanguage.ENGLISH
            else -> AppLanguage.SYSTEM
        }

    override fun setTheme(theme: AppTheme) {
        _theme.value = theme
        preferences.edit().putString(KEY_THEME_MODE, theme.name).apply()
        applyNightMode(theme)
    }

    override fun setHapticsEnabled(enabled: Boolean) {
        _hapticsEnabled.value = enabled
        preferences.edit().putBoolean(KEY_HAPTICS_ENABLED, enabled).apply()
    }

    override fun setReminder(reminder: DailyReminder) {
        _reminder.value = reminder
        preferences.edit()
            .putBoolean(KEY_REMINDER_ENABLED, reminder.enabled)
            .putInt(KEY_REMINDER_MINUTE_OF_DAY, reminder.time.hour * 60 + reminder.time.minute)
            .apply()
    }

    override fun setLanguage(language: AppLanguage) {
        val tags = when (language) {
            AppLanguage.SYSTEM -> ""
            AppLanguage.RUSSIAN -> LANGUAGE_RUSSIAN
            AppLanguage.ENGLISH -> LANGUAGE_ENGLISH
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tags))
    }

    override fun completeOnboarding() {
        _onboardingCompleted.value = true
        preferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
    }

    private fun applyNightMode(theme: AppTheme) {
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                AppTheme.AUTO -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                AppTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                AppTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            }
        )
    }

    private fun readTheme(): AppTheme {
        val stored = preferences.getString(KEY_THEME_MODE, null) ?: return AppTheme.AUTO

        return runCatching { AppTheme.valueOf(stored) }.getOrDefault(AppTheme.AUTO)
    }

    private fun readReminder(): DailyReminder {
        val minuteOfDay =
            preferences.getInt(KEY_REMINDER_MINUTE_OF_DAY, DEFAULT_REMINDER_MINUTE_OF_DAY)

        return DailyReminder(
            enabled = preferences.getBoolean(KEY_REMINDER_ENABLED, false),
            time = LocalTime.of(minuteOfDay / 60, minuteOfDay % 60)
        )
    }

    private companion object {
        const val PREFS_NAME = "here_preferences"
        const val KEY_THEME_MODE = "key_theme_mode"
        const val KEY_HAPTICS_ENABLED = "key_haptics_enabled"
        const val KEY_REMINDER_ENABLED = "key_reminder_enabled"
        const val KEY_REMINDER_MINUTE_OF_DAY = "key_reminder_minute_of_day"
        const val KEY_ONBOARDING_COMPLETED = "key_onboarding_completed"
        const val DEFAULT_REMINDER_MINUTE_OF_DAY = 21 * 60
        const val LANGUAGE_RUSSIAN = "ru"
        const val LANGUAGE_ENGLISH = "en"
    }
}
