package ru.aloyaloya.memory.presentation

import androidx.activity.compose.LocalActivity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.core.view.WindowCompat
import ru.aloyaloya.design_system.component.emotion.EmotionTag
import ru.aloyaloya.design_system.component.media.MediaPhoto
import ru.aloyaloya.design_system.component.media.MediaPlayBadge
import ru.aloyaloya.design_system.format.FullDateFormat
import ru.aloyaloya.design_system.format.TimeFormat
import ru.aloyaloya.design_system.format.currentLocale
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.MediaType
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.model.MemoryMedia
import ru.aloyaloya.memory.R
import ru.aloyaloya.memory.model.MemorySheet
import ru.aloyaloya.memory.model.MemoryUiState
import ru.aloyaloya.memory.presentation.component.DeleteMemoryDialog
import ru.aloyaloya.memory.presentation.component.MediaViewer
import ru.aloyaloya.memory.presentation.component.MemoryCover
import ru.aloyaloya.memory.presentation.component.MemoryTopBar
import ru.aloyaloya.memory.presentation.component.coverHeight
import ru.aloyaloya.ui.emotion.character
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.labelResId
import ru.aloyaloya.ui.theme.LocalAppDarkTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import ru.aloyaloya.design_system.R as DesignSystemR

private const val SEPARATOR = " · "
private const val DAYS_IN_WEEK = 7

/**
 * Экран воспоминания.
 *
 * @param uiState Состояние экрана.
 * @param onBackClick Колбэк возврата назад.
 * @param onEditClick Колбэк пункта «Изменить» в меню шапки.
 * @param onDeleteClick Колбэк пункта «Удалить» в меню шапки.
 * @param onMediaClick Колбэк открытия снимка на весь экран.
 * @param onViewerDismiss Колбэк закрытия просмотра снимка.
 * @param onDeleteConfirm Колбэк подтверждения удаления.
 * @param onSheetDismiss Колбэк закрытия открытого листа.
 * @param modifier [Modifier], применяемый к экрану.
 */
@Composable
fun MemoryScreen(
    uiState: MemoryUiState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMediaClick: (Int) -> Unit,
    onViewerDismiss: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onSheetDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        MemoryUiState.Loading -> Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxSize()
                .background(HereTheme.colors.background)
        ) {
            CircularProgressIndicator(color = HereTheme.colors.accent)
        }

        is MemoryUiState.Content -> {
            MemoryContent(
                memory = uiState.memory,
                address = uiState.address?.line,
                onBackClick = onBackClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                onMediaClick = onMediaClick,
                modifier = modifier
            )

            uiState.viewedMedia?.let { index ->
                MediaViewer(
                    media = uiState.memory.media,
                    initialIndex = index,
                    onDismissRequest = onViewerDismiss
                )
            }

            when (uiState.activeSheet) {
                MemorySheet.DELETE -> DeleteMemoryDialog(
                    title = uiState.memory.title,
                    onConfirmClick = onDeleteConfirm,
                    onDismissRequest = onSheetDismiss
                )

                null -> Unit
            }
        }

        MemoryUiState.NotFound -> LaunchedEffect(Unit) { onBackClick() }
    }
}

/** Обложка, рассказ под ней и шапка поверх. */
@Composable
private fun MemoryContent(
    memory: Memory,
    address: String?,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMediaClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val photos = memory.media.isNotEmpty()
    val listState = rememberLazyListState()
    val pagerState = rememberPagerState { memory.media.size }

    val density = LocalDensity.current
    val statusBarHeight = WindowInsets.statusBars.getTop(density)
    val collapseOffset = with(density) {
        (coverHeight(photos) - HereSize.TopAppBar.height).roundToPx()
    } - statusBarHeight

    val collapsed by remember(collapseOffset) {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 ||
                listState.firstVisibleItemScrollOffset >= collapseOffset
        }
    }

    StatusBarIcons(light = (photos && !collapsed) || LocalAppDarkTheme.current)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HereTheme.colors.background)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            item(key = "cover") {
                MemoryCover(
                    memory = memory,
                    pagerState = pagerState,
                    scrollOffset = {
                        if (listState.firstVisibleItemIndex == 0) {
                            listState.firstVisibleItemScrollOffset
                        } else {
                            0
                        }
                    },
                    onMediaClick = onMediaClick
                )
            }

            item(key = "header") {
                MemoryHeader(
                    memory = memory,
                    modifier = Modifier
                        .padding(horizontal = HereSpacing.l)
                        .padding(top = HereSize.Memory.contentTopPadding)
                )
            }

            item(key = "details") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(HereSize.Memory.sectionSpacing),
                    modifier = Modifier
                        .padding(horizontal = HereSpacing.l)
                        .padding(top = HereSize.Memory.sectionSpacing)
                ) {
                    MemoryFacts(happenedAt = memory.happenedAt, address = address)

                    if (photos) {
                        MediaSection(media = memory.media, onMediaClick = onMediaClick)
                    }
                }
            }

            item(key = "bottom") {
                Spacer(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .height(HereSize.Memory.bottomPadding)
                )
            }
        }

        MemoryTopBar(
            title = memory.title,
            collapsed = collapsed,
            overPhotos = photos,
            onBackClick = onBackClick,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick
        )
    }
}

