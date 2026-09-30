package ru.aloyaloya.here

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.aloyaloya.design_system.theme.ThemeMode
import ru.aloyaloya.domain.model.AppTheme
import ru.aloyaloya.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * ViewModel корневого экрана приложения.
 *
 * Отдает в UI режим темы из [SettingsRepository]. Меняют его на экране настроек,
 * а сюда он приходит через общий репозиторий.
 */
class MainViewModel @Inject constructor(
    settingsRepository: SettingsRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = settingsRepository.theme
        .map { it.toThemeMode() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = settingsRepository.theme.value.toThemeMode()
        )

    private fun AppTheme.toThemeMode(): ThemeMode = when (this) {
        AppTheme.AUTO -> ThemeMode.AUTO
        AppTheme.LIGHT -> ThemeMode.LIGHT
        AppTheme.DARK -> ThemeMode.DARK
    }
}
