package ru.aloyaloya.calendar.presentation.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.aloyaloya.design_system.extension.LocalNavAnimatedVisibilityScope
import ru.aloyaloya.calendar.di.CalendarComponent
import ru.aloyaloya.calendar.presentation.CalendarScreen
import ru.aloyaloya.calendar.presentation.CalendarViewModel
import ru.aloyaloya.ui.di.ComponentProvider

@Serializable
/** Маршрут экрана календаря в графе навигации. */
data object CalendarRoute

/**
 * Выполняет переход на экран календаря с заданными параметрами навигации.
 *
 * @param navOptions Параметры перехода, определяющие поведение навигации
 * (анимации, правила `popUpTo`, режим запуска и т.д.).
 */
fun NavController.navigateToCalendar(navOptions: NavOptions) =
    navigate(route = CalendarRoute, navOptions)

/**
 * Регистрирует экран календаря как destination в [NavGraphBuilder].
 *
 * Внутри функции добавляется composable-маршрут [CalendarRoute] и
 * размещается UI-контент экрана календаря.
 *
 * @param onMemoryClick Колбэк нажатия на воспоминание дня, получает его id.
 */
fun NavGraphBuilder.calendarScreen(onMemoryClick: (Long) -> Unit) {
    composable<CalendarRoute> { navBackStackEntry ->

        val context = LocalContext.current.applicationContext

        val factory = (context as ComponentProvider)
            .provideComponent("calendar", CalendarComponent::class)
            .viewModelFactory

        val viewModel = viewModel<CalendarViewModel>(
            viewModelStoreOwner = navBackStackEntry,
            factory = factory
        )

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
            CalendarScreen(
                uiState = uiState,
                onMemoryClick = onMemoryClick
            )
        }
    }
}