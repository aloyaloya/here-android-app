package ru.aloyaloya.memory.presentation.component

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.io.File

/**
 * Страница просмотра с видео.
 *
 * Плеер живет ровно столько, сколько страница: свой на каждый ролик, с
 * освобождением в [DisposableEffect] — иначе останется висеть декодер и звук.
 *
 * @param uri Путь к файлу во внутреннем хранилище.
 * @param playing Страница открыта: с уходом на соседнюю ролик встает на паузу.
 * @param modifier [Modifier], применяемый к плееру.
 */
@OptIn(UnstableApi::class)
@Composable
fun VideoPage(
    uri: String,
    playing: Boolean,
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

    AndroidView(
        factory = { PlayerView(it).apply { this.player = player } },
        modifier = modifier
    )
}

/** В базе лежит путь к файлу, а плееру нужен полноценный адрес со схемой. */
private fun String.toMediaUri(): Uri {
    val uri = Uri.parse(this)
    return if (uri.scheme == null) Uri.fromFile(File(this)) else uri
}
