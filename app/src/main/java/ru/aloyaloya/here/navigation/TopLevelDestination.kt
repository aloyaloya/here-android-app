package ru.aloyaloya.here.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.serialization.Serializable
import ru.aloyaloya.here.R
import kotlin.reflect.KClass

// TODO: Move all route resources, classes to feature modules

/**
 * Перечисление верхнеуровневых разделов навигации в приложении Here.
 *
 * Каждый раздел содержит метаданные, необходимые для навигации и отображения в UI:
 * иконку, подпись и информацию о маршруте.
 *
 * @property iconUnselectedResId Идентификатор ресурса активной иконки для отображения в панели навигации.
 * @property iconSelectedResId Идентификатор ресурса неактивной иконки для отображения в панели навигации.
 * @property labelResId Идентификатор строкового ресурса подписи для этого раздела.
 * @property route Основной класс маршрута раздела, используемый для навигации.
 * @property baseRoute Базовый класс маршрута для проверки иерархии и состояния выбора.
 */
enum class TopLevelDestination(
    @DrawableRes val iconUnselectedResId: Int,
    @DrawableRes val iconSelectedResId: Int,
    @StringRes val labelResId: Int,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
) {
    MAP(
        iconUnselectedResId = R.drawable.ic_map_outline,
        iconSelectedResId = R.drawable.ic_map_solid,
        labelResId = R.string.map_screen_title,
        route = MapRoute::class,
        baseRoute = MapBaseRoute::class,
    ),
    CALENDAR(
        iconUnselectedResId = R.drawable.ic_calendar_outline,
        iconSelectedResId = R.drawable.ic_calendar_solid,
        labelResId = R.string.calendar_screen_title,
        route = CalendarRoute::class,
        baseRoute = CalendarBaseRoute::class,
    ),
    ANALYTIC(
        iconUnselectedResId = R.drawable.ic_analytic_outline,
        iconSelectedResId = R.drawable.ic_analytic_solid,
        labelResId = R.string.analytic_screen_title,
        route = AnalyticRoute::class,
        baseRoute = AnalyticBaseRoute::class,
    )
}

@Serializable
data object MapRoute

@Serializable
data object MapBaseRoute

@Serializable
data object CalendarRoute

@Serializable
data object CalendarBaseRoute

@Serializable
data object AnalyticRoute

@Serializable
data object AnalyticBaseRoute