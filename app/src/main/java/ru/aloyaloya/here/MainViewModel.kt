package ru.aloyaloya.here

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.aloyaloya.design_system.theme.ThemeMode
import javax.inject.Inject

/**
 * ViewModel корневого экрана приложения.
 *
 * Хранит режим темы, отдает его в UI через [themeMode] и сохраняет
 * в `SharedPreferences`, чтобы выбор восстанавливался после перезапуска.
 * До первого касания переключателя режим равен [ThemeMode.AUTO].
 */
class MainViewModel @Inject constructor(
    context: Context
) : ViewModel() {

    private val sharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _themeMode = MutableStateFlow(readThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    /**
     * Переключает тему на противоположную и сохраняет выбор.
     *
     * @param darkTheme Тема, которую пользователь видит сейчас. При [ThemeMode.AUTO]
     * она известна только UI, поэтому приходит снаружи, а не берется из [themeMode].
     */
    fun onThemeChange(darkTheme: Boolean) {
        val newMode = if (darkTheme) ThemeMode.LIGHT else ThemeMode.DARK

        _themeMode.value = newMode
        sharedPreferences.edit {
            putString(KEY_THEME_MODE, newMode.name)
        }
    }

    private fun readThemeMode(): ThemeMode {
        val stored = sharedPreferences.getString(KEY_THEME_MODE, null) ?: return ThemeMode.AUTO

        return runCatching { ThemeMode.valueOf(stored) }.getOrDefault(ThemeMode.AUTO)
    }

    private companion object {
        const val PREFS_NAME = "here_preferences"
        const val KEY_THEME_MODE = "key_theme_mode"
    }
}