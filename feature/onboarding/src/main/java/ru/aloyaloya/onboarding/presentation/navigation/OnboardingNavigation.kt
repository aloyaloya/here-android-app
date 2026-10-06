package ru.aloyaloya.onboarding.presentation.navigation

import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.aloyaloya.onboarding.presentation.FeatureScreen
import ru.aloyaloya.onboarding.presentation.LocationScreen
import ru.aloyaloya.onboarding.presentation.OnboardingFeature

/**
 * Маршрут экрана о возможности приложения.
 *
 * @property feature Возможность, о которой рассказывает экран.
 */
@Serializable
data class OnboardingFeatureRoute(val feature: OnboardingFeature)

/** Маршрут экрана запроса геолокации. */
@Serializable
data object OnboardingLocationRoute

/**
 * Выполняет переход на экран о возможности приложения.
 *
 * @param feature Возможность.
 */
fun NavController.navigateToOnboardingFeature(feature: OnboardingFeature) =
    navigate(route = OnboardingFeatureRoute(feature))

/**
 * Выполняет переход на экран запроса геолокации.
 */
fun NavController.navigateToOnboardingLocation() =
    navigate(route = OnboardingLocationRoute) { launchSingleTop = true }

/**
 * Регистрирует экраны о возможностях как destination в [NavGraphBuilder].
 *
 * @param onNextClick Колбэк кнопки «Далее» с текущей возможностью.
 * @param onSkipClick Колбэк кнопки «Пропустить».
 * @param onBackClick Колбэк кнопки «Назад».
 */
fun NavGraphBuilder.onboardingFeatureScreen(
    onNextClick: (OnboardingFeature) -> Unit,
    onSkipClick: () -> Unit,
    onBackClick: () -> Unit
) {
    composable<OnboardingFeatureRoute> { navBackStackEntry ->
        val feature = navBackStackEntry.toRoute<OnboardingFeatureRoute>().feature

        FeatureScreen(
            feature = feature,
            onNext = dropUnlessResumed { onNextClick(feature) },
            onSkip = dropUnlessResumed(block = onSkipClick),
            onNavigateBack = dropUnlessResumed(block = onBackClick)
        )
    }
}

/**
 * Регистрирует экран запроса геолокации как destination в [NavGraphBuilder].
 *
 * @param onFinished Колбэк завершения онбординга.
 * @param onBackClick Колбэк кнопки «Назад».
 */
fun NavGraphBuilder.onboardingLocationScreen(
    onFinished: () -> Unit,
    onBackClick: () -> Unit
) {
    composable<OnboardingLocationRoute> {
        LocationScreen(
            onFinished = onFinished,
            onNavigateBack = dropUnlessResumed(block = onBackClick)
        )
    }
}
