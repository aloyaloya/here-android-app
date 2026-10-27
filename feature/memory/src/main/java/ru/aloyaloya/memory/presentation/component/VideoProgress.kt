package ru.aloyaloya.memory.presentation.component

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.common.Player
import kotlinx.coroutines.delay
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.memory.R

private const val PROGRESS_TICK_MILLIS = 100L
private const val INACTIVE_TRACK_ALPHA = 0.35f
private const val TABULAR_FIGURES = "tnum"

/**
 * Полоса прогресса ролика со временем: перетаскивание перематывает.
 *
 * @param player Плеер ролика.
 * @param onSeekingChange Колбэк начала и конца перемотки.
 * @param modifier [Modifier], применяемый к полосе.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VideoProgress(
    player: Player,
    onSeekingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var position by remember(player) { mutableLongStateOf(0L) }
    var duration by remember(player) { mutableLongStateOf(0L) }
    var seekFraction by remember(player) { mutableStateOf<Float?>(null) }
    val currentOnSeekingChange by rememberUpdatedState(onSeekingChange)

    LaunchedEffect(player) {
        while (true) {
            position = player.currentPosition
            duration = player.duration.takeIf { it != C.TIME_UNSET } ?: 0L
            delay(PROGRESS_TICK_MILLIS)
        }
    }

    val fraction = seekFraction ?: if (duration > 0) position.toFloat() / duration else 0f
    val colors = SliderDefaults.colors(
        thumbColor = Color.White,
        activeTrackColor = Color.White,
        inactiveTrackColor = Color.White.copy(alpha = INACTIVE_TRACK_ALPHA)
    )
    val description = stringResource(R.string.memory_video_position)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HereSpacing.m),
        modifier = modifier
    ) {
        VideoTime(millis = (fraction * duration).toLong())

        Slider(
            value = fraction.coerceIn(0f, 1f),
            onValueChange = { value ->
                if (seekFraction == null) currentOnSeekingChange(true)
                seekFraction = value
            },
            onValueChangeFinished = {
                seekFraction?.let { player.seekTo((it * duration).toLong()) }
                position = player.currentPosition
                seekFraction = null
                currentOnSeekingChange(false)
            },
            enabled = duration > 0,
            colors = colors,
            thumb = {
                Box(
                    modifier = Modifier
                        .size(HereSize.MediaViewer.progressThumbSize)
                        .background(Color.White, HereShape.pill)
                )
            },
            track = { state ->
                SliderDefaults.Track(
                    sliderState = state,
                    colors = colors,
                    drawStopIndicator = null,
                    thumbTrackGapSize = 0.dp,
                    modifier = Modifier.height(HereSize.MediaViewer.progressTrackHeight)
                )
            },
            modifier = Modifier
                .weight(1f)
                .semantics { contentDescription = description }
        )

        VideoTime(millis = duration)
    }
}

/** Время ролика в формате «0:12». */
@Composable
private fun VideoTime(millis: Long) {
    Text(
        text = DateUtils.formatElapsedTime(millis / 1000),
        style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = TABULAR_FIGURES),
        color = Color.White
    )
}
