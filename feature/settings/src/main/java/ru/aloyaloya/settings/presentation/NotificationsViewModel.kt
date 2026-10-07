package ru.aloyaloya.settings.presentation

import androidx.lifecycle.ViewModel
import ru.aloyaloya.domain.repository.SettingsRepository
import ru.aloyaloya.domain.scheduler.ReminderScheduler
import java.time.LocalTime
import javax.inject.Inject

/**
 * ViewModel экрана уведомлений.
 */
class NotificationsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    val reminderTime: LocalTime get() = settingsRepository.reminder.value.time

    val permissionRequested: Boolean get() = settingsRepository.notificationsRequested.value

    fun onPermissionRequested() = settingsRepository.markNotificationsRequested()

    fun onGranted() {
        settingsRepository.setReminder(settingsRepository.reminder.value.copy(enabled = true))
        reminderScheduler.sync()
    }
}
