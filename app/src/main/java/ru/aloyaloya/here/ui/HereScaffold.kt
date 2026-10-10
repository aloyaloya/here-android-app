package ru.aloyaloya.here.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.component.navigation.BottomNavigationBar
import ru.aloyaloya.design_system.component.navigation.BottomNavigationBarItem
import ru.aloyaloya.design_system.component.topbar.HereContextTopAppBar
import ru.aloyaloya.design_system.component.topbar.TopAppBar
import ru.aloyaloya.design_system.theme.HereMotion
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
 * @param onSettingsClick Колбэк нажатия на кнопку настроек в верхней панели.
 * @param modifier [Modifier], применяемый к контейнеру [Box].
 * @param content Основной контент экрана.
 */
@Composable
fun HereScaffold(
    currentTopLevelDestination: TopLevelDestination?,
    contextMode: HereContextMode?,
    destinations: List<TopLevelDestination>,
    onNavigate: (TopLevelDestination) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val contentOffsets = remember { mutableStateMapOf<TopLevelDestination, Float>() }
    val scrollConnection = remember(currentTopLevelDestination) {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                currentTopLevelDestination?.let { destination ->
                    contentOffsets[destination] = (contentOffsets[destination] ?: 0f) - consumed.y
                }
                return Offset.Zero
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HereTheme.colors.background)
            .nestedScroll(scrollConnection)
    ) {
        content()

        if (currentTopLevelDestination != null) {
            val contentScrolled =
                (contentOffsets[currentTopLevelDestination] ?: 0f) > SCROLLED_THRESHOLD

            AnimatedContent(
                targetState = contextMode,
                transitionSpec = {
                    val forward = targetState != null
                    (fadeIn(HereMotion.fadeThroughEnter()) +
                            slideInHorizontally(HereMotion.fadeThroughEnter()) { width ->
                                HereMotion.axisShift(width, forward)
                            }) togetherWith
                            (fadeOut(HereMotion.fadeThroughExit()) +
                                    slideOutHorizontally(HereMotion.fadeThroughExit()) { width ->
                                        -HereMotion.axisShift(width, forward)
                                    })
                },
                contentKey = { mode -> mode != null },
                label = "top-app-bar",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(HereTheme.colors.background)
            ) { mode ->
                if (mode != null) {
                    HereContextTopAppBar(
                        title = stringResource(mode.titleResId),
                        navigationContentDescription = stringResource(
                            DesignSystemR.string.context_bar_back_content_description
                        ),
                        onNavigateBack = mode.onExit
                    )
                } else {
                    TopAppBar(
                        title = stringResource(currentTopLevelDestination.titleResId),
                        onSettingsClick = onSettingsClick,
                        scrolled = contentScrolled
                    )
                }
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

private const val SCROLLED_THRESHOLD = 0.5f