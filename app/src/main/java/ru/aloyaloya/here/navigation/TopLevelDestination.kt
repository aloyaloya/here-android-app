package ru.aloyaloya.here.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.aloyaloya.calendar.presentation.navigation.CalendarRoute
import ru.aloyaloya.map.presentation.navigation.MapRoute
import ru.aloyaloya.summary.presentation.navigation.SummaryRoute
import kotlin.reflect.KClass

/**
 * Перечисление верхнеуровневых разделов навигации в приложении Here.
 *
 * @property iconUnselectedResId Идентификатор ресурса активной иконки для отображения в панели навигации.
 * @property iconSelectedResId Идентификатор ресурса неактивной иконки для отображения в панели навигации.
 * @property labelResId Идентификатор строкового ресурса подписи для этого раздела.
 * @property titleResId Идентификатор строкового ресурса для заголовка экрана.
 * @property route Основной класс маршрута раздела, используемый для навигации.
 * @property baseRoute Базовый класс маршрута для проверки иерархии и состояния выбора.
 */
enum class TopLevelDestination(
    @DrawableRes val iconUnselectedResId: Int,
    @DrawableRes val iconSelectedResId: Int,
    @StringRes val labelResId: Int,
    @StringRes val titleResId: Int,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
) {
    MAP(
        iconUnselectedResId = ru.aloyaloya.map.R.drawable.ic_map_outline,
        iconSelectedResId = ru.aloyaloya.map.R.drawable.ic_map_solid,
        labelResId = ru.aloyaloya.map.R.string.map_screen_label,
        titleResId = ru.aloyaloya.map.R.string.map_screen_title,
        route = MapRoute::class
    ),
    CALENDAR(
        iconUnselectedResId = ru.aloyaloya.calendar.R.drawable.ic_calendar_outline,
        iconSelectedResId = ru.aloyaloya.calendar.R.drawable.ic_calendar_solid,
        labelResId = ru.aloyaloya.calendar.R.string.calendar_screen_label,
        titleResId = ru.aloyaloya.calendar.R.string.calendar_screen_title,
        route = CalendarRoute::class
    ),
    SUMMARY(
        iconUnselectedResId = ru.aloyaloya.summary.R.drawable.ic_summary_outline,
        iconSelectedResId = ru.aloyaloya.summary.R.drawable.ic_summary_solid,
        labelResId = ru.aloyaloya.summary.R.string.summary_screen_label,
        titleResId = ru.aloyaloya.summary.R.string.summary_screen_title,
        route = SummaryRoute::class
    )
}