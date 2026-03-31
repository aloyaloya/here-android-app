package ru.aloyaloya.here.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.component.topbar.TopAppBar
import ru.aloyaloya.design_system.component.navigation.BottomNavigationBar
import ru.aloyaloya.design_system.component.navigation.BottomNavigationBarItem
import ru.aloyaloya.here.navigation.TopLevelDestination

/**
 * Кастомный Scaffold для приложения Here
 * с градиентным фоном и нижней навигацией.
 *
 * Этот composable создает layout на базе Scaffold с вертикальным
 * градиентным фоном и нижней панелью навигации.
 *
 * Состояние выбора элементов навигации определяется на основе
 * текущего верхнеуровневого destination.
 *
 * @param currentTopLevelDestination Текущий верхнеуровневый destination навигации.
 * @param destinations Список объектов [TopLevelDestination].
 * @param onNavigate Колбэк, вызываемый при нажатии на элемент навигации.
 * @param darkTheme Текущий режим темы, отображаемый в кнопке top bar.
 * @param onThemeChange Колбэк переключения темы из [TopAppBar].
 * @param modifier [Modifier], применяемый к контейнеру [Box].
 * @param content Основной контент экрана.
 */
@Composable
fun HereScaffold(
    currentTopLevelDestination: TopLevelDestination,
    destinations: List<TopLevelDestination>,
    onNavigate: (TopLevelDestination) -> Unit,
    darkTheme: Boolean,
    onThemeChange: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.primary,
            topBar = {
                TopAppBar(
                    painter = painterResource(currentTopLevelDestination.iconUnselectedResId),
                    title = stringResource(currentTopLevelDestination.titleResId),
                    darkTheme = darkTheme,
                    onThemeChange = onThemeChange,
                    onOptionsNavigate = {}
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    modifier = Modifier.height(dimensionResource(R.dimen.bottom_nav_height))
                ) {
                    destinations.forEach { destination ->
                        BottomNavigationBarItem(
                            selectedPainter = painterResource(destination.iconSelectedResId),
                            unselectedPainter = painterResource(destination.iconUnselectedResId),
                            label = stringResource(destination.labelResId),
                            selected = destination == currentTopLevelDestination,
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
