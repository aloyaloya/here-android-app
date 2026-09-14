package ru.aloyaloya.memory.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.aloyaloya.design_system.component.button.HereIconButton
import ru.aloyaloya.design_system.component.emotion.EmotionPin
import ru.aloyaloya.design_system.component.emotion.EmotionTag
import ru.aloyaloya.design_system.component.media.MediaPhoto
import ru.aloyaloya.design_system.component.media.MediaPlayBadge
import ru.aloyaloya.design_system.extension.sheetShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.model.MediaType
import ru.aloyaloya.domain.model.MemoryMedia
import ru.aloyaloya.mapkit.model.MapPoint
import ru.aloyaloya.mapkit.ui.YandexMap
import ru.aloyaloya.memory.R
import ru.aloyaloya.memory.model.MemorySheet
import ru.aloyaloya.memory.model.MemoryUiState
import ru.aloyaloya.memory.presentation.component.DeleteMemorySheet
import ru.aloyaloya.memory.presentation.component.MediaViewer
import ru.aloyaloya.memory.presentation.component.MemoryActionsSheet
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.emoji
import ru.aloyaloya.ui.emotion.labelResId
import ru.aloyaloya.ui.theme.LocalAppDarkTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import ru.aloyaloya.design_system.R as DesignSystemR

private const val HALO_ALPHA = 0.42f
private const val SCRIM_ALPHA = 0.28f
private const val SHEET_MAX_HEIGHT_FRACTION = 0.6f
private const val MAP_ZOOM = 15.5f

private const val SEPARATOR = " · "
private val DateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"))
private val TimeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.forLanguageTag("ru"))

/**
 * Экран воспоминания.
 *
 * @param uiState Состояние экрана.
 * @param onBackClick Колбэк возврата назад.
 * @param onMoreClick Колбэк открытия меню действий.
 * @param onEditClick Колбэк выбора редактирования в меню.
 * @param onDeleteClick Колбэк выбора удаления в меню.
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
    onMoreClick: () -> Unit,
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
                address = uiState.address,
                onBackClick = onBackClick,
                onMoreClick = onMoreClick,
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
                MemorySheet.ACTIONS -> MemoryActionsSheet(
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                    onDismissRequest = onSheetDismiss
                )

                MemorySheet.DELETE -> DeleteMemorySheet(
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

/**
 * Место на карте под листом с рассказом о воспоминании.
 */
@Composable
private fun MemoryContent(
    memory: Memory,
    address: String?,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit,
    onMediaClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var sheetHeight by remember { mutableStateOf(0.dp) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(HereTheme.colors.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = mapBottomPadding(sheetHeight))
        ) {
            YandexMap(
                modifier = Modifier.fillMaxSize(),
                interactive = false,
                locationEnabled = false,
                isDarkTheme = LocalAppDarkTheme.current,
                startPosition = MapPoint(memory.latitude, memory.longitude),
                startZoom = MAP_ZOOM
            )

            MemoryPin(
                emotion = memory.emotion,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        TopScrim(modifier = Modifier.align(Alignment.TopCenter))

        HereIconButton(
            icon = DesignSystemR.drawable.ic_arrow_back,
            contentDescription = stringResource(R.string.memory_back),
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(
                    top = HereSize.Memory.actionsTopPadding,
                    start = HereSize.Memory.actionsHorizontalPadding
                )
        )

        HereIconButton(
            icon = DesignSystemR.drawable.ic_more,
            contentDescription = stringResource(R.string.memory_more),
            onClick = onMoreClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(
                    top = HereSize.Memory.actionsTopPadding,
                    end = HereSize.Memory.actionsHorizontalPadding
                )
        )

        MemoryDetailSheet(
            memory = memory,
            address = address,
            onMediaClick = onMediaClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .heightIn(max = maxHeight * SHEET_MAX_HEIGHT_FRACTION)
                .onSizeChanged { size ->
                    sheetHeight = with(density) { size.height.toDp() }
                }
        )
    }
}

/**
 * Отступ карты снизу: высота листа без его скругления.
 */
private fun mapBottomPadding(sheetHeight: Dp): Dp =
    (sheetHeight - HereSize.Memory.sheetCornerOverlap).coerceAtLeast(0.dp)

/** Пин воспоминания с ореолом: та же метка, что на карте, только крупно. */
@Composable
private fun MemoryPin(
    emotion: Emotion,
    modifier: Modifier = Modifier
) {
    val color = emotion.color.solid

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(HereSize.Memory.haloSize)
            .background(color = color.copy(alpha = HALO_ALPHA), shape = CircleShape)
    ) {
        EmotionPin(
            emoji = emotion.emoji,
            color = color,
            size = HereSize.Memory.pinSize,
            border = HereSize.Memory.pinBorder,
            emojiSize = HereSize.Memory.pinEmojiSize
        )
    }
}

/** Затемнение под статус-баром: на светлой карте иначе не видно ни часов, ни кнопки. */
@Composable
private fun TopScrim(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(HereSize.Memory.scrimHeight)
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = SCRIM_ALPHA), Color.Transparent)
                )
            )
    )
}

/** Лист с рассказом: эмоция, заголовок, когда и где это было, текст и медиа. */
@Composable
private fun MemoryDetailSheet(
    memory: Memory,
    address: String?,
    onMediaClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors

    Column(
        verticalArrangement = Arrangement.spacedBy(HereSize.Memory.sheetSpacing),
        modifier = modifier
            .fillMaxWidth()
            .sheetShadow(HereShape.sheetDetail)
            .clip(HereShape.sheetDetail)
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(horizontal = HereSize.Memory.sheetHorizontalPadding)
            .padding(
                top = HereSize.Memory.sheetTopPadding,
                bottom = HereSize.Memory.sheetBottomPadding
            )
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HereSize.Memory.headerSpacing)) {
            EmotionTag(
                emoji = memory.emotion.emoji,
                label = stringResource(memory.emotion.labelResId),
                color = memory.emotion.color.soft
            )

            Text(
                text = memory.title,
                style = MaterialTheme.typography.headlineMedium,
                color = colors.textPrimary
            )

            Text(
                text = subtitle(happenedAt = memory.happenedAt, address = address),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
        }

        if (memory.description.isNotBlank()) {
            Text(
                text = memory.description,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HereSize.Memory.dividerThickness)
                .background(colors.outline)
        )

        MediaSection(media = memory.media, onMediaClick = onMediaClick)
    }
}

@Composable
private fun MediaSection(
    media: List<MemoryMedia>,
    onMediaClick: (Int) -> Unit
) {
    val colors = HereTheme.colors

    if (media.isEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(HereSize.Memory.mediaTextSpacing)) {
            Text(
                text = stringResource(R.string.memory_no_media_title),
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary
            )

            Text(
                text = stringResource(R.string.memory_no_media_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }
        return
    }

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

/** Когда и где это было: дата, время и адрес одной строкой. */
private fun subtitle(happenedAt: Long, address: String?): String {
    val moment = Instant.ofEpochMilli(happenedAt).atZone(ZoneId.systemDefault())
    val dateTime = DateFormat.format(moment) + SEPARATOR + TimeFormat.format(moment)

    return if (address == null) dateTime else dateTime + SEPARATOR + address
}
