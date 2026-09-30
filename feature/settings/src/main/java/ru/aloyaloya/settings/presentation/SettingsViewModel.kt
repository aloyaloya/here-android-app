package ru.aloyaloya.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import ru.aloyaloya.domain.model.AppTheme
import ru.aloyaloya.domain.repository.SettingsRepository
import ru.aloyaloya.settings.model.SettingsUiState
import javax.inject.Inject

/**
 * ViewModel экрана настроек.
 */
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.theme,
        settingsRepository.hapticsEnabled,
        ::SettingsUiState
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsUiState(
            theme = settingsRepository.theme.value,
            hapticsEnabled = settingsRepository.hapticsEnabled.value
        )
    )

    fun onThemeSelected(theme: AppTheme) = settingsRepository.setTheme(theme)

    fun onHapticsChange(enabled: Boolean) = settingsRepository.setHapticsEnabled(enabled)
}
