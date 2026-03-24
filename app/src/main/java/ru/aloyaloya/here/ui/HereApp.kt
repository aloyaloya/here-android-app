package ru.aloyaloya.here.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.util.trace
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import ru.aloyaloya.here.navigation.TopLevelDestination

/**
 * Корневой composable приложения Here, который настраивает
 * навигацию и базовый scaffold.
 *
 * Функция выступает точкой входа в UI: создает [NavController],
 * отслеживает текущий destination и передает состояние в [GametrackerScaffold].
 * Внутри scaffold размещается основной контент экрана.
 *
 * Для сохранения состояния навигации между рекомпозициями используется
 * [rememberNavController], а для наблюдения за текущим destination —
 * [currentBackStackEntryAsState].
 */
@Composable
fun HereApp() {

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    HereScaffold(
        currentDestination = currentDestination,
        destinations = TopLevelDestination.entries,
        onNavigate = {} // navigateToTopLevelDestination(navController, it)
    ) {
        Text("Test")
    }
}

private fun navigateToTopLevelDestination(
    navController: NavController,
    topLevelDestination: TopLevelDestination
) {
    trace("Navigation: ${topLevelDestination.name}") {
        val topLevelNavOptions = navOptions {
            /**
             * Возвращаемся к стартовому destination графа,
             * чтобы не накапливать большой back stack
             * при переключении пунктов нижней навигации.
             * */
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            /**
             * Запрещаем создание дубликатов одного и того же
             * destination при повторном выборе текущего пункта.
             */
            launchSingleTop = true
            /** Восстанавливаем состояние ранее выбранного пункта. */
            restoreState = true
        }

        when (topLevelDestination) {
//            TopLevelDestination.MAP -> navController.navigateToMap(topLevelNavOptions)
            TopLevelDestination.MAP -> {}
            TopLevelDestination.CALENDAR -> {}
            TopLevelDestination.ANALYTIC -> {}
        }
    }
}