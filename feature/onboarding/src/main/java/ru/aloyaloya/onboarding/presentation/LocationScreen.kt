package ru.aloyaloya.onboarding.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import ru.aloyaloya.design_system.component.pager.HerePagerIndicator
import ru.aloyaloya.design_system.component.permission.PermissionPage
import ru.aloyaloya.design_system.component.permission.PermissionScreen
import ru.aloyaloya.design_system.component.settings.SettingsPathCard
import ru.aloyaloya.onboarding.R
import ru.aloyaloya.onboarding.presentation.illustration.LocationIllustration

private val locationPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

private fun Context.hasLocationPermission(): Boolean =
    locationPermissions.any { permission ->
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

/**
 * Экран запроса геолокации: запрос, повтор после отказа и путь в настройки.
 *
 * @param onFinished Колбэк завершения онбординга без доступа к геолокации.
 * @param onPermissionGranted Колбэк завершения онбординга с выданным доступом.
 * @param onNavigateBack Колбэк кнопки «Назад».
 */
@Composable
fun LocationScreen(
    onFinished: () -> Unit,
    onPermissionGranted: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    var step by rememberSaveable { mutableStateOf(LocationStep.REQUEST) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.any { it }) {
            // TODO: добавить хаптик подтверждения
            onPermissionGranted()
            return@rememberLauncherForActivityResult
        }

        val rationale = activity?.shouldShowRequestPermissionRationale(
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == true

        step = if (step == LocationStep.REQUEST && rationale) {
            LocationStep.DENIED
        } else {
            LocationStep.BLOCKED
        }
    }

    LifecycleResumeEffect(step) {
        if (step == LocationStep.BLOCKED && context.hasLocationPermission()) {
            onPermissionGranted()
        }
        onPauseOrDispose {}
    }

    val (title, body) = when (step) {
        LocationStep.REQUEST -> R.string.onboarding_location_title to R.string.onboarding_location_body
        LocationStep.DENIED -> R.string.onboarding_location_denied_title to R.string.onboarding_location_denied_body
        LocationStep.BLOCKED -> R.string.onboarding_location_blocked_title to R.string.onboarding_location_blocked_body
    }

    PermissionScreen(
        primaryText = stringResource(
            when (step) {
                LocationStep.REQUEST -> R.string.permission_allow
                LocationStep.DENIED -> R.string.permission_try_again
                LocationStep.BLOCKED -> R.string.permission_open_settings
            }
        ),
        onPrimaryClick = {
            if (step == LocationStep.BLOCKED) {
                context.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                )
            } else {
                permissionLauncher.launch(locationPermissions)
            }
        },
        modifier = modifier,
        secondaryText = stringResource(
            if (step == LocationStep.REQUEST) R.string.permission_not_now else R.string.permission_continue_without
        ),
        onSecondaryClick = onFinished,
        canNavigateBack = true,
        onNavigateBack = onNavigateBack
    ) { contentPadding ->
        PermissionPage(
            contentPadding = contentPadding,
            illustrationBackground = colorResource(R.color.onboarding_map_land),
            illustration = { LocationIllustration(step = step) },
            title = stringResource(title),
            body = stringResource(body),
            pageIndicator = {
                HerePagerIndicator(pageCount = OnboardingSteps, currentPage = OnboardingSteps - 1)
            },
            settingsPath = if (step == LocationStep.BLOCKED) {
                {
                    SettingsPathCard(
                        label = stringResource(R.string.onboarding_location_settings_label),
                        path = stringResource(R.string.onboarding_location_settings_path)
                    )
                }
            } else {
                null
            }
        )
    }
}
