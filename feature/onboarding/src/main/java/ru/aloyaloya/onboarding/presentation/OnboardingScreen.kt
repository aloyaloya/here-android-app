package ru.aloyaloya.onboarding.presentation

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import ru.aloyaloya.design_system.component.button.HereTextButton
import ru.aloyaloya.design_system.component.pager.HerePagerIndicator
import ru.aloyaloya.design_system.component.permission.PermissionContent
import ru.aloyaloya.design_system.component.permission.PermissionScreen
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
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
        topBar = {
            OnboardingTopBar(
                pagerState = pagerState,
                onSkip = { scope.launch { pagerState.animateScrollToPage(LOCATION_PAGE) } }
            )
        },
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
        onSecondaryClick = onFinished
    ) {
        HorizontalPager(state = pagerState) { page ->
            OnboardingPage(page)
        }
    }
}

/**
 * Верхняя строка: индикатор страниц и «Пропустить» до страницы геолокации.
 *
 * @param onSkip переход сразу на страницу геолокации
 */
@Composable
private fun OnboardingTopBar(
    pagerState: PagerState,
    onSkip: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = HereSize.TopAppBar.contentPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HerePagerIndicator(
            pageCount = PAGE_COUNT,
            currentPage = pagerState.currentPage,
            modifier = Modifier.padding(start = HereSpacing.screenHorizontal)
        )
        Spacer(Modifier.weight(1f))
        if (pagerState.currentPage < LOCATION_PAGE) {
            HereTextButton(
                text = stringResource(R.string.onboarding_skip),
                onClick = onSkip
            )
        }
    }
}

/**
 * Содержимое страницы онбординга.
 *
 * @param page номер страницы от 0 до [LOCATION_PAGE]
 */
@Composable
private fun OnboardingPage(page: Int) {
    when (page) {
        0 -> PermissionContent(
            illustration = { MapIllustration(pins = FeaturePins) },
            title = stringResource(R.string.onboarding_map_title),
            body = stringResource(R.string.onboarding_map_body)
        )

        1 -> PermissionContent(
            illustration = { MemoryIllustration() },
            title = stringResource(R.string.onboarding_memory_title),
            body = stringResource(R.string.onboarding_memory_body)
        )

        2 -> PermissionContent(
            illustration = { CalendarIllustration() },
            title = stringResource(R.string.onboarding_calendar_title),
            body = stringResource(R.string.onboarding_calendar_body)
        )

        else -> PermissionContent(
            illustration = { MapIllustration(pins = NearbyPins) { UserLocationOverlay() } },
            title = stringResource(R.string.onboarding_location_title),
            body = stringResource(R.string.onboarding_location_body)
        )
    }
}