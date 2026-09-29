package ru.aloyaloya.here.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.component.navigation.BottomNavigationBar
import ru.aloyaloya.design_system.component.navigation.BottomNavigationBarItem
import ru.aloyaloya.design_system.component.topbar.HereContextTopAppBar
import ru.aloyaloya.design_system.component.topbar.TopAppBar
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.here.navigation.TopLevelDestination
import ru.aloyaloya.design_system.R as DesignSystemR

/**
 * Каркас приложения Here.
 *
 * В отличие от [androidx.compose.material3.Scaffold] контент занимает весь экран,
 * а панели лежат поверх него по краям: на карте это позволяет держать ее под ними
 * во всю высоту. Отступы, чтобы не уехать под панели, экраны задают себе сами.
 * Свои кнопки поверх контента экраны тоже размещают сами.
 *
 * Панель и навигация принадлежат разделам приложения, поэтому на экранах поверх
 * них — например на новом воспоминании — каркас рисует только контент.
 *
 * Пока приложение в режиме ([contextMode]), верхнюю панель раздела подменяет шапка режима:
 * заголовок называет происходящее, а стрелка выходит. Нижняя навигация остается на месте —
 * уход в другой раздел выключает режим сам.
 *
 * @param currentTopLevelDestination Текущий верхнеуровневый destination навигации
 * или `null`, если открыт экран вне разделов.
 * @param contextMode Текущий режим приложения или `null`, если приложение не в режиме.
 * @param destinations Список объектов [TopLevelDestination].
 * @param onNavigate Колбэк, вызываемый при нажатии на элемент навигации.
 * @param darkTheme Текущее состояние темы для кнопки в верхней панели.
 * @param onThemeChange Колбэк переключения темы.
 * @param modifier [Modifier], применяемый к контейнеру [Box].
 * @param content Основной контент экрана.
 */
@Composable
fun HereScaffold(
    currentTopLevelDestination: TopLevelDestination?,
    contextMode: HereContextMode?,
    destinations: List<TopLevelDestination>,
    onNavigate: (TopLevelDestination) -> Unit,
    darkTheme: Boolean,
    onThemeChange: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HereTheme.colors.background)
    ) {
        content()

        if (currentTopLevelDestination != null) {
            if (contextMode != null) {
                HereContextTopAppBar(
                    title = stringResource(contextMode.titleResId),
                    navigationContentDescription = stringResource(
                        DesignSystemR.string.context_bar_back_content_description
                    ),
                    onNavigateBack = contextMode.onExit,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            } else {
                TopAppBar(
                    title = stringResource(currentTopLevelDestination.titleResId),
                    darkTheme = darkTheme,
                    onThemeChange = onThemeChange,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }

            BottomNavigationBar(
                modifier = Modifier.align(Alignment.BottomCenter)
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
    }
}
