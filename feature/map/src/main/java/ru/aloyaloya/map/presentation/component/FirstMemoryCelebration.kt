package ru.aloyaloya.map.presentation.component

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.delay
import ru.aloyaloya.design_system.component.celebration.Confetti
import ru.aloyaloya.design_system.component.character.BubbleTail
import ru.aloyaloya.design_system.component.character.CharacterBubble
import ru.aloyaloya.design_system.theme.HereMotion
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.map.R
import kotlin.math.roundToInt

private const val BUBBLE_MILLIS = 4000L

private val BottomOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 1f)

/**
 * Первое воспоминание на карте.
 *
 * @param emotion Эмоция воспоминания.
 * @param pin Рамка его пина на экране в пикселях.
 */
internal data class FirstMemory(
    val emotion: Emotion,
    val pin: Rect
)

/**
 * Праздник первого воспоминания: конфетти из пина и реплика персонажа над ним.
 *
 * @param firstMemory Первое воспоминание и его пин.
 * @param dismissed Убрать реплику раньше времени, например карту сдвинули.
 * @param onEnded Колбэк: конфетти осыпались, реплика скрылась.
 */
@Composable
internal fun FirstMemoryCelebration(
    firstMemory: FirstMemory,
    dismissed: Boolean,
    onEnded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors
    var confettiEnded by remember { mutableStateOf(false) }
    var timedOut by remember { mutableStateOf(false) }
    val bubbleState = remember { MutableTransitionState(false) }
    bubbleState.targetState = !dismissed && !timedOut

    LaunchedEffect(Unit) {
        delay(BUBBLE_MILLIS)
        timedOut = true
    }

    if (confettiEnded && bubbleState.isIdle && !bubbleState.currentState) {
        LaunchedEffect(Unit) { onEnded() }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (!confettiEnded) {
            Confetti(
                origin = firstMemory.pin.center,
                onEnded = { confettiEnded = true }
            )
        }

        AnimatedVisibility(
            visibleState = bubbleState,
            enter = scaleIn(
                animationSpec = HereMotion.bouncy(),
                transformOrigin = BottomOrigin
            ) + fadeIn(HereMotion.effects()),
            exit = scaleOut(
                animationSpec = tween(HereMotion.Duration.short),
                transformOrigin = BottomOrigin
            ) + fadeOut(tween(HereMotion.Duration.short)),
            modifier = Modifier.above(
                pin = firstMemory.pin,
                spacing = HereSpacing.xs
            )
        ) {
            CharacterBubble(
                text = stringResource(firstMemory.emotion.firstMemoryLine),
                containerColor = colors.surface,
                contentColor = colors.textPrimary,
                tail = BubbleTail.BOTTOM,
                borderColor = colors.outline,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = HereSize.MapEmptyHint.bubbleMaxWidth)
            )
        }
    }
}

private fun Modifier.above(pin: Rect, spacing: Dp) = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
    layout(constraints.maxWidth, constraints.maxHeight) {
        placeable.place(
            x = (pin.center.x - placeable.width / 2f).roundToInt(),
            y = (pin.top - spacing.toPx() - placeable.height).roundToInt()
        )
    }
}

private val Emotion.firstMemoryLine: Int
    @StringRes get() = when (this) {
        Emotion.HAPPY -> R.string.first_memory_happy
        Emotion.TENDER -> R.string.first_memory_tender
        Emotion.SURPRISED -> R.string.first_memory_surprised
        Emotion.CALM -> R.string.first_memory_calm
        Emotion.SAD -> R.string.first_memory_sad
        Emotion.ANGRY -> R.string.first_memory_angry
    }
