package ru.aloyaloya.calendar.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import ru.aloyaloya.calendar.R
import ru.aloyaloya.calendar.model.CalendarUiState
import ru.aloyaloya.design_system.component.calendar.CalendarDayMark
import ru.aloyaloya.design_system.component.calendar.HereMonthGrid
import ru.aloyaloya.design_system.component.calendar.HereMonthHeader
import ru.aloyaloya.design_system.component.memory.HereMemoryRow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.emoji
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/** Первый месяц, до которого можно долистать: от него считаются страницы. */
private val FirstMonth = YearMonth.of(1900, 1)

/** Сколько месяцев в листалке: хватает и назад, и вперед на любую жизнь. */
private const val MONTH_COUNT = 12 * 300

private fun monthAt(page: Int): YearMonth = FirstMonth.plusMonths(page.toLong())

private val YearMonth.page: Int
    get() = ChronoUnit.MONTHS.between(FirstMonth, this).toInt()

private val DayFormat = DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru"))
private val TimeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.forLanguageTag("ru"))

/**
 * Экран календаря: воспоминания по дням месяца.
 *
 * @param uiState Состояние экрана.
 * @param onMemoryClick Колбэк нажатия на воспоминание дня, получает его id.
 * @param modifier Внешний [Modifier] экрана.
 */
@Composable
fun CalendarScreen(
    uiState: CalendarUiState,
    onMemoryClick: (Long) -> Unit,
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

            is CalendarUiState.Content -> CalendarContent(
                uiState = uiState,
                onMemoryClick = onMemoryClick
            )
        }
    }
}

/**
 * Месяц с эмоциями в днях на карточке, под ним воспоминания выбранного дня,
 * а за всем этим фон в цвет настроения месяца.
 *
 * Сразу выбран сегодняшний день: экран открывается с тем, что было сегодня.
 *
 * Месяцы листаются свайпом: показанный месяц — это текущая страница [HorizontalPager],
 * стрелки в шапке только двигают его.
 */
@Composable
private fun CalendarContent(
    uiState: CalendarUiState.Content,
    onMemoryClick: (Long) -> Unit
) {
    val today = LocalDate.now()
    var selectedDate by rememberSaveable { mutableStateOf(today) }

    val pagerState = rememberPagerState(initialPage = YearMonth.from(today).page) { MONTH_COUNT }
    val shownMonth = monthAt(pagerState.currentPage)
    val scope = rememberCoroutineScope()

    fun showMonth(month: YearMonth) {
        scope.launch { pagerState.animateScrollToPage(month.page) }
    }

    val markByDate = uiState.emotionByDate.mapValues { (_, emotion) ->
        CalendarDayMark(emoji = emotion.emoji, color = emotion.color.solid)
    }
    val monthEmotion = remember(uiState.emotionByDate, shownMonth) {
        uiState.emotionByDate.dominantEmotionIn(shownMonth)
    }

    // TODO: плавно менять цвет фона при перелистывании месяца
    MoodGlow(color = monthEmotion?.color?.solid)

    Column(
        verticalArrangement = Arrangement.spacedBy(HereSpacing.xl),
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                top = HereSize.TopAppBar.height,
                bottom = HereSize.NavBar.height
            )
            .verticalScroll(rememberScrollState())
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
                onPreviousClick = { showMonth(shownMonth.minusMonths(1)) },
                onNextClick = { showMonth(shownMonth.plusMonths(1)) }
            )

            HorizontalPager(
                state = pagerState,
                verticalAlignment = Alignment.Top
            ) { page ->
                HereMonthGrid(
                    month = monthAt(page),
                    selectedDate = selectedDate,
                    markByDate = markByDate,
                    onDayClick = { day ->
                        selectedDate = day
                        // День соседнего месяца в сетке ведет в его месяц
                        showMonth(YearMonth.from(day))
                    }
                )
            }
        }

        DayMemories(
            date = selectedDate,
            memories = uiState.memoriesByDate[selectedDate].orEmpty(),
            onTodayClick = if (selectedDate != today || shownMonth != YearMonth.from(today)) {
                {
                    selectedDate = today
                    showMonth(YearMonth.from(today))
                }
            } else {
                null
            },
            onMemoryClick = onMemoryClick
        )
    }
}

/**
 * Заголовок выбранного дня и его воспоминания, или подсказка, что их нет.
 *
 * @param onTodayClick Колбэк возврата к сегодняшнему дню или `null`, если он и так выбран
 * и его месяц на экране: тогда кнопки нет.
 */
@Composable
private fun DayMemories(
    date: LocalDate,
    memories: List<Memory>,
    onTodayClick: (() -> Unit)?,
    onMemoryClick: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(HereSize.MemoryRow.spacing)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = HereSize.Calendar.navButtonSize)
        ) {
            Text(
                text = DayFormat.format(date),
                style = MaterialTheme.typography.titleMedium,
                color = HereTheme.colors.textPrimary
            )

            if (onTodayClick != null) {
                TodayButton(onClick = onTodayClick)
            }
        }

        if (memories.isEmpty()) {
            Text(
                text = stringResource(R.string.calendar_day_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = HereTheme.colors.textTertiary
            )
        }

        memories.forEach { memory ->
            HereMemoryRow(
                emoji = memory.emotion.emoji,
                color = memory.emotion.color.soft,
                title = memory.title,
                subtitle = TimeFormat.format(
                    Instant.ofEpochMilli(memory.happenedAt).atZone(ZoneId.systemDefault())
                ),
                onClick = { onMemoryClick(memory.id) }
            )
        }
    }
}

/** Кнопка возврата к сегодняшнему дню. */
@Composable
private fun TodayButton(onClick: () -> Unit) {
    Text(
        text = stringResource(R.string.calendar_today),
        style = MaterialTheme.typography.labelMedium,
        color = HereTheme.colors.accent,
        modifier = Modifier
            .clip(HereShape.pill)
            .background(HereTheme.colors.accentContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = HereSpacing.m, vertical = HereSpacing.s)
    )
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
