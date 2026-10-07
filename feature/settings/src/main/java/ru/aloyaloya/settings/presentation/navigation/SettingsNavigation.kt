package ru.aloyaloya.settings.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.aloyaloya.settings.di.SettingsComponent
import ru.aloyaloya.settings.presentation.NotificationsScreen
import ru.aloyaloya.settings.presentation.NotificationsViewModel
import ru.aloyaloya.settings.presentation.SettingsScreen
import ru.aloyaloya.settings.presentation.SettingsViewModel
import ru.aloyaloya.ui.di.ComponentProvider

@Serializable
/** Маршрут экрана настроек в графе навигации. */
data object SettingsRoute

/** Маршрут экрана запроса уведомлений. */
@Serializable
data object NotificationsRoute

/**
 * Выполняет переход на экран настроек.
 *
 * Настройки открываются поверх раздела, а не вместо него: стрелка назад
 * возвращает туда, откуда пришли.
 */
fun NavController.navigateToSettings() =
    navigate(route = SettingsRoute) { launchSingleTop = true }

/**
 * Выполняет переход на экран запроса уведомлений.
 */
fun NavController.navigateToNotifications() =
    navigate(route = NotificationsRoute) { launchSingleTop = true }

/**
 * Регистрирует экран настроек как destination в [NavGraphBuilder].
 *
 * @param onBackClick Колбэк выхода с экрана.
 * @param onNotificationsRequest Колбэк включения напоминания.
 */
fun NavGraphBuilder.settingsScreen(
    onBackClick: () -> Unit,
    onNotificationsRequest: () -> Unit
) {
    composable<SettingsRoute>(
        exitTransition = { if (targetState.isNotifications) ExitTransition.KeepUntilTransitionsFinished else null },
        popEnterTransition = { if (initialState.isNotifications) EnterTransition.None else null }
    ) { navBackStackEntry ->

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
            onNotificationsRequest = dropUnlessResumed(block = onNotificationsRequest),
            onReminderTimeChange = viewModel::onReminderTimeChange,
            onExport = viewModel::onExport,
            onImport = viewModel::onImport,
            onDeleteAllConfirmed = viewModel::onDeleteAllConfirmed
        )
    }
}

/**
 * Регистрирует экран запроса уведомлений как destination в [NavGraphBuilder].
 *
 * @param onClose Колбэк выхода с экрана.
 */
fun NavGraphBuilder.notificationsScreen(
    onClose: () -> Unit
) {
    composable<NotificationsRoute>(
        enterTransition = { slideIntoContainer(SlideDirection.Up) },
        popExitTransition = { slideOutOfContainer(SlideDirection.Down) }
    ) { navBackStackEntry ->

        val context = LocalContext.current.applicationContext

        val factory = (context as ComponentProvider)
            .provideComponent("settings", SettingsComponent::class)
            .viewModelFactory

        val viewModel = viewModel<NotificationsViewModel>(
            viewModelStoreOwner = navBackStackEntry,
            factory = factory
        )

        val close = dropUnlessResumed(block = onClose)

        NotificationsScreen(
            reminderTime = viewModel.reminderTime,
            permissionRequested = viewModel.permissionRequested,
            onPermissionRequested = viewModel::onPermissionRequested,
            onGranted = {
                viewModel.onGranted()
                onClose()
            },
            onDismiss = close
        )
    }
}

/** Экран запроса уведомлений. */
private val NavBackStackEntry.isNotifications: Boolean
    get() = destination.hasRoute<NotificationsRoute>()
