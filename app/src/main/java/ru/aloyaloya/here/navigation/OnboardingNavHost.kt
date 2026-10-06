package ru.aloyaloya.here.navigation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import ru.aloyaloya.onboarding.presentation.OnboardingFeature
import ru.aloyaloya.onboarding.presentation.navigation.OnboardingFeatureRoute
import ru.aloyaloya.onboarding.presentation.navigation.navigateToOnboardingFeature
import ru.aloyaloya.onboarding.presentation.navigation.navigateToOnboardingLocation
import ru.aloyaloya.onboarding.presentation.navigation.onboardingFeatureScreen
import ru.aloyaloya.onboarding.presentation.navigation.onboardingLocationScreen

/**
 * Навигационный граф онбординга: экраны о возможностях и экран запроса геолокации.
 *
 * @param onFinished Колбэк: пользователь прошел онбординг без доступа к геолокации.
 * @param onPermissionGranted Колбэк: пользователь прошел онбординг и выдал доступ.
 * @param modifier Модификатор для настройки внешнего вида контейнера навигации.
 */
@Composable
fun OnboardingNavHost(
    onFinished: () -> Unit,
    onPermissionGranted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = OnboardingFeatureRoute(OnboardingFeature.entries.first()),
        modifier = modifier,
        enterTransition = { slideIntoContainer(SlideDirection.Start) },
        exitTransition = { slideOutOfContainer(SlideDirection.Start) },
        popEnterTransition = { slideIntoContainer(SlideDirection.End) },
        popExitTransition = { slideOutOfContainer(SlideDirection.End) }
    ) {
        onboardingFeatureScreen(
            onNextClick = { feature ->
                val next = OnboardingFeature.entries.getOrNull(feature.ordinal + 1)
                if (next != null) {
                    navController.navigateToOnboardingFeature(next)
                } else {
                    navController.navigateToOnboardingLocation()
                }
            },
            onSkipClick = { navController.navigateToOnboardingLocation() },
            onBackClick = { navController.popBackStack() }
        )
        onboardingLocationScreen(
            onFinished = onFinished,
            onPermissionGranted = onPermissionGranted,
            onBackClick = { navController.popBackStack() }
        )
    }
}
