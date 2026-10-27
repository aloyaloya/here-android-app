package ru.aloyaloya.memory.presentation.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.aloyaloya.design_system.component.topbar.HereContextTopAppBar
import ru.aloyaloya.design_system.component.topbar.TopAppBarAction
import ru.aloyaloya.design_system.theme.HereMotion
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.domain.model.MediaType
import ru.aloyaloya.domain.model.MemoryMedia
import ru.aloyaloya.memory.R
import kotlin.math.abs
import ru.aloyaloya.design_system.R as DesignSystemR

private const val FADE_DISTANCE_RATIO = 4f
private const val MIN_BACKGROUND_ALPHA = 0.35f
private const val CONTROLS_HIDE_MILLIS = 3000L

private val ViewerBackground = Color.Black

/**
 * Полноэкранный просмотр медиа воспоминания поверх экрана: «назад» закрывает сначала его.
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

    var playingPage by remember { mutableStateOf<Int?>(null) }
    var controlsShown by remember { mutableStateOf(true) }
    var seeking by remember { mutableStateOf(false) }
    var muted by rememberSaveable { mutableStateOf(false) }
    val videoPlaying = playingPage == pagerState.currentPage
    val videoPage = media.getOrNull(pagerState.currentPage)?.type == MediaType.VIDEO
    val controlsVisible = controlsShown || !videoPlaying
    val accessibility = LocalAccessibilityManager.current

    LaunchedEffect(videoPlaying, controlsShown, seeking) {
        if (!videoPlaying) {
            controlsShown = true
        } else if (controlsShown && !seeking) {
            delay(
                accessibility?.calculateRecommendedTimeoutMillis(
                    originalTimeoutMillis = CONTROLS_HIDE_MILLIS,
                    containsIcons = true,
                    containsControls = true
                ) ?: CONTROLS_HIDE_MILLIS
            )
            controlsShown = false
        }
    }

    BackHandler(onBack = onDismissRequest)

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
                    muted = muted,
                    controlsVisible = controlsVisible,
                    onPlaybackChange = { started ->
                        playingPage = if (started) page else playingPage?.takeIf { it != page }
                    },
                    onSeekingChange = { seeking = it },
                    onTap = { controlsShown = !controlsShown },
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

        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(HereMotion.fadeThroughEnter()),
            exit = fadeOut(HereMotion.fadeThroughExit())
        ) {
            HereContextTopAppBar(
                title = "",
                navigationContentDescription = stringResource(R.string.memory_media_close),
                onNavigateBack = onDismissRequest,
                overMedia = true
            ) {
                if (videoPage) {
                    TopAppBarAction(
                        icon = if (muted) DesignSystemR.drawable.ic_volume_off else DesignSystemR.drawable.ic_volume_on,
                        contentDescription = stringResource(
                            if (muted) R.string.memory_video_unmute else R.string.memory_video_mute
                        ),
                        onClick = { muted = !muted },
                        tint = LocalContentColor.current
                    )
                }
            }
        }

        if (media.size > 1) {
            AnimatedVisibility(
                visible = controlsVisible,
                enter = fadeIn(HereMotion.fadeThroughEnter()),
                exit = fadeOut(HereMotion.fadeThroughExit()),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = HereSize.Memory.indicatorBottomPadding)
            ) {
                CoverIndicator(
                    pageCount = media.size,
                    currentPage = pagerState.currentPage
                )
            }
        }
    }
}

/** Чем дальше утянули снимок, тем прозрачнее фон: под ним проступает экран воспоминания. */
private fun backgroundAlpha(shift: Float, dismissDistance: Float): Float =
    (1f - abs(shift) / (dismissDistance * FADE_DISTANCE_RATIO))
        .coerceIn(MIN_BACKGROUND_ALPHA, 1f)
