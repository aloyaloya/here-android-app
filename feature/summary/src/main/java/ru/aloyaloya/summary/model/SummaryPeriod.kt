package ru.aloyaloya.summary.model

import androidx.annotation.StringRes
import ru.aloyaloya.summary.R
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth

/**
 * Период, за который считаются итоги.
 *
 * @property labelResId Подпись периода в переключателе.
 */
enum class SummaryPeriod(@StringRes val labelResId: Int) {
    ALL_TIME(R.string.summary_period_all_time),
    YEAR(R.string.summary_period_year),
    MONTH(R.string.summary_period_month);

    fun contains(date: LocalDate, today: LocalDate): Boolean = when (this) {
        ALL_TIME -> true
        YEAR -> Year.from(date) == Year.from(today)
        MONTH -> YearMonth.from(date) == YearMonth.from(today)
    }
}
