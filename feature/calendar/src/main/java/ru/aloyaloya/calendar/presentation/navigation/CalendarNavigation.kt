package ru.aloyaloya.calendar.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

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
 */
fun NavGraphBuilder.calendarScreen() {
    composable<CalendarRoute> {

    }
}