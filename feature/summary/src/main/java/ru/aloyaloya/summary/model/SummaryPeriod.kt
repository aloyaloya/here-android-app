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
    MONTH(R.string.summary_period_month),
    YEAR(R.string.summary_period_year),
    ALL_TIME(R.string.summary_period_all_time);

    fun contains(date: LocalDate, today: LocalDate): Boolean = when (this) {
        MONTH -> YearMonth.from(date) == YearMonth.from(today)
        YEAR -> Year.from(date) == Year.from(today)
        ALL_TIME -> true
    }
}
