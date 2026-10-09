package ru.aloyaloya.settings.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import ru.aloyaloya.design_system.component.permission.PermissionPage
import ru.aloyaloya.design_system.component.permission.PermissionScreen
import ru.aloyaloya.design_system.component.settings.SettingsPathCard
import ru.aloyaloya.design_system.format.currentLocale
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.settings.R
import java.time.LocalTime

/**
 * Экран запроса уведомлений для напоминания о дне.
 *
 * @param reminderTime Время напоминания.
 * @param permissionRequested Спрашивали ли разрешение раньше.
 * @param onPermissionRequested Колбэк перед показом системного запроса.
 * @param onGranted Колбэк выданного разрешения.
 * @param onDismiss Колбэк отказа, «Не сейчас» и «Назад».
 */
@Composable
fun NotificationsScreen(
    reminderTime: LocalTime,
    permissionRequested: Boolean,
    onPermissionRequested: () -> Unit,
    onGranted: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val haptic = LocalHapticFeedback.current
    var step by rememberSaveable {
        mutableStateOf(
            if (context.notificationsBlocked(activity, permissionRequested)) {
                NotificationsStep.BLOCKED
            } else {
                NotificationsStep.REQUEST
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
            onGranted()
        } else {
            onDismiss()
        }
    }

    LifecycleResumeEffect(step) {
        if (step == NotificationsStep.BLOCKED && context.notificationsEnabled()) {
            onGranted()
        }
        onPauseOrDispose {}
    }

    val blocked = step == NotificationsStep.BLOCKED

    PermissionScreen(
        primaryText = stringResource(
            if (blocked) R.string.notifications_open_settings else R.string.notifications_allow
        ),
        onPrimaryClick = {
            when {
                blocked -> context.openNotificationSettings()
                Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || context.notificationsEnabled() -> onGranted()
                else -> {
                    onPermissionRequested()
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        },
        modifier = modifier,
        secondaryText = stringResource(R.string.notifications_not_now),
        onSecondaryClick = onDismiss,
        canNavigateBack = true,
        onNavigateBack = onDismiss
    ) { contentPadding ->
        PermissionPage(
            contentPadding = contentPadding,
            illustrationBackground = HereTheme.colors.emotions.surprised.soft,
            illustration = { NotificationsIllustration(step = step, time = reminderTime) },
            title = stringResource(
                if (blocked) R.string.notifications_blocked_title else R.string.notifications_title
            ),
            body = if (blocked) {
                stringResource(R.string.notifications_blocked_body)
            } else {
                stringResource(R.string.notifications_body, reminderTime.format(reminderTimeFormat(currentLocale())))
            },
            settingsPath = if (blocked) {
                {
                    SettingsPathCard(
                        label = stringResource(R.string.notifications_settings_label),
                        path = stringResource(R.string.notifications_settings_path)
                    )
                }
            } else {
                null
            }
        )
    }
}

/** Включены ли уведомления приложения в системе. */
internal fun Context.notificationsEnabled(): Boolean =
    NotificationManagerCompat.from(this).areNotificationsEnabled()

/**
 * Запрещены ли уведомления так, что включить их можно только в настройках.
 *
 * @param activity Activity для проверки rationale.
 * @param requested Спрашивали ли разрешение раньше.
 */
internal fun Context.notificationsBlocked(activity: Activity?, requested: Boolean): Boolean {
    if (notificationsEnabled()) return false
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true

    val permission = Manifest.permission.POST_NOTIFICATIONS
    if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) return true

    return requested && activity?.shouldShowRequestPermissionRationale(permission) != true
}

/** Открывает системные настройки уведомлений приложения. */
internal fun Context.openNotificationSettings() {
    startActivity(
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    )
}
