package ru.aloyaloya.settings.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.StringRes
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import ru.aloyaloya.design_system.component.text.HereSectionLabel
import ru.aloyaloya.design_system.component.topbar.HereContextTopAppBar
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.AppTheme
import ru.aloyaloya.settings.R
import ru.aloyaloya.settings.model.BackupStatus
import ru.aloyaloya.settings.model.SettingsUiState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val YANDEX_MAPS_TERMS_URL = "https://yandex.ru/legal/maps_termsofuse/"

internal val ReminderTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

private const val ZIP_MIME_TYPE = "application/zip"

/** Некоторые файловые менеджеры отдают zip как произвольные байты. */
private val ZIP_OPEN_MIME_TYPES = arrayOf(ZIP_MIME_TYPE, "application/octet-stream")

/**
 * Экран настроек.
 *
 * @param uiState Состояние экрана.
 * @param onBackClick Колбэк стрелки назад.
 * @param onThemeSelected Колбэк выбора темы.
 * @param onHapticsChange Колбэк переключения тактильного отклика.
 * @param onReminderEnabledChange Колбэк включения вечернего напоминания.
 * @param onReminderTimeChange Колбэк выбора времени напоминания.
 * @param onExport Колбэк экспорта в выбранный файл.
 * @param onImport Колбэк импорта из выбранного файла.
 * @param onDeleteAllConfirmed Колбэк удаления всех воспоминаний, уже подтвержденного в диалоге.
 * @param modifier Внешний [Modifier] экрана.
 */
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onBackClick: () -> Unit,
    onThemeSelected: (AppTheme) -> Unit,
    onHapticsChange: (Boolean) -> Unit,
    onReminderEnabledChange: (Boolean) -> Unit,
    onReminderTimeChange: (LocalTime) -> Unit,
    onExport: (destination: String) -> Unit,
    onImport: (source: String) -> Unit,
    onDeleteAllConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var deleteAllDialogVisible by rememberSaveable { mutableStateOf(false) }
    var reminderTimeSheetVisible by rememberSaveable { mutableStateOf(false) }
    var notificationsDenied by rememberSaveable { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationsDenied = !granted
        if (granted) onReminderEnabledChange(true)
    }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(ZIP_MIME_TYPE)
    ) { uri -> uri?.let { onExport(it.toString()) } }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { onImport(it.toString()) } }

    val status = uiState.backupStatus
    val busy = status == BackupStatus.Exporting || status == BackupStatus.Importing
    val hasMemories = (uiState.memoryCount ?: 0) > 0
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HereTheme.colors.background)
    ) {
        HereContextTopAppBar(
            title = stringResource(R.string.settings_title),
            navigationContentDescription = stringResource(R.string.settings_back),
            onNavigateBack = onBackClick
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(HereSpacing.xl),
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(
                    horizontal = HereSpacing.l,
                    vertical = HereSpacing.s
                )
        ) {
            SettingsSection(title = stringResource(R.string.settings_section_appearance)) {
                ThemeSelector(
                    selected = uiState.theme,
                    onSelect = onThemeSelected
                )
            }

            SettingsSection(title = stringResource(R.string.settings_section_feedback)) {
                SwitchRow(
                    title = stringResource(R.string.settings_haptics),
                    description = stringResource(R.string.settings_haptics_description),
                    checked = uiState.hapticsEnabled,
                    onCheckedChange = onHapticsChange
                )
            }

            SettingsSection(title = stringResource(R.string.settings_section_reminder)) {
                SwitchRow(
                    title = stringResource(R.string.settings_reminder),
                    description = if (notificationsDenied) {
                        stringResource(R.string.settings_reminder_denied)
                    } else {
                        stringResource(R.string.settings_reminder_description)
                    },
                    checked = uiState.reminder.enabled,
                    onCheckedChange = { enabled ->
                        if (enabled && needsNotificationPermission(context)) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            notificationsDenied = false
                            onReminderEnabledChange(enabled)
                        }
                    },
                    descriptionColor = if (notificationsDenied) {
                        HereTheme.colors.danger
                    } else {
                        HereTheme.colors.textSecondary
                    }
                )
                ActionRow(
                    title = stringResource(R.string.settings_reminder_time),
                    description = uiState.reminder.time.format(ReminderTimeFormat),
                    enabled = uiState.reminder.enabled,
                    onClick = { reminderTimeSheetVisible = true }
                )
            }

            SettingsSection(title = stringResource(R.string.settings_section_data)) {
                ActionRow(
                    title = stringResource(R.string.settings_export),
                    description = exportDescription(status),
                    enabled = !busy && hasMemories,
                    onClick = { exportLauncher.launch("here-${LocalDate.now()}.zip") },
                    descriptionColor = if (status == BackupStatus.ExportFailed) {
                        HereTheme.colors.danger
                    } else {
                        HereTheme.colors.textSecondary
                    }
                )
                ActionRow(
                    title = stringResource(R.string.settings_import),
                    description = importDescription(status),
                    enabled = !busy,
                    onClick = { importLauncher.launch(ZIP_OPEN_MIME_TYPES) },
                    descriptionColor = if (status == BackupStatus.ImportFailed) {
                        HereTheme.colors.danger
                    } else {
                        HereTheme.colors.textSecondary
                    }
                )
                ActionRow(
                    title = stringResource(R.string.settings_delete_all),
                    description = deleteAllDescription(uiState.memoryCount),
                    enabled = !busy && hasMemories,
                    onClick = { deleteAllDialogVisible = true },
                    titleColor = HereTheme.colors.danger
                )
            }

            SettingsSection(title = stringResource(R.string.settings_section_about)) {
                ActionRow(
                    title = stringResource(R.string.settings_version),
                    description = appVersionName(),
                    enabled = false,
                    onClick = {},
                    disabledTitleColor = HereTheme.colors.textPrimary
                )
                ActionRow(
                    title = stringResource(R.string.settings_yandex_maps_terms),
                    description = stringResource(R.string.settings_yandex_maps_terms_description),
                    enabled = true,
                    onClick = { uriHandler.openUri(YANDEX_MAPS_TERMS_URL) }
                )
            }
        }
    }

    if (reminderTimeSheetVisible) {
        ReminderTimeSheet(
            initialTime = uiState.reminder.time,
            onTimeSelected = { time ->
                reminderTimeSheetVisible = false
                onReminderTimeChange(time)
            },
            onDismissRequest = { reminderTimeSheetVisible = false }
        )
    }

    val memoryCount = uiState.memoryCount
    if (deleteAllDialogVisible && memoryCount != null && memoryCount > 0) {
        DeleteAllDialog(
            memoryCount = memoryCount,
            onConfirmClick = {
                deleteAllDialogVisible = false
                onDeleteAllConfirmed()
            },
            onDismissRequest = { deleteAllDialogVisible = false }
        )
    }
}

