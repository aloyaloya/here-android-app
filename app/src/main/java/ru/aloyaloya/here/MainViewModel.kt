package ru.aloyaloya.here

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import ru.aloyaloya.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * ViewModel корневого экрана приложения.
 *
 * Отдает в UI флаг онбординга и настройку тактильного отклика из [SettingsRepository].
 */
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val onboardingCompleted: StateFlow<Boolean> = settingsRepository.onboardingCompleted

    val hapticsEnabled: StateFlow<Boolean> = settingsRepository.hapticsEnabled

    fun completeOnboarding() {
        settingsRepository.completeOnboarding()
    }
}
