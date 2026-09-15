package ru.aloyaloya.memory.presentation.component

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import ru.aloyaloya.design_system.component.button.HereIconButton
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.domain.model.MediaType
import ru.aloyaloya.domain.model.MemoryMedia
import ru.aloyaloya.memory.R
import kotlin.math.abs
import ru.aloyaloya.design_system.R as DesignSystemR

private const val FADE_DISTANCE_RATIO = 4f
private const val MIN_BACKGROUND_ALPHA = 0.35f

private val ViewerBackground = Color.Black
private val CounterBackground = Color.White.copy(alpha = 0.18f)

/**
 * Полноэкранный просмотр медиа воспоминания.
 *
 * @param media Медиа воспоминания.
 * @param initialIndex Файл, с которого открылся просмотр.
 * @param onDismissRequest Колбэк закрытия просмотра.
 * @param modifier [Modifier], применяемый к просмотру.
 */
@Composable
fun MediaViewer(
    media: List<MemoryMedia>,
    initialIndex: Int,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(initialPage = initialIndex) { media.size }
    val scope = rememberCoroutineScope()

    var zoomed by remember { mutableStateOf(false) }
    val shift = remember { Animatable(0f) }

    val dismissDistance = with(LocalDensity.current) {
        HereSize.MediaViewer.dismissDistance.toPx()
    }

    val dragState = rememberDraggableState { delta ->
        scope.launch { shift.snapTo(shift.value + delta) }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    ViewerBackground.copy(
                        alpha = backgroundAlpha(
                            shift.value,
                            dismissDistance
                        )
                    )
                )
                .draggable(
                    state = dragState,
                    orientation = Orientation.Vertical,
                    enabled = !zoomed,
                    onDragStopped = {
                        if (abs(shift.value) > dismissDistance) {
                            onDismissRequest()
                        } else {
                            shift.animateTo(0f)
                        }
                    }
                )
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = !zoomed,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { translationY = shift.value }
            ) { page ->
                val item = media[page]

                if (item.type == MediaType.VIDEO) {
                    VideoPage(
                        uri = item.uri,
                        playing = pagerState.currentPage == page,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    ZoomablePhoto(
                        uri = item.uri,
                        active = pagerState.currentPage == page,
                        onZoomChange = { zoomed = it },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            HereIconButton(
                icon = DesignSystemR.drawable.ic_close,
                contentDescription = stringResource(R.string.memory_media_close),
                onClick = onDismissRequest,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(HereSize.MediaViewer.actionsPadding)
            )

            if (media.size > 1) {
                Text(
                    text = stringResource(
                        R.string.memory_media_counter,
                        pagerState.currentPage + 1,
                        media.size
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(HereSize.MediaViewer.counterMargin)
                        .clip(HereShape.pill)
                        .background(CounterBackground)
                        .padding(
                            vertical = HereSize.MediaViewer.counterVerticalPadding,
                            horizontal = HereSize.MediaViewer.counterHorizontalPadding
                        )
                )
            }
        }
    }
}

/** Чем дальше утянули снимок, тем прозрачнее фон: под ним проступает экран воспоминания. */
private fun backgroundAlpha(shift: Float, dismissDistance: Float): Float =
    (1f - abs(shift) / (dismissDistance * FADE_DISTANCE_RATIO))
        .coerceIn(MIN_BACKGROUND_ALPHA, 1f)
