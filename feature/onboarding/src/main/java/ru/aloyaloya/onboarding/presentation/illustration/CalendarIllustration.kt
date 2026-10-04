package ru.aloyaloya.onboarding.presentation.illustration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import ru.aloyaloya.design_system.component.calendar.DAYS_IN_WEEK
import ru.aloyaloya.design_system.component.calendar.monthGrid
import ru.aloyaloya.design_system.component.emotion.EmotionPin
import ru.aloyaloya.design_system.format.currentLocale
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.onboarding.R
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.emoji
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle

/** Месяц на иллюстрации. */
private val IllustrationMonth = YearMonth.of(2026, 7)

/** Недель в сетке иллюстрации: июль 2026 укладывается в пять. */
private const val ILLUSTRATION_WEEKS = 5

/** Эмоции по дням месяца. */
private val DayEmotions = mapOf(
    1 to Emotion.HAPPY,
    3 to Emotion.CALM,
    4 to Emotion.HAPPY,
    7 to Emotion.TENDER,
    9 to Emotion.SAD,
    11 to Emotion.HAPPY,
    12 to Emotion.CALM,
    15 to Emotion.TENDER,
    18 to Emotion.SURPRISED,
    19 to Emotion.HAPPY,
    22 to Emotion.CALM,
    24 to Emotion.ANGRY,
    26 to Emotion.HAPPY,
    28 to Emotion.CALM,
    30 to Emotion.TENDER
)

/** Доли эмоций за месяц, от частой к редкой. */
private val EmotionShares = DayEmotions.values
    .groupingBy { it }
    .eachCount()
    .entries
    .sortedByDescending { it.value }

/** Месяц с эмоциями по дням и полоса итогов под ним. */
@Composable
fun CalendarIllustration(modifier: Modifier = Modifier) {
    val colors = HereTheme.colors
    val locale = currentLocale()
    val days = remember { monthGrid(IllustrationMonth).take(ILLUSTRATION_WEEKS * DAYS_IN_WEEK) }
    val weekdays = remember(locale) {
        days.take(DAYS_IN_WEEK).map { it.dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE, locale) }
    }
    val monthName = remember(locale) {
        IllustrationMonth.month
            .getDisplayName(TextStyle.FULL_STANDALONE, locale)
            .replaceFirstChar { it.titlecase(locale) }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(HereSpacing.s),
        modifier = modifier
            .fillMaxSize()
            .background(colors.surface)
            .padding(HereSpacing.l)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(HereSpacing.s)
        ) {
            Text(
                text = monthName,
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary
            )

            Text(
                text = IllustrationMonth.year.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(HereSpacing.xs)) {
            Row {
                weekdays.forEach { weekday ->
                    Text(
                        text = weekday,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = HereSize.Calendar.weekdaySize
                        ),
                        color = colors.textTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            days.chunked(DAYS_IN_WEEK).forEach { week ->
                Row {
                    week.forEach { day ->
                        DayCell(
                            day = day,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Text(
            text = stringResource(R.string.onboarding_calendar_summary),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(HereSize.Summary.barGap),
            modifier = Modifier
                .fillMaxWidth()
                .height(HereSize.Summary.barHeight)
                .clip(HereShape.pill)
        ) {
            EmotionShares.forEach { (emotion, count) ->
                Box(
                    modifier = Modifier
                        .weight(count.toFloat())
                        .fillMaxHeight()
                        .background(emotion.color.solid)
                )
            }
        }
    }
}

/**
 * Ячейка дня: пин эмоции или число, как в календаре приложения.
 *
 * @param day День сетки.
 * @param modifier [Modifier], применяемый к ячейке.
 */
@Composable
private fun DayCell(
    day: LocalDate,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors
    val sizes = HereSize.OnboardingIllustration
    val inMonth = YearMonth.from(day) == IllustrationMonth
    val emotion = DayEmotions[day.dayOfMonth]?.takeIf { inMonth }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.height(sizes.dayCellSize)
    ) {
        if (emotion != null) {
            EmotionPin(
                emoji = emotion.emoji,
                color = emotion.color.solid,
                size = sizes.dayCellSize,
                border = HereSize.Calendar.pinBorder,
                emojiSize = sizes.dayEmojiSize
            )
        } else {
            Text(
                text = day.dayOfMonth.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = if (inMonth) colors.textPrimary else colors.textQuaternary
            )
        }
    }
}
