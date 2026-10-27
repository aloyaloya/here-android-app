package ru.aloyaloya.memory.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import ru.aloyaloya.design_system.component.character.EmotionCharacter
import ru.aloyaloya.design_system.component.media.MediaPhoto
import ru.aloyaloya.design_system.component.media.MediaPlayBadge
import ru.aloyaloya.design_system.component.media.MediaRemoveButton
import ru.aloyaloya.design_system.component.section.HereCardRow
import ru.aloyaloya.design_system.component.section.HereSection
import ru.aloyaloya.design_system.component.text.HereSectionLabel
import ru.aloyaloya.design_system.format.FullDateFormat
import ru.aloyaloya.design_system.format.TimeFormat
import ru.aloyaloya.design_system.format.currentLocale
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.domain.model.MediaType
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.model.MemoryMedia
import ru.aloyaloya.domain.model.PlaceAddress
import ru.aloyaloya.mapkit.model.MapLogoPlacement
import ru.aloyaloya.mapkit.model.MapPoint
import ru.aloyaloya.mapkit.ui.YandexMap
import ru.aloyaloya.memory.R
import ru.aloyaloya.ui.emotion.character
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.labelResId
import ru.aloyaloya.ui.theme.LocalAppDarkTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import ru.aloyaloya.design_system.R as DesignSystemR

private const val GRID_COLUMNS = 3
private const val HERO_COUNT = 3
private const val DAYS_IN_WEEK = 7
private const val MINI_MAP_HALO_ALPHA = 0.55f
private val WeekdayFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("cccc")

/**
 * Секция «Когда и где»: дата со временем, место и мини-карта, если обложку заняли снимки.
 *
 * @param memory Воспоминание.
 * @param address Адрес места или `null`, пока он не найден.
 * @param modifier [Modifier], применяемый к секции.
 */
@Composable
fun MemoryWhenWhere(
    memory: Memory,
    address: PlaceAddress?,
    modifier: Modifier = Modifier
) {
    val locale = currentLocale()
    val moment = Instant.ofEpochMilli(memory.happenedAt).atZone(ZoneId.systemDefault())

    HereSection(
        title = stringResource(R.string.memory_when_where_title),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(HereSize.Memory.rowSpacing),
            modifier = Modifier.padding(HereSpacing.l)
        ) {
            HereCardRow(
                icon = DesignSystemR.drawable.ic_calendar_outline,
                title = stringResource(
                    R.string.memory_date_time,
                    FullDateFormat.withLocale(locale).format(moment),
                    TimeFormat.format(moment)
                ),
                description = dayCaption(moment.toLocalDate())
            )

            HereCardRow(
                icon = DesignSystemR.drawable.ic_place,
                title = address?.line ?: stringResource(R.string.memory_place_unknown),
                description = address?.locality
            )

            if (memory.media.isNotEmpty()) {
                MemoryMiniMap(
                    point = MapPoint(memory.latitude, memory.longitude),
                    emotion = memory.emotion
                )
            }
        }
    }
}

/**
 * Карта места с персонажем: только смотреть, без нажатий.
 *
 * @param point Точка воспоминания.
 * @param emotion Эмоция персонажа или `null`, пока ее не выбрали.
 * @param modifier [Modifier], применяемый к карте.
 */
@Composable
internal fun MemoryMiniMap(
    point: MapPoint,
    emotion: Emotion?,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(HereSize.Memory.miniMapHeight)
            .clip(HereShape.bubble)
    ) {
        YandexMap(
            modifier = Modifier.fillMaxSize(),
            movable = true,
            interactive = false,
            locationEnabled = false,
            isDarkTheme = LocalAppDarkTheme.current,
            startPosition = point,
            startZoom = MAP_ZOOM,
            logoPlacement = MapLogoPlacement.Card
        )

        if (emotion != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(HereSize.Memory.miniMapHaloSize)
                    .background(
                        color = emotion.color.solid.copy(alpha = MINI_MAP_HALO_ALPHA),
                        shape = CircleShape
                    )
            ) {
                EmotionCharacter(
                    emotion = emotion.character,
                    size = HereSize.Memory.miniMapCharacterSize
                )
            }
        }
    }
}

/**
 * Секция «Фото и видео · N»: первый снимок крупно, остальные рядами по три и по два без пустот.
 *
 * @param media Снимки и ролики воспоминания.
 * @param onMediaClick Колбэк открытия снимка на весь экран.
 * @param modifier [Modifier], применяемый к секции.
 */
