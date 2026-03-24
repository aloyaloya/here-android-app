package ru.aloyaloya.here.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import ru.aloyaloya.design_system.component.navigation.BottomNavigationBar
import ru.aloyaloya.design_system.component.navigation.BottomNavigationBarItem
import ru.aloyaloya.here.navigation.TopLevelDestination
import kotlin.reflect.KClass

/**
 * Кастомный Scaffold для приложения Here
 * с градиентным фоном и нижней навигацией.
 *
 * Этот composable создает layout на базе Scaffold с вертикальным
 * градиентным фоном и нижней панелью навигации.
 *
 * Состояние выбора элементов навигации определяется автоматически
 * на основе текущего destination.
 *
 * @param currentDestination Текущий destination навигации из NavController.
 * @param destinations Список объектов [TopLevelDestination].
 * @param onNavigate Колбэк, вызываемый при нажатии на элемент навигации.
 * @param modifier [Modifier], применяемый к контейнеру [Box].
 * @param content Основной контент экрана.
 */
@Composable
fun HereScaffold(
    currentDestination: NavDestination?,
    destinations: List<TopLevelDestination>,
    onNavigate: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar {
                    destinations.forEach { destination ->
                        BottomNavigationBarItem(
                            selectedPainter = painterResource(destination.iconSelectedResId),
                            unselectedPainter = painterResource(destination.iconUnselectedResId),
                            label = stringResource(destination.labelResId),
                            selected = currentDestination.isRouteInHierarchy(destination.baseRoute),
                            onClick = { onNavigate(destination) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                content()
            }
        }
    }
}

/**
 * Проходит по иерархии destination, начиная с текущего,
 * и проверяет, есть ли в ней указанный класс маршрута.
 *
 * @param route Kotlin-класс маршрута, который нужно найти в иерархии.
 * @return `true`, если маршрут найден в иерархии текущего destination, иначе `false`.
 */
private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any { it.hasRoute(route) } ?: false
