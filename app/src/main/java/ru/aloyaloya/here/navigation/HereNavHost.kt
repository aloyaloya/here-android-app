package ru.aloyaloya.here.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import ru.aloyaloya.summary.presentation.navigation.summaryScreen
import ru.aloyaloya.calendar.presentation.navigation.calendarScreen
import ru.aloyaloya.map.presentation.navigation.MapRoute
import ru.aloyaloya.map.presentation.navigation.mapScreen
import ru.aloyaloya.memory.presentation.navigation.memoryFormScreen
import ru.aloyaloya.memory.presentation.navigation.memoryScreen
import ru.aloyaloya.memory.presentation.navigation.navigateToEditMemory
import ru.aloyaloya.memory.presentation.navigation.navigateToMemory
import ru.aloyaloya.memory.presentation.navigation.navigateToNewMemory

/**
 * Корневой навигационный граф приложения Here.
 *
 * Настраивает [NavHost], определяет стартовый маршрут [MapRoute]
 * и регистрирует экраны приложения.
 *
 * @param navController Контроллер навигации, управляющий back stack и переходами.
 * @param placePicking Включен ли режим выбора места: им владеет [ru.aloyaloya.here.ui.HereApp],
 * потому что режим меняет не только карту, но и панели приложения.
 * @param onPlacePickingChange Колбэк входа в режим выбора места и выхода из него.
 * @param modifier Модификатор для настройки внешнего вида контейнера навигации.
 */
@Composable
fun HereNavHost(
    navController: NavHostController,
    placePicking: Boolean,
    onPlacePickingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: сделать анимацию перехода
    NavHost(
        navController = navController,
        startDestination = MapRoute,
        modifier = modifier
    ) {
        mapScreen(
            picking = placePicking,
            onPickingChange = onPlacePickingChange,
            onEmotionConfirmed = { emotion, point ->
                navController.navigateToNewMemory(
                    emotion = emotion,
                    latitude = point.latitude,
                    longitude = point.longitude
                )
            },
            onMemoryClick = { memoryId -> navController.navigateToMemory(memoryId) }
        )
        memoryFormScreen(onClose = { navController.popBackStack() })
        memoryScreen(
            onBackClick = { navController.popBackStack() },
            onEditClick = { memoryId -> navController.navigateToEditMemory(memoryId) }
        )
        calendarScreen(
            onMemoryClick = { memoryId -> navController.navigateToMemory(memoryId) }
        )
        summaryScreen()
    }
}