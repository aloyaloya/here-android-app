package ru.aloyaloya.summary.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import ru.aloyaloya.design_system.component.button.HereSecondaryButton
import ru.aloyaloya.design_system.component.emotion.EmotionBadge
import ru.aloyaloya.design_system.component.media.MediaPhoto
import ru.aloyaloya.design_system.component.media.MediaPlayBadge
import ru.aloyaloya.design_system.component.memory.HereMemoryRow
import ru.aloyaloya.design_system.component.text.HereSectionLabel
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.MediaType
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.summary.R
import ru.aloyaloya.summary.model.EmotionShare
import ru.aloyaloya.summary.model.MoodPlace
import ru.aloyaloya.summary.model.SummaryPeriod
import ru.aloyaloya.summary.model.SummaryUiState
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.emoji
import ru.aloyaloya.ui.emotion.labelResId
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private val MonthFormat = DateTimeFormatter.ofPattern("LLLL yyyy", Locale.forLanguageTag("ru"))
private val DateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"))

/** С меньшим числом воспоминаний итогам не о чем рассказать. */
private const val MIN_MEMORY_COUNT = 3

/**
 * Экран итогов: что и где чувствовалось за выбранный период.
 *
 * @param uiState Состояние экрана.
 * @param onPeriodSelected Колбэк выбора периода.
 * @param onPlaceClick Колбэк нажатия на место настроения: отдает его точку.
 * @param onMemoryClick Колбэк нажатия на воспоминание в «Вспомнить».
 * @param onRecallAnother Колбэк кнопки «Другое».
 * @param modifier Внешний [Modifier] экрана.
 */
@Composable
fun SummaryScreen(
    uiState: SummaryUiState,
    onPeriodSelected: (SummaryPeriod) -> Unit,
    onPlaceClick: (latitude: Double, longitude: Double) -> Unit,
    onMemoryClick: (Long) -> Unit,
    onRecallAnother: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HereTheme.colors.background)
    ) {
        when (uiState) {
            SummaryUiState.Loading -> {
                CircularProgressIndicator(
                    color = HereTheme.colors.accent,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is SummaryUiState.Content -> SummaryContent(
                uiState = uiState,
                onPeriodSelected = onPeriodSelected,
                onPlaceClick = onPlaceClick,
                onMemoryClick = onMemoryClick,
                onRecallAnother = onRecallAnother
            )
        }
    }
}

@Composable
private fun SummaryContent(
    uiState: SummaryUiState.Content,
    onPeriodSelected: (SummaryPeriod) -> Unit,
    onPlaceClick: (latitude: Double, longitude: Double) -> Unit,
    onMemoryClick: (Long) -> Unit,
    onRecallAnother: () -> Unit
) {
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
        PeriodSelector(
            selected = uiState.period,
            onSelect = onPeriodSelected
        )

        if (uiState.memoryCount < MIN_MEMORY_COUNT) {
            NotEnoughMemories()
            return@Column
        }

        PeriodCard(uiState)

        EmotionMixCard(uiState.emotionShares, total = uiState.memoryCount)

        MoodPlaces(uiState.places, onPlaceClick)

        uiState.recall?.let { memory ->
            Recall(
                memory = memory,
                canRecallAnother = uiState.canRecallAnother,
                onClick = { onMemoryClick(memory.id) },
                onAnotherClick = onRecallAnother
            )
        }
    }
}

/**
 * Карточка периода: название, главная эмоция и сколько всего было.
 */
@Composable
private fun PeriodCard(uiState: SummaryUiState.Content) {
    val colors = HereTheme.colors
    val emotion = uiState.dominantEmotion

    Column(
        verticalArrangement = Arrangement.spacedBy(HereSize.Summary.cardSpacing),
        modifier = Modifier
            .fillMaxWidth()
            .clip(HereShape.tile)
            .background(emotion?.color?.soft ?: colors.surface)
            .padding(HereSpacing.l)
    ) {
        Text(
            text = periodTitle(uiState.period),
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary
        )

        if (emotion != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HereSpacing.m)
            ) {
                Text(
                    text = emotion.emoji,
                    fontSize = HereSize.Summary.emojiSize
                )

                Column {
                    Text(
                        text = stringResource(R.string.summary_dominant_label),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = stringResource(emotion.labelResId),
                        style = MaterialTheme.typography.headlineSmall,
                        color = colors.textPrimary
                    )
                }
            }
        }

        Text(
            text = pluralStringResource(
                R.plurals.summary_memory_count,
                uiState.memoryCount,
                uiState.memoryCount
            ) + " · " + pluralStringResource(
                R.plurals.summary_day_count,
                uiState.dayCount,
                uiState.dayCount
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary
        )
    }
}

/**
 * Смесь эмоций: одна полоса из отрезков по долям и расшифровка под ней.
 */
