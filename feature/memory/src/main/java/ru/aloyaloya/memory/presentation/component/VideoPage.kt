package ru.aloyaloya.memory.presentation.component

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import androidx.media3.ui.compose.state.rememberPresentationState
import ru.aloyaloya.design_system.component.media.MediaPhoto
import ru.aloyaloya.design_system.component.media.MediaPlayButton
import ru.aloyaloya.design_system.theme.HereMotion
import ru.aloyaloya.memory.R
import java.io.File
import ru.aloyaloya.design_system.R as DesignSystemR

/**
 * Страница просмотра с видео.
 *
 * Плеер живет ровно столько, сколько страница: свой на каждый ролик, с
 * освобождением в [DisposableEffect] — иначе останется висеть декодер и звук.
 * Видео выводится в TextureView, чтобы на него действовала прозрачность просмотра.
 *
 * @param uri Путь к файлу во внутреннем хранилище.
 * @param playing Страница открыта: с уходом на соседнюю ролик встает на паузу.
 * @param controlsVisible Кнопка воспроизведения видна.
 * @param onPlaybackChange Колбэк запуска и остановки ролика.
 * @param onTap Колбэк тапа по играющему ролику, на паузе тап запускает его.
 * @param modifier [Modifier], применяемый к странице.
 */
@OptIn(UnstableApi::class)
@Composable
fun VideoPage(
    uri: String,
    playing: Boolean,
    controlsVisible: Boolean,
    onPlaybackChange: (Boolean) -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val player = remember(uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri.toMediaUri()))
            prepare()
        }
    }

    DisposableEffect(player) {
        onDispose { player.release() }
    }

    LaunchedEffect(playing) {
        if (!playing) player.pause()
    }

    LifecycleResumeEffect(player) {
        onPauseOrDispose { player.pause() }
    }

    val presentation = rememberPresentationState(player)
    val playPause = rememberPlayPauseButtonState(player)
    val videoPlaying = !playPause.showPlay
    val currentOnPlaybackChange by rememberUpdatedState(onPlaybackChange)

    LaunchedEffect(videoPlaying) { currentOnPlaybackChange(videoPlaying) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.clickable(
            interactionSource = null,
            indication = null,
            enabled = playPause.isEnabled,
            onClickLabel = stringResource(
                when {
                    !videoPlaying -> DesignSystemR.string.media_play
                    controlsVisible -> R.string.memory_video_hide_controls
                    else -> R.string.memory_video_show_controls
                }
            ),
            onClick = if (videoPlaying) onTap else playPause::onClick
        )
    ) {
        Box(
            modifier = Modifier.resizeWithContentScale(
                contentScale = ContentScale.Fit,
                sourceSizeDp = presentation.videoSizeDp
            )
        ) {
            PlayerSurface(
                player = player,
                surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
                modifier = Modifier.fillMaxSize()
            )

            if (presentation.coverSurface) {
                MediaPhoto(
                    uri = uri,
                    video = true,
                    shape = RectangleShape,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(HereMotion.fadeThroughEnter()),
            exit = fadeOut(HereMotion.fadeThroughExit())
        ) {
            MediaPlayButton(
                playing = videoPlaying,
                onClick = playPause::onClick
            )
        }
    }
}

/** В базе лежит путь к файлу, а плееру нужен полноценный адрес со схемой. */
private fun String.toMediaUri(): Uri {
    val uri = Uri.parse(this)
    return if (uri.scheme == null) Uri.fromFile(File(this)) else uri
}
