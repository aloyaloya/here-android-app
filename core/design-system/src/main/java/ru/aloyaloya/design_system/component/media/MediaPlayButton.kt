package ru.aloyaloya.design_system.component.media

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.theme.HereMotion
import ru.aloyaloya.design_system.theme.HereSize

private const val PRESSED_SCALE = 0.9f

/**
 * Кнопка воспроизведения ролика в виде большого [MediaPlayBadge]: значок сменяется с play на паузу растворением.
 *
 * @param playing Ролик играет: на кнопке пауза.
 * @param onClick Колбэк нажатия.
 * @param modifier [Modifier], применяемый к кнопке.
 */
@Composable
fun MediaPlayButton(
    playing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) PRESSED_SCALE else 1f,
        animationSpec = if (pressed) snap() else HereMotion.bouncy(),
        label = "media-play-scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .size(HereSize.MediaBadge.largeSize)
            .background(color = BadgeBackground, shape = CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        AnimatedContent(
            targetState = playing,
            transitionSpec = {
                fadeIn(HereMotion.fadeThroughEnter()) togetherWith
                    fadeOut(HereMotion.fadeThroughExit())
            },
            label = "media-play-icon"
        ) { isPlaying ->
            Icon(
                painter = painterResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                contentDescription = stringResource(
                    if (isPlaying) R.string.media_pause else R.string.media_play
                ),
                tint = Color.White,
                modifier = Modifier.size(HereSize.MediaBadge.largeIconSize)
            )
        }
    }
}
