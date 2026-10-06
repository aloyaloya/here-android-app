package ru.aloyaloya.onboarding.presentation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

/** Шагов в онбординге: возможности и геолокация. */
internal val OnboardingSteps = OnboardingFeature.entries.size + 1

/** Маршрут экрана о возможности приложения. */
@Serializable
private data class FeatureRoute(val feature: OnboardingFeature)

/** Маршрут экрана запроса геолокации. */
@Serializable
private data object LocationRoute

/**
 * Онбординг: экраны о возможностях и экран запроса геолокации.
 *
 * @param onFinished вызывается, когда пользователь прошел последний экран
 */
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = FeatureRoute(OnboardingFeature.entries.first()),
        modifier = modifier,
        enterTransition = { slideIntoContainer(SlideDirection.Start) },
        exitTransition = { slideOutOfContainer(SlideDirection.Start) },
        popEnterTransition = { slideIntoContainer(SlideDirection.End) },
        popExitTransition = { slideOutOfContainer(SlideDirection.End) }
    ) {
        composable<FeatureRoute> { entry ->
            val feature = entry.toRoute<FeatureRoute>().feature
            val next = OnboardingFeature.entries.getOrNull(feature.ordinal + 1)

            FeatureScreen(
                feature = feature,
                onNext = dropUnlessResumed {
                    navController.navigate(next?.let(::FeatureRoute) ?: LocationRoute)
                },
                onSkip = dropUnlessResumed { navController.navigate(LocationRoute) },
                onNavigateBack = dropUnlessResumed { navController.popBackStack() }
            )
        }

        composable<LocationRoute> {
            LocationScreen(
                onFinished = onFinished,
                onNavigateBack = dropUnlessResumed { navController.popBackStack() }
            )
        }
    }
}
