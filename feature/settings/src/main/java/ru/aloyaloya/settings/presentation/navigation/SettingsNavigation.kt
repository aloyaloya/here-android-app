package ru.aloyaloya.settings.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.aloyaloya.settings.di.SettingsComponent
import ru.aloyaloya.settings.presentation.SettingsScreen
import ru.aloyaloya.settings.presentation.SettingsViewModel
import ru.aloyaloya.ui.di.ComponentProvider

@Serializable
/** Маршрут экрана настроек в графе навигации. */
data object SettingsRoute

/**
 * Выполняет переход на экран настроек.
 *
 * Настройки открываются поверх раздела, а не вместо него: стрелка назад
 * возвращает туда, откуда пришли.
 */
fun NavController.navigateToSettings() =
    navigate(route = SettingsRoute) { launchSingleTop = true }

/**
 * Регистрирует экран настроек как destination в [NavGraphBuilder].
 *
 * @param onBackClick Колбэк выхода с экрана.
 */
fun NavGraphBuilder.settingsScreen(
    onBackClick: () -> Unit
) {
    composable<SettingsRoute> { navBackStackEntry ->

        val context = LocalContext.current.applicationContext

        val factory = (context as ComponentProvider)
            .provideComponent("settings", SettingsComponent::class)
            .viewModelFactory

        val viewModel = viewModel<SettingsViewModel>(
            viewModelStoreOwner = navBackStackEntry,
            factory = factory
        )

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        SettingsScreen(
            uiState = uiState,
            onBackClick = onBackClick,
            onThemeSelected = viewModel::onThemeSelected,
            onLanguageSelected = viewModel::onLanguageSelected,
            onHapticsChange = viewModel::onHapticsChange,
            onReminderEnabledChange = viewModel::onReminderEnabledChange,
            onReminderTimeChange = viewModel::onReminderTimeChange,
            onExport = viewModel::onExport,
            onImport = viewModel::onImport,
            onDeleteAllConfirmed = viewModel::onDeleteAllConfirmed
        )
    }
}
