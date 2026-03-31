package ru.aloyaloya.analytic.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
/** Маршрут экрана аналитики в графе навигации. */
data object AnalyticRoute

/**
 * Выполняет переход на экран аналитики с заданными параметрами навигации.
 *
 * @param navOptions Параметры перехода, определяющие поведение навигации
 * (анимации, правила `popUpTo`, режим запуска и т.д.).
 */
fun NavController.navigateToAnalytic(navOptions: NavOptions) =
    navigate(route = AnalyticRoute, navOptions)

/**
 * Регистрирует экран аналитики как destination в [NavGraphBuilder].
 *
 * Внутри функции добавляется composable-маршрут [AnalyticRoute] и
 * размещается UI-контент экрана аналитики.
 */
fun NavGraphBuilder.analyticScreen() {
    composable<AnalyticRoute> {

    }
}