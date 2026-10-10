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
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.map.di.MapComponent
import ru.aloyaloya.map.presentation.MapScreen
import ru.aloyaloya.map.presentation.MapViewModel
import ru.aloyaloya.mapkit.model.MapPoint
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
 * размещается UI-контент экрана карты. *
 *
 * @param picking Включен ли режим выбора места.
 * @param onPickingChange Колбэк входа в режим выбора места и выхода из него.
 * @param onEmotionConfirmed Колбэк выбора эмоции в листе: вместе с эмоцией отдает
 * точку на карте, дальше идет экран нового места.
 * @param onMemoryClick Колбэк перехода к воспоминанию по нажатию на его метку.
 * @param focus Точка, которую карта должна показать, или `null`.
 * @param onFocusShown Колбэк: камера встала на [focus].
 */
fun NavGraphBuilder.mapScreen(
    picking: Boolean,
    focus: MapPoint?,
    onFocusShown: () -> Unit,
    onPickingChange: (Boolean) -> Unit,
    onEmotionConfirmed: (Emotion, MapPoint) -> Unit,
    onMemoryClick: (Long) -> Unit
) {
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

        MapScreen(
            uiState = uiState,
            picking = picking,
            focus = focus,
            onFocusShown = onFocusShown,
            onNewMemoryShown = viewModel::onNewMemoryShown,
            onEmotionConfirmed = onEmotionConfirmed,
            onMemoryClick = onMemoryClick,
            onPickStart = { onPickingChange(true) },
            onPickCancel = { onPickingChange(false) }
        )
    }
}