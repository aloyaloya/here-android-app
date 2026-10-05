package ru.aloyaloya.onboarding.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import kotlinx.coroutines.launch
import ru.aloyaloya.design_system.component.button.HereTextButton
import ru.aloyaloya.design_system.component.pager.HerePagerIndicator
import ru.aloyaloya.design_system.component.permission.PermissionPage
import ru.aloyaloya.design_system.component.permission.PermissionScreen
import ru.aloyaloya.design_system.component.settings.SettingsPathCard
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.onboarding.R
import ru.aloyaloya.onboarding.presentation.illustration.CalendarIllustration
import ru.aloyaloya.onboarding.presentation.illustration.LocationIllustration
import ru.aloyaloya.onboarding.presentation.illustration.MemoryIllustration
import ru.aloyaloya.onboarding.presentation.illustration.WelcomeIllustration

private const val PAGE_COUNT = 4
private const val LOCATION_PAGE = PAGE_COUNT - 1

private val locationPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

private fun Context.hasLocationPermission(): Boolean =
    locationPermissions.any { permission ->
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

/**
 * Онбординг: три страницы о возможностях и страница запроса геолокации.
 *
 * @param onFinished вызывается, когда пользователь прошел последнюю страницу
 */
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val pagerState = rememberPagerState { PAGE_COUNT }
    val scope = rememberCoroutineScope()
    val isLocationPage = pagerState.currentPage == LOCATION_PAGE
    var locationStep by rememberSaveable { mutableStateOf(LocationStep.REQUEST) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.any { it }) {
            onFinished()
            return@rememberLauncherForActivityResult
        }

        val rationale = activity?.shouldShowRequestPermissionRationale(
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == true

        locationStep = if (locationStep == LocationStep.REQUEST && rationale) {
            LocationStep.DENIED
        } else {
            LocationStep.BLOCKED
        }
    }

    LifecycleResumeEffect(locationStep) {
        if (locationStep == LocationStep.BLOCKED && context.hasLocationPermission()) {
            onFinished()
        }
        onPauseOrDispose {}
    }

    val canGoBack = pagerState.currentPage > 0
    val goBack: () -> Unit = {
        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
    }

    BackHandler(enabled = canGoBack, onBack = goBack)

    PermissionScreen(
        primaryText = stringResource(
            when {
                !isLocationPage -> R.string.onboarding_next
                locationStep == LocationStep.REQUEST -> R.string.permission_allow
                locationStep == LocationStep.DENIED -> R.string.permission_try_again
                else -> R.string.permission_open_settings
            }
        ),
        onPrimaryClick = {
            when {
                !isLocationPage -> scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }

                locationStep == LocationStep.BLOCKED -> context.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                )

                else -> permissionLauncher.launch(locationPermissions)
            }
        },
        modifier = modifier,
        secondaryText = when {
            !isLocationPage -> null
            locationStep == LocationStep.REQUEST -> stringResource(R.string.permission_not_now)
            else -> stringResource(R.string.permission_continue_without)
        },
        onSecondaryClick = onFinished,
        onNavigateBack = goBack.takeIf { canGoBack },
        topAction = {
            if (!isLocationPage) {
                HereTextButton(
                    text = stringResource(R.string.onboarding_skip),
                    onClick = { scope.launch { pagerState.animateScrollToPage(LOCATION_PAGE) } }
                )
            }
        }
    ) { contentPadding ->
        HorizontalPager(state = pagerState) { page ->
            val active = pagerState.currentPage == page

            if (page == LOCATION_PAGE) {
                LocationPage(step = locationStep, active = active, contentPadding = contentPadding)
            } else {
                FeaturePage(page = page, active = active, contentPadding = contentPadding)
            }
        }
    }
}

/**
 * Страница о возможности приложения.
 *
 * @param page Номер страницы до [LOCATION_PAGE].
 * @param active Текущая ли страница.
 * @param contentPadding Отступы от [PermissionScreen].
 */
@Composable
private fun FeaturePage(page: Int, active: Boolean, contentPadding: PaddingValues) {
    val emotions = HereTheme.colors.emotions

    val (background, title, body) = when (page) {
        0 -> Triple(colorResource(R.color.onboarding_map_land), R.string.onboarding_map_title, R.string.onboarding_map_body)
        1 -> Triple(emotions.tender.soft, R.string.onboarding_memory_title, R.string.onboarding_memory_body)
        else -> Triple(emotions.calm.soft, R.string.onboarding_calendar_title, R.string.onboarding_calendar_body)
    }

    PermissionPage(
        contentPadding = contentPadding,
        illustrationBackground = background,
        illustration = {
            when (page) {
                0 -> WelcomeIllustration(active = active)
                1 -> MemoryIllustration(active = active)
                else -> CalendarIllustration(active = active)
            }
        },
        title = stringResource(title),
        body = stringResource(body),
        pageIndicator = {
            HerePagerIndicator(pageCount = PAGE_COUNT, currentPage = page)
        }
    )
}

/**
 * Страница запроса геолокации.
 *
 * @param step Шаг запроса.
 * @param active Текущая ли страница.
 * @param contentPadding Отступы от [PermissionScreen].
 */
@Composable
private fun LocationPage(step: LocationStep, active: Boolean, contentPadding: PaddingValues) {
    val (title, body) = when (step) {
        LocationStep.REQUEST -> R.string.onboarding_location_title to R.string.onboarding_location_body
        LocationStep.DENIED -> R.string.onboarding_location_denied_title to R.string.onboarding_location_denied_body
        LocationStep.BLOCKED -> R.string.onboarding_location_blocked_title to R.string.onboarding_location_blocked_body
    }

    PermissionPage(
        contentPadding = contentPadding,
        illustrationBackground = colorResource(R.color.onboarding_map_land),
        illustration = { LocationIllustration(step = step, active = active) },
        title = stringResource(title),
        body = stringResource(body),
        pageIndicator = {
            HerePagerIndicator(pageCount = PAGE_COUNT, currentPage = LOCATION_PAGE)
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
