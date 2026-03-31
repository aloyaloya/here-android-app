package ru.aloyaloya.map.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
/** Маршрут экрана карты в графе навигации. */
data object MapRoute

/**
 * Выполняет переход на экран карты с заданными параметрами навигации.
 *
 * @param navOptions Параметры перехода, определяющие поведение навигации
 * (анимации, правила `popUpTo`, режим запуска и т.д.).
 */
fun NavController.navigateToMap(navOptions: NavOptions) =
    navigate(route = MapRoute, navOptions)

/**
 * Регистрирует экран карты как destination в [NavGraphBuilder].
 *
 * Внутри функции добавляется composable-маршрут [MapRoute] и
 * размещается UI-контент экрана карты.
 */
fun NavGraphBuilder.mapScreen() {
    composable<MapRoute> {

    }
}