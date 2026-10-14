package ru.aloyaloya.memory.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import ru.aloyaloya.design_system.component.emotion.EmotionTag
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.model.PlaceAddress
import ru.aloyaloya.memory.model.MemorySheet
import ru.aloyaloya.memory.model.MemoryUiState
import ru.aloyaloya.memory.presentation.component.DeleteMemoryDialog
import ru.aloyaloya.memory.presentation.component.MediaViewer
import ru.aloyaloya.memory.presentation.component.MemoryAddPhoto
import ru.aloyaloya.memory.presentation.component.MemoryCover
import ru.aloyaloya.memory.presentation.component.MemoryMediaGrid
import ru.aloyaloya.memory.presentation.component.MemoryNearby
import ru.aloyaloya.memory.presentation.component.MemoryTopBar
import ru.aloyaloya.memory.presentation.component.MemoryWhenWhere
import ru.aloyaloya.memory.presentation.component.coverHeight
import ru.aloyaloya.ui.emotion.character
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.labelResId
import ru.aloyaloya.ui.theme.LocalAppDarkTheme

/**
 * Экран воспоминания.
 *
 * @param uiState Состояние экрана.
 * @param onBackClick Колбэк возврата назад.
 * @param onEditClick Колбэк кнопки «Изменить» в шапке.
 * @param onDeleteClick Колбэк кнопки «Удалить» в шапке.
 * @param onMediaClick Колбэк открытия снимка на весь экран.
 * @param onNearbyClick Колбэк нажатия на воспоминание рядом, отдает его идентификатор.
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
    onNearbyClick: (Long) -> Unit,
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
                nearby = uiState.nearby,
                onBackClick = onBackClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                onMediaClick = onMediaClick,
                onNearbyClick = onNearbyClick,
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
    address: PlaceAddress?,
    nearby: List<Memory>,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMediaClick: (Int) -> Unit,
    onNearbyClick: (Long) -> Unit,
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

            item(key = "when-where") {
                MemoryWhenWhere(
                    memory = memory,
                    address = address,
                    modifier = Modifier.sectionPadding()
                )
            }

            item(key = "media") {
                if (photos) {
                    MemoryMediaGrid(
                        media = memory.media,
                        onMediaClick = onMediaClick,
                        modifier = Modifier.sectionPadding()
                    )
                } else {
                    MemoryAddPhoto(
                        onClick = onEditClick,
                        modifier = Modifier.sectionPadding()
                    )
                }
            }

            if (nearby.isNotEmpty()) {
                item(key = "nearby") {
                    MemoryNearby(
                        memories = nearby,
                        onMemoryClick = onNearbyClick,
                        modifier = Modifier.sectionPadding()
                    )
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

/** Отступы секции под рассказом. */
private fun Modifier.sectionPadding(): Modifier =
    padding(horizontal = HereSpacing.l)
        .padding(top = HereSize.Memory.sectionSpacing)
