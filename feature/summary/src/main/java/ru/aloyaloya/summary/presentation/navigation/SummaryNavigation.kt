package ru.aloyaloya.summary.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.aloyaloya.summary.di.SummaryComponent
import ru.aloyaloya.summary.presentation.SummaryScreen
import ru.aloyaloya.summary.presentation.SummaryViewModel
import ru.aloyaloya.ui.di.ComponentProvider

@Serializable
/** Маршрут экрана итогов в графе навигации. */
data object SummaryRoute

/**
 * Выполняет переход на экран итогов с заданными параметрами навигации.
 *
 * @param navOptions Параметры перехода, определяющие поведение навигации
 * (анимации, правила `popUpTo`, режим запуска и т.д.).
 */
fun NavController.navigateToSummary(navOptions: NavOptions) =
    navigate(route = SummaryRoute, navOptions)

/**
 * Регистрирует экран итогов как destination в [NavGraphBuilder].
 *
 * Внутри функции добавляется composable-маршрут [SummaryRoute] и
 * размещается UI-контент экрана итогов.
 *
 * @param onPlaceClick Колбэк нажатия на место настроения: отдает его точку,
 * чтобы показать место на карте.
 * @param onMemoryClick Колбэк перехода к воспоминанию.
 */
fun NavGraphBuilder.summaryScreen(
    onPlaceClick: (latitude: Double, longitude: Double) -> Unit,
    onMemoryClick: (Long) -> Unit
) {
    composable<SummaryRoute> { navBackStackEntry ->

        val context = LocalContext.current.applicationContext

        val factory = (context as ComponentProvider)
            .provideComponent("summary", SummaryComponent::class)
            .viewModelFactory

        val viewModel = viewModel<SummaryViewModel>(
            viewModelStoreOwner = navBackStackEntry,
            factory = factory
        )

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        SummaryScreen(
            uiState = uiState,
            onPeriodSelected = viewModel::onPeriodSelected,
            onPlaceClick = onPlaceClick,
            onMemoryClick = onMemoryClick,
            onRecallAnother = viewModel::onRecallAnother
        )
    }
}