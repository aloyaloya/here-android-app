package ru.aloyaloya.here.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.util.trace
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import ru.aloyaloya.analytic.presentation.navigation.navigateToAnalytic
import ru.aloyaloya.calendar.presentation.navigation.navigateToCalendar
import ru.aloyaloya.here.navigation.HereNavHost
import ru.aloyaloya.here.navigation.TopLevelDestination
import ru.aloyaloya.map.presentation.navigation.navigateToMap
import ru.aloyaloya.ui.theme.LocalAppDarkTheme

/**
 * Корневой composable приложения Here, который настраивает
 * навигацию и базовый scaffold.
 *
 * Функция выступает точкой входа в UI: создает [NavController],
 * отслеживает текущий destination и передает состояние в [HereScaffold].
 * Внутри scaffold размещается основной контент экрана.
 *
 * Для сохранения состояния навигации между рекомпозициями используется
 * [rememberNavController], а для наблюдения за текущим destination —
 * [currentBackStackEntryAsState].
 *
 * @param darkTheme Текущий режим темы, влияющий на отображение кнопки в top bar.
 * @param onThemeChange Колбэк переключения темы, пробрасываемый в [HereScaffold].
 */
@Composable
fun HereApp(
    darkTheme: Boolean,
    onThemeChange: () -> Unit
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentTopLevelDestination = currentDestination.toTopLevelDestination()

    CompositionLocalProvider(LocalAppDarkTheme provides darkTheme) {
        HereScaffold(
            currentTopLevelDestination = currentTopLevelDestination ?: TopLevelDestination.MAP,
            destinations = TopLevelDestination.entries,
            onNavigate = { navigateToTopLevelDestination(navController, it) },
            darkTheme = darkTheme,
            onThemeChange = onThemeChange
        ) {
            HereNavHost(navController = navController)
        }
    }
}

/**
 * Преобразует текущий [NavDestination] в соответствующий верхнеуровневый пункт
 * навигации [TopLevelDestination], используя проверку по иерархии destination.
 *
 * Функция проходит по всем [TopLevelDestination.entries] и возвращает первый элемент,
 * чей [TopLevelDestination.baseRoute] найден в [NavDestination.hierarchy] текущего
 * destination. Если входной destination равен `null` или соответствие не найдено,
 * возвращается `null`.
 *
 * @return Текущий [TopLevelDestination] или `null`, если определить его невозможно.
 */
private fun NavDestination?.toTopLevelDestination(): TopLevelDestination? {
    val destination = this ?: return null
    return TopLevelDestination.entries.firstOrNull { topLevelDestination ->
        destination.hierarchy.any { navDestination ->
            navDestination.hasRoute(topLevelDestination.baseRoute)
        }
    }
}

/**
 * Выполняет переход к верхнеуровневому разделу приложения.
 *
 * Навигация настроена так, чтобы не раздувать back stack и
 * восстанавливать состояние ранее открытых разделов.
 */
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
            TopLevelDestination.MAP -> navController.navigateToMap(topLevelNavOptions)
            TopLevelDestination.CALENDAR -> navController.navigateToCalendar(topLevelNavOptions)
            TopLevelDestination.ANALYTIC -> navController.navigateToAnalytic(topLevelNavOptions)
        }
    }
}