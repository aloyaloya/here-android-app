package ru.aloyaloya.memory.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.aloyaloya.design_system.component.button.HereIconButton
import ru.aloyaloya.design_system.component.media.MediaPhoto
import ru.aloyaloya.design_system.component.media.MediaPlayBadge
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.domain.model.MediaType
import ru.aloyaloya.domain.model.MemoryMedia
import ru.aloyaloya.memory.R
import ru.aloyaloya.design_system.R as DesignSystemR

private val ViewerBackground = Color.Black
private val CounterBackground = Color.White.copy(alpha = 0.18f)

/**
 * Полноэкранный просмотр снимков воспоминания.
 *
 * @param media Снимки воспоминания.
 * @param initialIndex Снимок, с которого открылся просмотр.
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
                .background(ViewerBackground)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(contentAlignment = Alignment.Center) {
                    MediaPhoto(
                        uri = media[page].uri,
                        shape = RectangleShape,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (media[page].type == MediaType.VIDEO) {
                        MediaPlayBadge(
                            size = HereSize.MediaBadge.largeSize,
                            iconSize = HereSize.MediaBadge.largeIconSize
                        )
                    }
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
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = HereSize.MediaViewer.counterBottomPadding)
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
