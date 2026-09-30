package ru.aloyaloya.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.aloyaloya.domain.model.AppTheme
import ru.aloyaloya.domain.model.DailyReminder
import ru.aloyaloya.domain.repository.BackupRepository
import ru.aloyaloya.domain.repository.MemoryRepository
import ru.aloyaloya.domain.repository.SettingsRepository
import ru.aloyaloya.domain.scheduler.ReminderScheduler
import ru.aloyaloya.settings.model.BackupStatus
import ru.aloyaloya.settings.model.SettingsUiState
import java.time.LocalTime
import javax.inject.Inject

/**
 * ViewModel экрана настроек.
 */
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val memoryRepository: MemoryRepository,
    private val backupRepository: BackupRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    private val backupStatus = MutableStateFlow<BackupStatus>(BackupStatus.Idle)

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.theme,
        settingsRepository.hapticsEnabled,
        settingsRepository.reminder,
        memoryRepository.observeAll().map { it.size },
        backupStatus,
        ::SettingsUiState
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsUiState(
            theme = settingsRepository.theme.value,
            hapticsEnabled = settingsRepository.hapticsEnabled.value,
            reminder = settingsRepository.reminder.value,
            memoryCount = null,
            backupStatus = BackupStatus.Idle
        )
    )

    fun onThemeSelected(theme: AppTheme) = settingsRepository.setTheme(theme)

    fun onHapticsChange(enabled: Boolean) = settingsRepository.setHapticsEnabled(enabled)

    fun onReminderEnabledChange(enabled: Boolean) = updateReminder { it.copy(enabled = enabled) }

    fun onReminderTimeChange(time: LocalTime) = updateReminder { it.copy(time = time) }

    fun onDeleteAllConfirmed() {
        viewModelScope.launch { memoryRepository.deleteAll() }
    }

    fun onExport(destination: String) = runBackup(
        running = BackupStatus.Exporting,
        failed = BackupStatus.ExportFailed
    ) {
        BackupStatus.Exported(backupRepository.export(destination))
    }

    fun onImport(source: String) = runBackup(
        running = BackupStatus.Importing,
        failed = BackupStatus.ImportFailed
    ) {
        BackupStatus.Imported(backupRepository.import(source))
    }

    private fun updateReminder(transform: (DailyReminder) -> DailyReminder) {
        settingsRepository.setReminder(transform(settingsRepository.reminder.value))
        reminderScheduler.sync()
    }

    private fun runBackup(
        running: BackupStatus,
        failed: BackupStatus,
        block: suspend () -> BackupStatus
    ) {
        if (backupStatus.value == BackupStatus.Exporting || backupStatus.value == BackupStatus.Importing) return

        backupStatus.value = running
        viewModelScope.launch {
            backupStatus.value = try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                failed
            }
        }
    }
}
