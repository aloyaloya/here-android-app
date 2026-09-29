package ru.aloyaloya.calendar.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import ru.aloyaloya.calendar.model.CalendarUiState
import ru.aloyaloya.design_system.component.calendar.CalendarDayMark
import ru.aloyaloya.design_system.component.calendar.HereMonthGrid
import ru.aloyaloya.design_system.component.calendar.HereMonthHeader
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.emoji
import java.time.LocalDate
import java.time.YearMonth

/**
 * Экран календаря: воспоминания по дням месяца.
 *
 * @param uiState Состояние экрана.
 * @param modifier Внешний [Modifier] экрана.
 */
@Composable
fun CalendarScreen(
    uiState: CalendarUiState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HereTheme.colors.background)
    ) {
        when (uiState) {
            CalendarUiState.Loading -> {
                CircularProgressIndicator(
                    color = HereTheme.colors.accent,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is CalendarUiState.Content -> CalendarContent(uiState)
        }
    }
}

/** Месяц с эмоциями в днях на карточке и фон в цвет его настроения. */
@Composable
private fun CalendarContent(uiState: CalendarUiState.Content) {
    var shownMonth by rememberSaveable { mutableStateOf(YearMonth.now()) }

    val markByDate = uiState.emotionByDate.mapValues { (_, emotion) ->
        CalendarDayMark(emoji = emotion.emoji, color = emotion.color.solid)
    }
    val monthEmotion = remember(uiState.emotionByDate, shownMonth) {
        uiState.emotionByDate.dominantEmotionIn(shownMonth)
    }

    // TODO: плавно менять цвет фона при перелистывании месяца
    MoodGlow(color = monthEmotion?.color?.solid)

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                top = HereSize.TopAppBar.height,
                bottom = HereSize.NavBar.height
            )
            .padding(
                horizontal = HereSpacing.l,
                vertical = HereSpacing.s
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(HereSpacing.xl),
            modifier = Modifier
                .clip(HereShape.tile)
                .background(HereTheme.colors.surface)
                .padding(HereSpacing.l)
        ) {
            HereMonthHeader(
                month = shownMonth,
                onPreviousClick = { shownMonth = shownMonth.minusMonths(1) },
                onNextClick = { shownMonth = shownMonth.plusMonths(1) }
            )

            HereMonthGrid(
                month = shownMonth,
                selectedDate = null,
                markByDate = markByDate,
                onDayClick = {}
            )
        }
    }
}

/**
 * Мягкое пятно цвета за карточкой месяца.
 *
 * Центр пятна ниже верхней панели, а край растворяется в фон раньше, чем доходит
 * до нее: иначе на границе панели и экрана был бы виден шов.
 *
 * @param color Цвет пятна или `null`, если в месяце нет воспоминаний.
 */
@Composable
private fun MoodGlow(color: Color?) {
    if (color == null) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(color.copy(alpha = GLOW_ALPHA), color.copy(alpha = 0f)),
                        center = Offset(size.width / 2, size.height * GLOW_CENTER_FRACTION),
                        radius = size.width * GLOW_RADIUS_FRACTION
                    )
                )
            }
    )
}

/**
 * Эмоция, которая чаще других встречается в днях [month].
 *
 * При равенстве побеждает та, что была позже: она ближе к тому,
 * чем месяц закончился.
 */
private fun Map<LocalDate, Emotion>.dominantEmotionIn(month: YearMonth): Emotion? =
    filterKeys { YearMonth.from(it) == month }
        .entries
        .groupBy({ it.value }, { it.key })
        .maxWithOrNull(
            compareBy<Map.Entry<Emotion, List<LocalDate>>> { it.value.size }
                .thenBy { it.value.max() }
        )
        ?.key

/** Насыщенность пятна: фон подсказывает настроение, а не спорит с днями. */
private const val GLOW_ALPHA = 0.45f
private const val GLOW_CENTER_FRACTION = 0.5f
private const val GLOW_RADIUS_FRACTION = 1.1f
