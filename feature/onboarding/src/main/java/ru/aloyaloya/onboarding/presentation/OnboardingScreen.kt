package ru.aloyaloya.onboarding.presentation

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import ru.aloyaloya.design_system.component.pager.HerePagerIndicator
import ru.aloyaloya.design_system.component.permission.PermissionPage
import ru.aloyaloya.design_system.component.permission.PermissionScreen
import ru.aloyaloya.design_system.component.permission.PermissionSkipButton
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.onboarding.R
import ru.aloyaloya.onboarding.presentation.illustration.CalendarIllustration
import ru.aloyaloya.onboarding.presentation.illustration.FeaturePins
import ru.aloyaloya.onboarding.presentation.illustration.MapIllustration
import ru.aloyaloya.onboarding.presentation.illustration.MemoryIllustration
import ru.aloyaloya.onboarding.presentation.illustration.NearbyPins
import ru.aloyaloya.onboarding.presentation.illustration.UserLocationOverlay

private const val PAGE_COUNT = 4
private const val LOCATION_PAGE = PAGE_COUNT - 1

private val locationPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

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
    val pagerState = rememberPagerState { PAGE_COUNT }
    val scope = rememberCoroutineScope()
    val isLocationPage = pagerState.currentPage == LOCATION_PAGE

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { onFinished() }

    BackHandler(enabled = pagerState.currentPage > 0) {
        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
    }

    PermissionScreen(
        primaryText = stringResource(
            if (isLocationPage) R.string.permission_allow else R.string.onboarding_next
        ),
        onPrimaryClick = {
            if (isLocationPage) {
                permissionLauncher.launch(locationPermissions)
            } else {
                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
            }
        },
        modifier = modifier,
        secondaryText = if (isLocationPage) stringResource(R.string.permission_not_now) else null,
        onSecondaryClick = onFinished,
        topAction = {
            if (!isLocationPage) {
                PermissionSkipButton(
                    text = stringResource(R.string.onboarding_skip),
                    onClick = { scope.launch { pagerState.animateScrollToPage(LOCATION_PAGE) } }
                )
            }
        }
    ) { contentPadding ->
        HorizontalPager(state = pagerState) { page ->
            OnboardingPage(page = page, contentPadding = contentPadding)
        }
    }
}

/**
 * Страница онбординга.
 *
 * @param page Номер страницы от 0 до [LOCATION_PAGE].
 * @param contentPadding Отступы от [PermissionScreen].
 */
@Composable
private fun OnboardingPage(page: Int, contentPadding: PaddingValues) {
    val emotions = HereTheme.colors.emotions
    val mapLand = colorResource(R.color.onboarding_map_land)

    // TODO: заменить временные иллюстрации на персонажей
    val illustrationModifier = Modifier
        .padding(top = contentPadding.calculateTopPadding())
        .height(HereSize.OnboardingIllustration.canvasHeight)

    val (background, title, body) = when (page) {
        0 -> Triple(mapLand, R.string.onboarding_map_title, R.string.onboarding_map_body)
        1 -> Triple(emotions.tender.soft, R.string.onboarding_memory_title, R.string.onboarding_memory_body)
        2 -> Triple(emotions.calm.soft, R.string.onboarding_calendar_title, R.string.onboarding_calendar_body)
        else -> Triple(mapLand, R.string.onboarding_location_title, R.string.onboarding_location_body)
    }

    PermissionPage(
        contentPadding = contentPadding,
        illustrationBackground = background,
        illustration = {
            when (page) {
                0 -> MapIllustration(pins = FeaturePins, modifier = illustrationModifier)
                1 -> MemoryIllustration(modifier = illustrationModifier)
                2 -> CalendarIllustration(modifier = illustrationModifier)
                else -> MapIllustration(pins = NearbyPins, modifier = illustrationModifier) {
                    UserLocationOverlay()
                }
            }
        },
        title = stringResource(title),
        body = stringResource(body),
        pageIndicator = {
            HerePagerIndicator(pageCount = PAGE_COUNT, currentPage = page)
        }
    )
}
