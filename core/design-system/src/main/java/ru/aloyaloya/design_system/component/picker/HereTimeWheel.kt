package ru.aloyaloya.design_system.component.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import java.time.LocalTime
import kotlin.math.abs

/** Прозрачность соседних значений: кегль у всех один, удаление показывает только она. */
private const val NEAR_ALPHA = 0.5f
private const val FAR_ALPHA = 0.25f

private val Hours = (0..23).toList()

/**
 * Карточка с барабанами часов и минут и полосой выбора между ними.
 *
 * @param time Выбранное время. Минуты должны быть кратны [minuteStep].
 * @param onTimeChange Колбэк смены времени прокруткой.
 * @param modifier [Modifier], применяемый к карточке.
 * @param minuteStep Шаг барабана минут.
 */
@Composable
fun HereTimeWheel(
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    minuteStep: Int = 1
) {
    val colors = HereTheme.colors
    val minutes = remember(minuteStep) { (0..59 step minuteStep).toList() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(HereSize.TimeWheel.cardHeight)
            .clip(HereShape.tile)
            .background(colors.surfaceMuted)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HereSize.TimeWheel.bandHorizontalMargin)
                .height(HereSize.TimeWheel.bandHeight)
                .clip(HereShape.pill)
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
                values = minutes,
                selected = time.minute,
                onSelect = { onTimeChange(time.withMinute(it)) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Один барабан.
 *
 * Прокрутка липнет к значениям через [rememberSnapFlingBehavior], а выбранным
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

    // TODO: добавить хаптики
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

            val alpha = when (distance) {
                0 -> 1f
                1 -> NEAR_ALPHA
                else -> FAR_ALPHA
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(HereSize.TimeWheel.itemHeight)
            ) {
                Text(
                    text = values[index].toString().padStart(2, '0'),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = HereSize.TimeWheel.itemSize,
                        fontWeight = if (distance == 0) FontWeight.ExtraBold else FontWeight.SemiBold
                    ),
                    color = colors.textPrimary.copy(alpha = alpha)
                )
            }
        }
    }
}
