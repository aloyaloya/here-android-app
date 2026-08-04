package ru.aloyaloya.memory.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import ru.aloyaloya.design_system.component.sheet.HereBottomSheet
import ru.aloyaloya.design_system.extension.cardShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.memory.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

/** Шаг барабана минут: до минуты воспоминание уточнять незачем. */
private const val MINUTE_STEP = 10

private val Hours = (0..23).toList()
private val Minutes = (0..59 step MINUTE_STEP).toList()

private val TimeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.forLanguageTag("ru"))

/** Время, которое предлагают чипы. */
private val QuickTimes = listOf(
    LocalTime.of(9, 0),
    LocalTime.of(13, 0),
    LocalTime.of(21, 0)
)

/**
 * Лист выбора времени события.
 *
 * Как и лист даты, наружу отдает значение только по кнопке подтверждения.
 *
 * Минуты крутятся с шагом [MINUTE_STEP], поэтому время открытия округляется вниз:
 * барабан не умеет показать значение, которого в нем нет.
 *
 * @param initialTime Время, с которого лист открывается.
 * @param onDismissRequest Колбэк закрытия листа без выбора.
 * @param onTimeSelected Колбэк подтвержденного времени.
 */
@Composable
fun TimeSheet(
    initialTime: LocalTime,
    onDismissRequest: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    var selectedTime by rememberSaveable {
        mutableStateOf(LocalTime.of(initialTime.hour, initialTime.minute / MINUTE_STEP * MINUTE_STEP))
    }

    HereBottomSheet(onDismissRequest = onDismissRequest) {
        Column(
            verticalArrangement = Arrangement.spacedBy(HereSize.Sheet.contentSpacing),
            modifier = Modifier.padding(
                start = HereSize.Sheet.horizontalPadding,
                end = HereSize.Sheet.horizontalPadding,
                bottom = HereSize.Sheet.bottomPadding
            )
        ) {
            SheetTitle(
                label = stringResource(R.string.time_sheet_label),
                value = TimeFormat.format(selectedTime)
            )

            TimeWheels(
                time = selectedTime,
                onTimeChange = { selectedTime = it }
            )

            QuickTimeChips(
                selectedTime = selectedTime,
                onTimeClick = { selectedTime = it }
            )

            SheetActions(
                onCancelClick = onDismissRequest,
                onConfirmClick = { onTimeSelected(selectedTime) }
            )
        }
    }
}

/** Карточка с двумя барабанами и полосой выбора между ними. */
@Composable
private fun TimeWheels(
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit
) {
    val colors = HereTheme.colors

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(HereSize.TimeWheel.cardHeight)
            .cardShadow(HereShape.card)
            .clip(HereShape.card)
            .background(colors.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HereSize.TimeWheel.bandHorizontalMargin)
                .height(HereSize.TimeWheel.bandHeight)
                .clip(MaterialTheme.shapes.medium)
                .background(colors.accentContainer)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Wheel(
                values = Hours,
                selected = time.hour,
                onSelect = { onTimeChange(time.withHour(it)) },
                modifier = Modifier.weight(1f)
            )

            Text(
                text = ":",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = HereSize.TimeWheel.colonSize,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = colors.accent,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(HereSize.TimeWheel.colonWidth)
            )

            Wheel(
                values = Minutes,
                selected = time.minute,
                onSelect = { onTimeChange(time.withMinute(it)) },
                modifier = Modifier.weight(1f)
            )
        }

        WheelFades()
    }
}

/**
 * Один барабан.
 *
 * Прокрутка «липнет» к значениям через [rememberSnapFlingBehavior], а выбранным
 * считается то, что оказалось в середине. Пустые отступы сверху и снизу нужны,
 * чтобы первое и последнее значение тоже могли встать по центру.
 */
@Composable
private fun Wheel(
    values: List<Int>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors
    val selectedIndex = values.indexOf(selected).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)

    val edgePadding = (HereSize.TimeWheel.cardHeight - HereSize.TimeWheel.itemHeight) / 2

    val centerIndex by remember {
        derivedStateOf {
            val offset = listState.firstVisibleItemScrollOffset
            val itemSize = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 1
            listState.firstVisibleItemIndex + if (offset > itemSize / 2) 1 else 0
        }
    }

    val currentOnSelect by rememberUpdatedState(onSelect)

    LaunchedEffect(listState) {
        snapshotFlow { centerIndex }.collect { index ->
            values.getOrNull(index)?.let(currentOnSelect)
        }
    }

    LaunchedEffect(selected) {
        if (values.getOrNull(centerIndex) != selected) listState.animateScrollToItem(selectedIndex)
    }

    LazyColumn(
        state = listState,
        flingBehavior = rememberSnapFlingBehavior(listState),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = edgePadding),
        modifier = modifier.fillMaxSize()
    ) {
        items(values.size) { index ->
            val distance = abs(index - centerIndex)

            val fontSize = when (distance) {
                0 -> HereSize.TimeWheel.selectedSize
                1 -> HereSize.TimeWheel.nearSize
                else -> HereSize.TimeWheel.farSize
            }

            val color = when (distance) {
                0 -> colors.textPrimary
                1 -> colors.textTertiary
                else -> colors.textQuaternary
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(HereSize.TimeWheel.itemHeight)
            ) {
                Text(
                    text = values[index].toString().padStart(2, '0'),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = fontSize,
                        fontWeight = if (distance == 0) FontWeight.ExtraBold else FontWeight.SemiBold
                    ),
                    color = color
                )
            }
        }
    }
}

/** Растворение значений у верхнего и нижнего края карточки. */
@Composable
private fun WheelFades() {
    val surface = HereTheme.colors.surface

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HereSize.TimeWheel.fadeHeight)
                .background(Brush.verticalGradient(listOf(surface, Color.Transparent)))
        )

        Box(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HereSize.TimeWheel.fadeHeight)
                .background(Brush.verticalGradient(listOf(Color.Transparent, surface)))
        )
    }
}

/** Чипы частого времени: «Сейчас» и три круглых часа. */
@Composable
private fun QuickTimeChips(
    selectedTime: LocalTime,
    onTimeClick: (LocalTime) -> Unit
) {
    val now = remember { LocalTime.now() }
    val roundedNow = remember(now) { LocalTime.of(now.hour, now.minute / MINUTE_STEP * MINUTE_STEP) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(HereSize.TimeWheel.chipSpacing),
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        QuickTimeChip(
            text = stringResource(R.string.time_sheet_now),
            selected = selectedTime == roundedNow,
            onClick = { onTimeClick(roundedNow) }
        )

        QuickTimes.forEach { time ->
            QuickTimeChip(
                text = TimeFormat.format(time),
                selected = selectedTime == time,
                onClick = { onTimeClick(time) }
            )
        }
    }
}

/** Один чип частого времени. */
@Composable
private fun QuickTimeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = HereTheme.colors

    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            fontSize = HereSize.TimeWheel.chipSize,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
        ),
        color = if (selected) colors.accent else colors.textSecondary,
        modifier = Modifier
            .clip(HereShape.chip)
            .background(if (selected) colors.accentContainer else colors.surfaceMuted)
            .clickable(onClick = onClick)
            .padding(
                vertical = HereSize.TimeWheel.chipVerticalPadding,
                horizontal = HereSize.TimeWheel.chipHorizontalPadding
            )
    )
}
