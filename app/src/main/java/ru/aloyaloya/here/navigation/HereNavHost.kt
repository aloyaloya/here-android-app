package ru.aloyaloya.here.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import ru.aloyaloya.calendar.presentation.navigation.calendarScreen
import ru.aloyaloya.design_system.theme.HereMotion
import ru.aloyaloya.map.presentation.navigation.MapRoute
import ru.aloyaloya.map.presentation.navigation.mapScreen
import ru.aloyaloya.mapkit.model.MapPoint
import ru.aloyaloya.memory.presentation.navigation.memoryFormScreen
import ru.aloyaloya.memory.presentation.navigation.memoryScreen
import ru.aloyaloya.memory.presentation.navigation.navigateToEditMemory
import ru.aloyaloya.memory.presentation.navigation.navigateToMemory
import ru.aloyaloya.memory.presentation.navigation.navigateToNewMemory
import ru.aloyaloya.settings.presentation.navigation.NotificationsRoute
import ru.aloyaloya.settings.presentation.navigation.navigateToNotifications
import ru.aloyaloya.settings.presentation.navigation.notificationsScreen
import ru.aloyaloya.settings.presentation.navigation.settingsScreen
import ru.aloyaloya.summary.presentation.navigation.summaryScreen

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
 * @param mapFocus Точка, которую должна показать карта, или `null`.
 * @param onShowOnMap Колбэк перехода на карту к точке: переход между разделами
 * ведет [ru.aloyaloya.here.ui.HereApp].
 * @param onMapFocusShown Колбэк: карта показала [mapFocus].
 * @param reselections Повторные нажатия на текущий раздел в нижней навигации.
 * @param modifier Модификатор для настройки внешнего вида контейнера навигации.
 */
@Composable
fun HereNavHost(
    navController: NavHostController,
    placePicking: Boolean,
    onPlacePickingChange: (Boolean) -> Unit,
    mapFocus: MapPoint?,
    onShowOnMap: (MapPoint) -> Unit,
    onMapFocusShown: () -> Unit,
    reselections: Flow<TopLevelDestination>,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MapRoute,
        modifier = modifier,
        enterTransition = { enter(forward = true) },
        exitTransition = { exit(forward = true) },
        popEnterTransition = { enter(forward = false) },
        popExitTransition = { exit(forward = false) }
    ) {
        mapScreen(
            picking = placePicking,
            onPickingChange = onPlacePickingChange,
            focus = mapFocus,
            onFocusShown = onMapFocusShown,
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
            onEditClick = { memoryId -> navController.navigateToEditMemory(memoryId) },
            onMemoryClick = { memoryId -> navController.navigateToMemory(memoryId) }
        )
        calendarScreen(
            scrollToTop = reselections.of(TopLevelDestination.CALENDAR),
            onMemoryClick = { memoryId -> navController.navigateToMemory(memoryId) }
        )
        summaryScreen(
            scrollToTop = reselections.of(TopLevelDestination.SUMMARY),
            onPlaceClick = { latitude, longitude -> onShowOnMap(MapPoint(latitude, longitude)) },
            onMemoryClick = { memoryId -> navController.navigateToMemory(memoryId) }
        )
        settingsScreen(
            onBackClick = { navController.popBackStack() },
            onNotificationsRequest = { navController.navigateToNotifications() }
        )
        notificationsScreen(
            onClose = { navController.popBackStack(route = NotificationsRoute, inclusive = true) }
        )
    }
}

/** Смена разделов: новый проявляется с легким приближением. */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.enter(forward: Boolean): EnterTransition =
    if (initialState.isTopLevel && targetState.isTopLevel) {
        fadeIn(HereMotion.fadeThroughEnter()) +
                scaleIn(HereMotion.fadeThroughEnter(), initialScale = FADE_THROUGH_SCALE)
    } else {
        fadeIn(HereMotion.fadeThroughEnter()) +
                slideInHorizontally(HereMotion.fadeThroughEnter()) { width ->
                    HereMotion.axisShift(width, forward)
                }
    }

/** Уход экрана: раздел растворяется, остальные уезжают в сторону, противоположную входу. */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.exit(forward: Boolean): ExitTransition =
    if (initialState.isTopLevel && targetState.isTopLevel) {
        fadeOut(HereMotion.fadeThroughExit())
    } else {
        fadeOut(HereMotion.fadeThroughExit()) +
                slideOutHorizontally(HereMotion.fadeThroughExit()) { width ->
                    -HereMotion.axisShift(width, forward)
                }
    }

/** Повторные выборы одного раздела. */
private fun Flow<TopLevelDestination>.of(destination: TopLevelDestination): Flow<Unit> =
    filter { it == destination }.map { }

private val NavBackStackEntry.isTopLevel: Boolean
    get() = TopLevelDestination.entries.any { destination.hasRoute(it.route) }

private const val FADE_THROUGH_SCALE = 0.92f
