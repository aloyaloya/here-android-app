package ru.aloyaloya.here.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import ru.aloyaloya.analytic.presentation.navigation.analyticScreen
import ru.aloyaloya.calendar.presentation.navigation.calendarScreen
import ru.aloyaloya.map.presentation.navigation.MapRoute
import ru.aloyaloya.map.presentation.navigation.mapScreen
import ru.aloyaloya.memory.presentation.navigation.navigateToNewMemory
import ru.aloyaloya.memory.presentation.navigation.newMemoryScreen

/**
 * Корневой навигационный граф приложения Here.
 *
 * Настраивает [NavHost], определяет стартовый маршрут [MapRoute]
 * и регистрирует экраны приложения.
 *
 * @param navController Контроллер навигации, управляющий back stack и переходами.
 * @param modifier Модификатор для настройки внешнего вида контейнера навигации.
 */
@Composable
fun HereNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MapRoute,
        modifier = modifier
    ) {
        mapScreen(onEmotionConfirmed = navController::navigateToNewMemory)
        newMemoryScreen(onClose = { navController.popBackStack() })
        calendarScreen()
        analyticScreen()
    }
}