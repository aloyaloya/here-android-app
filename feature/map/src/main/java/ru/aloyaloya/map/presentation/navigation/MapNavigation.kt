package ru.aloyaloya.map.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.aloyaloya.map.di.MapComponent
import ru.aloyaloya.map.presentation.MapScreen
import ru.aloyaloya.map.presentation.MapViewModel
import ru.aloyaloya.ui.di.ComponentProvider

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
    composable<MapRoute> { navBackStackEntry ->

        val context = LocalContext.current.applicationContext

        val factory = (context as ComponentProvider)
            .provideComponent("map", MapComponent::class)
            .viewModelFactory

        val viewModel = viewModel<MapViewModel>(
            viewModelStoreOwner = navBackStackEntry,
            factory = factory
        )

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        MapScreen(uiState)
    }
}