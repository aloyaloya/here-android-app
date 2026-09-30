package ru.aloyaloya.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.aloyaloya.domain.model.AppTheme
import ru.aloyaloya.domain.repository.MemoryRepository
import ru.aloyaloya.domain.repository.SettingsRepository
import ru.aloyaloya.settings.model.SettingsUiState
import javax.inject.Inject

/**
 * ViewModel экрана настроек.
 */
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val memoryRepository: MemoryRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.theme,
        settingsRepository.hapticsEnabled,
        memoryRepository.observeAll().map { it.size }
    ) { theme, hapticsEnabled, memoryCount ->
        SettingsUiState(theme, hapticsEnabled, memoryCount)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsUiState(
            theme = settingsRepository.theme.value,
            hapticsEnabled = settingsRepository.hapticsEnabled.value,
            memoryCount = null
        )
    )

    fun onThemeSelected(theme: AppTheme) = settingsRepository.setTheme(theme)

    fun onHapticsChange(enabled: Boolean) = settingsRepository.setHapticsEnabled(enabled)

    fun onDeleteAllConfirmed() {
        viewModelScope.launch { memoryRepository.deleteAll() }
    }
}
