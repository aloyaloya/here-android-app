package ru.aloyaloya.design_system.format

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Число и месяц: «4 октября». */
val DayMonthFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM")

/** Полная дата: «4 октября 2026». */
val FullDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

/** Месяц и год: «октябрь 2026». */
val MonthYearFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("LLLL yyyy")

/** Время в 24-часовом формате: «21:00». */
val TimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/** Текущий язык интерфейса. */
@Composable
fun currentLocale(): Locale = LocalConfiguration.current.locales[0]