@Composable
fun MemoryMediaGrid(
    media: List<MemoryMedia>,
    onMediaClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(HereSpacing.s),
        modifier = modifier
    ) {
        HereSectionLabel(
            text = stringResource(R.string.memory_media_count_title, media.size),
            modifier = Modifier.padding(horizontal = HereSpacing.xs)
        )

        MediaCollage(
            media = media,
            onMediaClick = onMediaClick
        )
    }
}

/**
 * Коллаж снимков: три первых крупным блоком, остальные рядами по три и по два без пустот.
 *
 * @param media Снимки и ролики.
 * @param modifier [Modifier], применяемый к коллажу.
 * @param onMediaClick Колбэк нажатия на снимок, без него снимки не нажимаются.
 * @param onMediaRemove Колбэк кнопки «убрать», без него кнопки нет.
 */
@Composable
internal fun MediaCollage(
    media: List<MemoryMedia>,
    modifier: Modifier = Modifier,
    onMediaClick: ((Int) -> Unit)? = null,
    onMediaRemove: ((Int) -> Unit)? = null
) {
    val spacing = HereSize.Memory.gridSpacing
    val heroCount = if (media.size >= HERO_COUNT) HERO_COUNT else 0
    val rows = remember(media.size, heroCount) { gridRows(from = heroCount, until = media.size) }

    @Composable
    fun Tile(index: Int, tileModifier: Modifier) {
        MediaGridTile(
            item = media[index],
            onClick = onMediaClick?.let { { it(index) } },
            onRemoveClick = onMediaRemove?.let { { it(index) } },
            modifier = tileModifier
        )
    }

    BoxWithConstraints(modifier = modifier.clip(HereShape.tile)) {
        val gridWidth = maxWidth
        val cell = (gridWidth - spacing * (GRID_COLUMNS - 1)) / GRID_COLUMNS

        Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
            if (heroCount > 0) {
                Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                    Tile(0, Modifier.size(cell * 2 + spacing))

                    Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
                        for (index in 1 until HERO_COUNT) {
                            Tile(index, Modifier.size(cell))
                        }
                    }
                }
            }

            rows.forEach { row ->
                val width = (gridWidth - spacing * (row.size - 1)) / row.size
                val height = if (row.size == 1) (gridWidth - spacing) / 2 else width

                Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                    row.forEach { index ->
                        Tile(index, Modifier.size(width = width, height = height))
                    }
                }
            }
        }
    }
}

/**
 * Ячейка сетки: снимок или кадр ролика со значком play.
 *
 * @param item Снимок или ролик.
 * @param onClick Колбэк открытия на весь экран.
 * @param onRemoveClick Колбэк кнопки «убрать».
 * @param modifier [Modifier] с размером ячейки.
 */
