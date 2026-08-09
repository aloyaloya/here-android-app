package ru.aloyaloya.memory.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

private const val GRID_WEEKS = 6
const val DAYS_IN_WEEK = 7

/**
 * Раскладывает [month] по сетке календаря.
 *
 * Сетка начинается с понедельника недели, в которую попало первое число, поэтому
 * по краям в нее попадают дни соседних месяцев.
 *
 * Недель всегда шесть, даже если месяц укладывается в пять: иначе лист прыгал бы
 * по высоте при переходе с одного месяца на другой.
 */
fun monthGrid(month: YearMonth): List<LocalDate> {
    val firstDay = month.atDay(1)
    val gridStart = firstDay.minusDays((firstDay.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())

    return List(GRID_WEEKS * DAYS_IN_WEEK) { gridStart.plusDays(it.toLong()) }
}