/**
 * Цвет значков статус-бара, пока экран открыт: при уходе с экрана возвращается прежний.
 *
 * @param light Значки светлые.
 */
@Composable
private fun StatusBarIcons(light: Boolean) {
    val window = LocalActivity.current?.window ?: return
    val view = LocalView.current

    DisposableEffect(window, light) {
        val controller = WindowCompat.getInsetsController(window, view)
        val previous = controller.isAppearanceLightStatusBars
        controller.isAppearanceLightStatusBars = !light
        onDispose { controller.isAppearanceLightStatusBars = previous }
    }
}

/** Эмоция, заголовок и текст воспоминания. */
@Composable
private fun MemoryHeader(
    memory: Memory,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors

    Column(
        verticalArrangement = Arrangement.spacedBy(HereSize.Memory.textSpacing),
        modifier = modifier
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HereSize.Memory.headerSpacing)) {
            EmotionTag(
                character = memory.emotion.character,
                label = stringResource(memory.emotion.labelResId),
                color = memory.emotion.color.soft
            )

            Text(
                text = memory.title,
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textPrimary
            )
        }

        if (memory.description.isNotBlank()) {
            Text(
                text = memory.description,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textBody
            )
        }
    }
}

@Composable
private fun MediaSection(
    media: List<MemoryMedia>,
    onMediaClick: (Int) -> Unit
) {
    val colors = HereTheme.colors

    Column(verticalArrangement = Arrangement.spacedBy(HereSize.Memory.mediaSpacing)) {
        Text(
            text = stringResource(R.string.memory_media_title),
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(HereSize.Memory.mediaSpacing),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            media.forEachIndexed { index, item ->
                Box(contentAlignment = Alignment.Center) {
                    MediaPhoto(
                        uri = item.uri,
                        video = item.type == MediaType.VIDEO,
                        modifier = Modifier
                            .size(HereSize.Memory.mediaSize)
                            .clickable { onMediaClick(index) }
                    )

                    if (item.type == MediaType.VIDEO) {
                        MediaPlayBadge()
                    }
                }
            }
        }
    }
}

/** Когда и где это было: дата и сколько прошло, время и адрес, если он известен. */
@Composable
private fun MemoryFacts(
    happenedAt: Long,
    address: String?
) {
    val dateFormat = FullDateFormat.withLocale(currentLocale())
    val moment = Instant.ofEpochMilli(happenedAt).atZone(ZoneId.systemDefault())

    Column(verticalArrangement = Arrangement.spacedBy(HereSpacing.s)) {
        MemoryFact(
            icon = DesignSystemR.drawable.ic_calendar_outline,
            text = dateFormat.format(moment),
            hint = elapsedText(moment.toLocalDate())
        )

        MemoryFact(
            icon = DesignSystemR.drawable.ic_clock_outline,
            text = TimeFormat.format(moment)
        )

        address?.let { place ->
            MemoryFact(
                icon = DesignSystemR.drawable.ic_place,
                text = place
            )
        }
    }
}

@Composable
private fun MemoryFact(
    @DrawableRes icon: Int,
    text: String,
    hint: String? = null
) {
    val colors = HereTheme.colors

    Row(horizontalArrangement = Arrangement.spacedBy(HereSpacing.m)) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(HereSize.Memory.factIconSize)
        )

        Text(
            text = buildAnnotatedString {
                append(text)
                hint?.let {
                    withStyle(SpanStyle(color = colors.textSecondary)) {
                        append(SEPARATOR + it)
                    }
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary
        )
    }
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
