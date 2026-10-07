package ru.aloyaloya.calendar.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import ru.aloyaloya.calendar.R
import ru.aloyaloya.calendar.model.CalendarUiState
import ru.aloyaloya.design_system.component.calendar.CalendarDayMark
import ru.aloyaloya.design_system.component.calendar.HereMonthGrid
import ru.aloyaloya.design_system.component.calendar.HereMonthHeader
import ru.aloyaloya.design_system.component.character.BubbleTail
import ru.aloyaloya.design_system.component.character.CharacterBubble
import ru.aloyaloya.design_system.component.character.CharacterEmotion
import ru.aloyaloya.design_system.component.character.EmotionCharacter
import ru.aloyaloya.design_system.component.memory.HereMemoryRow
import ru.aloyaloya.design_system.extension.Entrance
import ru.aloyaloya.design_system.extension.entrance
import ru.aloyaloya.design_system.format.DayMonthFormat
import ru.aloyaloya.design_system.format.TimeFormat
import ru.aloyaloya.design_system.format.currentLocale
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.ui.emotion.character
import ru.aloyaloya.ui.emotion.color
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/** Первый месяц, до которого можно долистать: от него считаются страницы. */
private val FirstMonth = YearMonth.of(1900, 1)

/** Сколько месяцев в листалке: хватает и назад, и вперед на любую жизнь. */
private const val MONTH_COUNT = 12 * 300

/** Смена дня: старые карточки сжимаются и тают, новые вырастают. */
private const val DAY_SWITCH_MILLIS = 220
private const val DAY_SWITCH_SCALE = 0.92f

/** Пустые дни — одно содержимое: персонаж остается на месте, меняется только реплика. */
private const val EMPTY_DAY_KEY = "empty"

private fun monthAt(page: Int): YearMonth = FirstMonth.plusMonths(page.toLong())

private val YearMonth.page: Int
    get() = ChronoUnit.MONTHS.between(FirstMonth, this).toInt()

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
        CalendarDayMark(character = emotion.character)
    }
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

        AnimatedContent(
            targetState = selectedDate,
            transitionSpec = {
                (fadeIn(tween(DAY_SWITCH_MILLIS)) +
                    scaleIn(tween(DAY_SWITCH_MILLIS), initialScale = DAY_SWITCH_SCALE)) togetherWith
                    (fadeOut(tween(DAY_SWITCH_MILLIS)) +
                        scaleOut(tween(DAY_SWITCH_MILLIS), targetScale = DAY_SWITCH_SCALE)) using
                    SizeTransform(clip = false)
            },
            contentAlignment = Alignment.TopCenter,
            contentKey = { date ->
                if (uiState.memoriesByDate[date].isNullOrEmpty()) EMPTY_DAY_KEY else date
            },
            label = "calendar-day"
        ) { date ->
            DayMemories(
                date = date,
                memories = uiState.memoriesByDate[date].orEmpty(),
                onTodayClick = if (date != today || shownMonth != YearMonth.from(today)) {
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
}

/**
 * Заголовок выбранного дня и его воспоминания или персонаж, если их нет.
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
    if (memories.isEmpty()) {
        EmptyDayHint(date = date)
        return
    }

    val dayFormat = DayMonthFormat.withLocale(currentLocale())

    Column(verticalArrangement = Arrangement.spacedBy(HereSize.MemoryRow.spacing)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = HereSize.Calendar.navButtonSize)
        ) {
            Text(
                text = dayFormat.format(date),
                style = MaterialTheme.typography.titleMedium,
                color = HereTheme.colors.textPrimary
            )

            if (onTodayClick != null) {
                TodayButton(onClick = onTodayClick)
            }
        }

        memories.forEach { memory ->
            HereMemoryRow(
                character = memory.emotion.character,
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

@Composable
private fun EmptyDayHint(date: LocalDate) {
    val colors = HereTheme.colors
    val today = LocalDate.now()
    val text = when {
        date == today -> R.string.calendar_day_empty_today
        date.isAfter(today) -> R.string.calendar_day_empty_future
        else -> R.string.calendar_day_empty
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = HereSize.Calendar.emptyTopPadding,
                bottom = HereSpacing.l
            )
    ) {
        CharacterBubble(
            text = stringResource(text),
            containerColor = colors.surface,
            contentColor = colors.textPrimary,
            tail = BubbleTail.BOTTOM,
            borderColor = colors.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .widthIn(max = HereSize.Calendar.emptyBubbleMaxWidth)
                .entrance(
                    kind = Entrance.BUBBLE,
                    delayMillis = EMPTY_BUBBLE_MILLIS,
                    origin = EmptyBubbleOrigin
                )
        )

        EmotionCharacter(
            emotion = CharacterEmotion.YOU,
            size = HereSize.EmotionCharacter.small,
            modifier = Modifier
                .padding(top = HereSpacing.xs)
                .entrance(kind = Entrance.DROP)
        )
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

/** Реплика пустого дня появляется после падения персонажа. */
private const val EMPTY_BUBBLE_MILLIS = 700

/** Опора реплики: хвостик снизу. */
private val EmptyBubbleOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 1f)
