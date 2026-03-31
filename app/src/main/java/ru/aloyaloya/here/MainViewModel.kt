package ru.aloyaloya.here

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import androidx.core.content.edit

/**
 * ViewModel корневого экрана приложения.
 *
 * Хранит пользовательский выбор темы (`light`/`dark`), отдает его в UI
 * через [darkTheme] и сохраняет в `SharedPreferences`, чтобы выбор
 * восстанавливался после перезапуска приложения.
 */
class MainViewModel @Inject constructor(
    context: Context
) : ViewModel() {

    private val sharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _darkTheme = MutableStateFlow(
        sharedPreferences.getBoolean(KEY_DARK_THEME, false)
    )
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    /** Переключает тему и сохраняет новое значение в постоянное хранилище. */
    fun onThemeChange() {
        val newTheme = !_darkTheme.value
        _darkTheme.value = newTheme
        sharedPreferences.edit {
            putBoolean(KEY_DARK_THEME, newTheme)
        }
    }

    private companion object {
        const val PREFS_NAME = "here_preferences"
        const val KEY_DARK_THEME = "key_dark_theme"
    }
}