@Composable
private fun MediaGridTile(
    item: MemoryMedia,
    onClick: (() -> Unit)?,
    onRemoveClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val video = item.type == MediaType.VIDEO

    Box(
        modifier = if (onClick == null) modifier else modifier.clickable(
            onClickLabel = stringResource(R.string.memory_cover_open),
            onClick = onClick
        )
    ) {
        MediaPhoto(
            uri = item.uri,
            video = video,
            shape = RectangleShape,
            modifier = Modifier.fillMaxSize()
        )

        if (video) {
            MediaPlayBadge(
                size = HereSize.Memory.gridBadgeSize,
                iconSize = HereSize.Memory.gridBadgeIconSize,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(HereSize.Memory.gridBadgeInset)
            )
        }

        if (onRemoveClick != null) {
            MediaRemoveButton(
                onClick = onRemoveClick,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

/**
 * Индексы снимков по рядам из трех и двух без пустот, одинокий снимок — на всю ширину.
 *
 * @param from Первый индекс.
 * @param until Индекс после последнего.
 */
private fun gridRows(from: Int, until: Int): List<List<Int>> {
    val count = until - from
    val sizes = when {
        count <= 0 -> emptyList()
        count == 1 -> listOf(1)
        count % GRID_COLUMNS == 1 -> List(count / GRID_COLUMNS - 1) { GRID_COLUMNS } + listOf(2, 2)
        count % GRID_COLUMNS == 2 -> List(count / GRID_COLUMNS) { GRID_COLUMNS } + 2
        else -> List(count / GRID_COLUMNS) { GRID_COLUMNS }
    }
    var next = from
    return sizes.map { size -> List(size) { next++ } }
}

/**
 * Секция «Фото и видео» без снимков: кнопки галереи и камеры.
 *
 * @param onMediaPicked Колбэк выбранных или снятых файлов.
 * @param modifier [Modifier], применяемый к секции.
 */
@Composable
fun MemoryAddMedia(
    onMediaPicked: (List<MemoryMedia>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(HereSpacing.s),
        modifier = modifier
    ) {
        HereSectionLabel(
            text = stringResource(R.string.memory_media_title),
            modifier = Modifier.padding(horizontal = HereSpacing.xs)
        )

        MediaAddButtons(onMediaPicked = onMediaPicked)
    }
}

/**
 * Секция «Здесь же»: другие воспоминания рядом с этим местом.
 *
 * @param memories Воспоминания рядом.
 * @param onMemoryClick Колбэк нажатия на воспоминание, отдает его идентификатор.
 * @param modifier [Modifier], применяемый к секции.
 */
@Composable
fun MemoryNearby(
    memories: List<Memory>,
    onMemoryClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val rowPadding = HereSize.Memory.rowSpacing / 2

    HereSection(
        title = stringResource(R.string.memory_nearby_title),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(vertical = HereSpacing.l - rowPadding)) {
            memories.forEach { memory ->
                NearbyRow(
                    memory = memory,
                    onClick = { onMemoryClick(memory.id) },
                    modifier = Modifier.padding(vertical = rowPadding)
                )
            }
        }
    }
}

@Composable
private fun NearbyRow(
    memory: Memory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors
    val date = Instant.ofEpochMilli(memory.happenedAt).atZone(ZoneId.systemDefault())

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HereSize.CardRow.spacing),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .then(modifier)
            .padding(horizontal = HereSpacing.l)
    ) {
        NearbyThumb(memory = memory)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = memory.title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = stringResource(
                    R.string.memory_dot_pair,
                    stringResource(memory.emotion.labelResId),
                    FullDateFormat.withLocale(currentLocale()).format(date)
                ),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Первый снимок воспоминания, а без снимков — персонаж его эмоции. */
@Composable
private fun NearbyThumb(memory: Memory) {
    val size = HereSize.Memory.nearbyThumbSize
    val first = memory.media.firstOrNull()

    if (first != null) {
        MediaPhoto(
            uri = first.uri,
            video = first.type == MediaType.VIDEO,
            shape = HereShape.bubble,
            modifier = Modifier.size(size)
        )
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .background(color = memory.emotion.color.soft, shape = HereShape.bubble)
        ) {
            EmotionCharacter(
                emotion = memory.emotion.character,
                size = HereSize.Memory.nearbyCharacterSize,
                animated = false,
                shadow = false
            )
        }
    }
}

/** Подпись дня: «Среда · 3 дня назад». */
@Composable
internal fun dayCaption(date: LocalDate): String {
    val locale = currentLocale()
    val weekday = WeekdayFormat.withLocale(locale).format(date).replaceFirstChar { it.titlecase(locale) }
    return elapsedText(date)?.let { stringResource(R.string.memory_dot_pair, weekday, it) } ?: weekday
}

/** Сколько прошло с [date]: «вчера», «3 месяца назад»; `null` для дня в будущем. */
@Composable
private fun elapsedText(date: LocalDate): String? {
    val today = LocalDate.now()
    val days = ChronoUnit.DAYS.between(date, today).toInt()
    val months = ChronoUnit.MONTHS.between(date, today).toInt()
    val years = ChronoUnit.YEARS.between(date, today).toInt()

    return when {
        years > 0 -> pluralStringResource(R.plurals.memory_elapsed_years, years, years)
        months > 0 -> pluralStringResource(R.plurals.memory_elapsed_months, months, months)
        days >= DAYS_IN_WEEK -> {
            val weeks = days / DAYS_IN_WEEK
            pluralStringResource(R.plurals.memory_elapsed_weeks, weeks, weeks)
        }
        days > 1 -> pluralStringResource(R.plurals.memory_elapsed_days, days, days)
        days == 1 -> stringResource(R.string.memory_elapsed_yesterday)
        days == 0 -> stringResource(R.string.memory_elapsed_today)
        else -> null
    }
}
