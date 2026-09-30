package ru.aloyaloya.settings.presentation

import androidx.annotation.StringRes
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import ru.aloyaloya.design_system.component.text.HereSectionLabel
import ru.aloyaloya.design_system.component.topbar.HereContextTopAppBar
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.AppTheme
import ru.aloyaloya.settings.R
import ru.aloyaloya.settings.model.SettingsUiState

/**
 * Экран настроек.
 *
 * @param uiState Состояние экрана.
 * @param onBackClick Колбэк стрелки назад.
 * @param onThemeSelected Колбэк выбора темы.
 * @param onHapticsChange Колбэк переключения тактильного отклика.
 * @param onDeleteAllConfirmed Колбэк удаления всех воспоминаний, уже подтвержденного в диалоге.
 * @param modifier Внешний [Modifier] экрана.
 */
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onBackClick: () -> Unit,
    onThemeSelected: (AppTheme) -> Unit,
    onHapticsChange: (Boolean) -> Unit,
    onDeleteAllConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var deleteAllDialogVisible by rememberSaveable { mutableStateOf(false) }

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

            SettingsSection(title = stringResource(R.string.settings_section_data)) {
                DeleteAllRow(
                    memoryCount = uiState.memoryCount,
                    onClick = { deleteAllDialogVisible = true }
                )
            }
        }
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
    onCheckedChange: (Boolean) -> Unit
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
                color = colors.textSecondary
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
private fun DeleteAllRow(
    memoryCount: Int?,
    onClick: () -> Unit
) {
    val colors = HereTheme.colors
    val enabled = memoryCount != null && memoryCount > 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(HereSpacing.l)
    ) {
        Text(
            text = stringResource(R.string.settings_delete_all),
            style = MaterialTheme.typography.titleMedium,
            color = if (enabled) colors.danger else colors.textTertiary
        )

        if (memoryCount != null) {
            Text(
                text = if (memoryCount > 0) {
                    pluralStringResource(R.plurals.settings_memory_count, memoryCount, memoryCount)
                } else {
                    stringResource(R.string.settings_delete_all_empty)
                },
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }
    }
}
