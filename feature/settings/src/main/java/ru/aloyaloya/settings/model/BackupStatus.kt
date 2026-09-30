package ru.aloyaloya.settings.model

/**
 * Состояние экспорта и импорта.
 */
sealed interface BackupStatus {
    data object Idle : BackupStatus
    data object Exporting : BackupStatus
    data object Importing : BackupStatus
    data class Exported(val count: Int) : BackupStatus
    data class Imported(val count: Int) : BackupStatus
    data object ExportFailed : BackupStatus
    data object ImportFailed : BackupStatus
}