/**
 * Секция настроек: подпись и плитка с содержимым под ней.
 */
@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(HereSpacing.s)) {
        HereSectionLabel(
            text = title,
            modifier = Modifier.padding(horizontal = HereSpacing.xs)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(HereShape.tile)
                .background(HereTheme.colors.surface)
        ) {
            content()
        }
    }
}

private val ThemeOptions: List<Pair<AppTheme, Int>> = listOf(
    AppTheme.AUTO to R.string.settings_theme_auto,
    AppTheme.LIGHT to R.string.settings_theme_light,
    AppTheme.DARK to R.string.settings_theme_dark
)

@Composable
private fun ThemeSelector(
    selected: AppTheme,
    onSelect: (AppTheme) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(HereSpacing.m),
        modifier = Modifier.padding(HereSpacing.l)
    ) {
        Text(
            text = stringResource(R.string.settings_theme),
            style = MaterialTheme.typography.titleMedium,
            color = HereTheme.colors.textPrimary
        )

        Row(horizontalArrangement = Arrangement.spacedBy(HereSpacing.s)) {
            ThemeOptions.forEach { (theme, labelResId) ->
                ThemeChip(
                    labelResId = labelResId,
                    selected = theme == selected,
                    onClick = { onSelect(theme) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ThemeChip(
    @StringRes labelResId: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors

    Text(
        text = stringResource(labelResId),
        style = MaterialTheme.typography.labelMedium,
        color = if (selected) colors.accent else colors.textSecondary,
        textAlign = TextAlign.Center,
        modifier = modifier
            .clip(HereShape.pill)
            .background(if (selected) colors.accentContainer else colors.surfaceMuted)
            .clickable(onClick = onClick)
            .padding(vertical = HereSpacing.s)
    )
}

@Composable
private fun SwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    descriptionColor: Color = HereTheme.colors.textSecondary
) {
    val colors = HereTheme.colors

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HereSpacing.m),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(HereSpacing.l)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = descriptionColor
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.onAccent,
                checkedTrackColor = colors.accent,
                checkedBorderColor = colors.accent,
                uncheckedThumbColor = colors.textTertiary,
                uncheckedTrackColor = colors.surfaceMuted,
                uncheckedBorderColor = colors.textTertiary
            )
        )
    }
}

@Composable
private fun ActionRow(
    title: String,
    description: String?,
    enabled: Boolean,
    onClick: () -> Unit,
    titleColor: Color = HereTheme.colors.textPrimary,
    descriptionColor: Color = HereTheme.colors.textSecondary,
    disabledTitleColor: Color = HereTheme.colors.textTertiary
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(HereSpacing.l)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = if (enabled) titleColor else disabledTitleColor
        )

        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = descriptionColor
            )
        }
    }
}

@Composable
private fun exportDescription(status: BackupStatus): String = when (status) {
    BackupStatus.Exporting -> stringResource(R.string.settings_export_running)
    is BackupStatus.Exported -> stringResource(R.string.settings_export_done, status.count)
    BackupStatus.ExportFailed -> stringResource(R.string.settings_export_failed)
    else -> stringResource(R.string.settings_export_description)
}

@Composable
private fun importDescription(status: BackupStatus): String = when (status) {
    BackupStatus.Importing -> stringResource(R.string.settings_import_running)
    is BackupStatus.Imported -> if (status.count > 0) {
        stringResource(R.string.settings_import_done, status.count)
    } else {
        stringResource(R.string.settings_import_nothing_new)
    }
    BackupStatus.ImportFailed -> stringResource(R.string.settings_import_failed)
    else -> stringResource(R.string.settings_import_description)
}

@Composable
private fun deleteAllDescription(memoryCount: Int?): String? = when {
    memoryCount == null -> null
    memoryCount > 0 -> pluralStringResource(R.plurals.settings_memory_count, memoryCount, memoryCount)
    else -> stringResource(R.string.settings_delete_all_empty)
}

private fun needsNotificationPermission(context: Context): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
        PackageManager.PERMISSION_GRANTED

@Composable
private fun appVersionName(): String {
    val context = LocalContext.current
    return context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()
}
