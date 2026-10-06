package ru.aloyaloya.onboarding.presentation.illustration

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.delay
import ru.aloyaloya.design_system.extension.rememberMotionEnabled
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing

/**
 * Появление элемента иллюстрации: пружина перелетает 1, отсюда отскок.
 *
 * @property spec Ход появления от 0 до 1.
 * @property origin Опора масштаба.
 * @property startScale Масштаб в начале.
 * @property startShift Сдвиг по вертикали в начале.
 */
enum class Entrance(
    val spec: AnimationSpec<Float>,
    val origin: TransformOrigin,
    val startScale: Float,
    val startShift: Dp
) {
    DROP(
        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        TransformOrigin(0.5f, 1f),
        1f,
        -HereSize.EmotionCharacter.small
    ),
    POP(spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow), TransformOrigin(0.5f, 0.6f), 0.4f, 0.dp),
    FADE(tween(360, easing = EaseOut), TransformOrigin.Center, 1f, HereSpacing.s),
    BUBBLE(spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium), TransformOrigin.Center, 0.85f, 0.dp)
}

/**
 * Появление, когда страница стала текущей; на ней же не повторяется.
 *
 * @param active Текущая ли страница.
 * @param kind Вид появления.
 * @param delayMillis Задержка от показа страницы.
 * @param origin Опора масштаба, у реплики — хвостик.
 */
@Composable
fun Modifier.entrance(
    active: Boolean,
    kind: Entrance,
    delayMillis: Int = 0,
    origin: TransformOrigin = kind.origin
): Modifier {
    val motion = rememberMotionEnabled()
    val progress = remember { Animatable(0f) }

    LaunchedEffect(active, motion) {
        if (!active || progress.value >= 1f) return@LaunchedEffect
        if (!motion) {
            progress.snapTo(1f)
            return@LaunchedEffect
        }
        delay(delayMillis.toLong())
        progress.animateTo(targetValue = 1f, animationSpec = kind.spec)
    }

    return graphicsLayer {
        val value = progress.value
        val scale = lerp(kind.startScale, 1f, value)
        transformOrigin = origin
        alpha = value.coerceIn(0f, 1f)
        scaleX = scale
        scaleY = scale
        translationY = kind.startShift.toPx() * (1f - value)
    }
}