@Composable
private fun EmotionMixCard(
    shares: List<EmotionShare>,
    total: Int
) {
    val colors = HereTheme.colors

    Column(verticalArrangement = Arrangement.spacedBy(HereSpacing.s)) {
        SummarySectionLabel(stringResource(R.string.summary_emotions_title))

        Column(
            verticalArrangement = Arrangement.spacedBy(HereSize.Summary.cardSpacing),
            modifier = Modifier
                .fillMaxWidth()
                .clip(HereShape.tile)
                .background(colors.surface)
                .padding(HereSpacing.l)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(HereSize.Summary.barGap),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HereSize.Summary.barHeight)
                    .clip(HereShape.pill)
            ) {
                shares.forEach { share ->
                    Box(
                        modifier = Modifier
                            .weight(share.count.toFloat())
                            .fillMaxHeight()
                            .background(share.emotion.color.solid)
                    )
                }
            }

            shares.forEach { share ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(HereSpacing.s)
                ) {
                    Text(
                        text = share.emotion.emoji,
                        fontSize = HereSize.Summary.legendEmojiSize
                    )
                    Text(
                        text = stringResource(share.emotion.labelResId),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${(share.count * 100f / total).roundToInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}

/**
 * Места настроения: где воспоминаний больше всего и что там чувствовалось.
 */
@Composable
private fun MoodPlaces(
    places: List<MoodPlace>,
    onPlaceClick: (latitude: Double, longitude: Double) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(HereSpacing.s)) {
        SummarySectionLabel(stringResource(R.string.summary_places_title))

        places.forEach { place ->
            HereMemoryRow(
                emoji = place.dominantEmotion.emoji,
                color = place.dominantEmotion.color.soft,
                title = place.address ?: stringResource(R.string.summary_place_unknown),
                subtitle = pluralStringResource(
                    R.plurals.summary_memory_count,
                    place.memoryCount,
                    place.memoryCount
                ),
                onClick = { onPlaceClick(place.latitude, place.longitude) }
            )
        }
    }
}

/**
 * «Вспомнить»: одно случайное воспоминание периода, лучше со снимком.
 */
@Composable
private fun Recall(
    memory: Memory,
    canRecallAnother: Boolean,
    onClick: () -> Unit,
    onAnotherClick: () -> Unit
) {
    val colors = HereTheme.colors
    val media = memory.media.firstOrNull()

    Column(verticalArrangement = Arrangement.spacedBy(HereSpacing.s)) {
        SummarySectionLabel(stringResource(R.string.summary_recall_title))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(HereShape.tile)
                .background(colors.surface)
                .clickable(onClick = onClick)
        ) {
            if (media != null) {
                val video = media.type == MediaType.VIDEO

                Box(contentAlignment = Alignment.Center) {
                    MediaPhoto(
                        uri = media.uri,
                        video = video,
                        shape = RectangleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(HereSize.Summary.recallPhotoHeight)
                    )

                    if (video) MediaPlayBadge()
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HereSize.MemoryRow.badgeSpacing),
                modifier = Modifier.padding(HereSize.MemoryRow.padding)
            ) {
                EmotionBadge(
                    emoji = memory.emotion.emoji,
                    color = memory.emotion.color.soft
                )

                Column(verticalArrangement = Arrangement.spacedBy(HereSize.MemoryRow.textSpacing)) {
                    Text(
                        text = memory.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = DateFormat.format(
                            Instant.ofEpochMilli(memory.happenedAt).atZone(ZoneId.systemDefault())
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textTertiary
                    )
                }
            }
        }

        if (canRecallAnother) {
            HereSecondaryButton(
                text = stringResource(R.string.summary_recall_another),
                onClick = onAnotherClick
            )
        }
    }
}

/** Вместо итогов, пока воспоминаний меньше [MIN_MEMORY_COUNT]. */
@Composable
private fun NotEnoughMemories() {
    Text(
        text = stringResource(R.string.summary_not_enough),
        style = MaterialTheme.typography.bodyMedium,
        color = HereTheme.colors.textSecondary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(HereShape.tile)
            .background(HereTheme.colors.surface)
            .padding(HereSpacing.xl)
    )
}

/** Заголовок карточки: текущий месяц, текущий год или «За всё время». */
@Composable
private fun periodTitle(period: SummaryPeriod): String {
    val today = LocalDate.now()

    return when (period) {
        SummaryPeriod.MONTH -> MonthFormat.format(today).replaceFirstChar { it.uppercase() }
        SummaryPeriod.YEAR -> stringResource(R.string.summary_title_year, today.year)
        SummaryPeriod.ALL_TIME -> stringResource(R.string.summary_title_all_time)
    }
}

@Composable
private fun PeriodSelector(
    selected: SummaryPeriod,
    onSelect: (SummaryPeriod) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(HereSpacing.s)) {
        SummaryPeriod.entries.forEach { period ->
            PeriodChip(
                text = stringResource(period.labelResId),
                selected = period == selected,
                onClick = { onSelect(period) }
            )
        }
    }
}

@Composable
private fun PeriodChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = HereTheme.colors

    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = if (selected) colors.accent else colors.textSecondary,
        modifier = Modifier
            .clip(HereShape.pill)
            .background(if (selected) colors.accentContainer else colors.surfaceMuted)
            .clickable(onClick = onClick)
            .padding(horizontal = HereSpacing.l, vertical = HereSpacing.s)
    )
}

@Composable
private fun SummarySectionLabel(text: String) {
    HereSectionLabel(
        text = text,
        modifier = Modifier.padding(horizontal = HereSpacing.xs)
    )
}
