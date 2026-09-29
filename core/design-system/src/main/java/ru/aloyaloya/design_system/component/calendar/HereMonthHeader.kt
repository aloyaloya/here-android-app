package ru.aloyaloya.design_system.component.calendar

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val MonthFormat = DateTimeFormatter.ofPattern("LLLL yyyy", Locale.forLanguageTag("ru"))

/**
 * Название месяца и стрелки перелистывания.
 *
 * @param month Показанный месяц.
 * @param onPreviousClick Колбэк перехода на предыдущий месяц.
 * @param onNextClick Колбэк перехода на следующий месяц.
 * @param modifier Внешний [Modifier] шапки.
 */
@Composable
fun HereMonthHeader(
    month: YearMonth,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = MonthFormat.format(
                month.atDay(1)
            ).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = HereSize.Calendar.monthSize
            ),
            color = HereTheme.colors.textPrimary
        )

        Row(horizontalArrangement = Arrangement.spacedBy(HereSize.Calendar.navButtonSpacing)) {
            MonthNavButton(
                icon = R.drawable.ic_chevron_left,
                contentDescription = stringResource(R.string.calendar_previous_month),
                onClick = onPreviousClick
            )
            MonthNavButton(
                icon = R.drawable.ic_chevron_right,
                contentDescription = stringResource(R.string.calendar_next_month),
                onClick = onNextClick
            )
        }
    }
}

/** Круглая кнопка перелистывания месяца. */
@Composable
private fun MonthNavButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    val colors = HereTheme.colors

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(HereSize.Calendar.navButtonSize)
            .clip(HereShape.pill)
            .background(colors.surfaceMuted)
            .clickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(icon),
            tint = colors.textPrimary,
            contentDescription = contentDescription,
            modifier = Modifier.size(HereSize.Calendar.navIconSize)
        )
    }
}
