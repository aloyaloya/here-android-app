package ru.aloyaloya.onboarding.presentation.illustration

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.delay
import ru.aloyaloya.design_system.extension.rememberMotionEnabled
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing

/** Плавное торможение появлений. */
private val EntranceEasing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)

/** Высота, с которой падает персонаж. */
private val DropHeight = HereSize.EmotionCharacter.small

/** Подъем проявляющегося элемента. */
private val FadeRise = HereSpacing.s

/**
 * Появление элемента иллюстрации.
 *
 * @property spec Ход появления от 0 до 1.
 * @property origin Опора масштаба.
 */
enum class Entrance(val spec: AnimationSpec<Float>, val origin: TransformOrigin) {
    DROP(tween(680, easing = LinearEasing), TransformOrigin(0.5f, 1f)),
    POP(tween(520, easing = LinearEasing), TransformOrigin(0.5f, 0.6f)),
    FADE(tween(360, easing = EaseOut), TransformOrigin.Center),
    BUBBLE(spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium), TransformOrigin.Center)
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
        transformOrigin = origin
        when (kind) {
            Entrance.DROP -> drop(progress.value)
            Entrance.POP -> pop(progress.value)
            Entrance.FADE -> fade(progress.value)
            Entrance.BUBBLE -> bubble(progress.value)
        }
    }
}

/** Падение с приземлением: прыжок вниз, сжатие и выпрямление. */
private fun GraphicsLayerScope.drop(progress: Float) {
    val drop = DropHeight.toPx()
    when {
        progress < 0.55f -> {
            val t = segment(progress, 0f, 0.55f)
            alpha = t
            translationY = lerp(-drop, drop / 12, t)
            scaleX = lerp(0.8f, 1.04f, t)
            scaleY = lerp(0.8f, 0.94f, t)
        }

        progress < 0.75f -> {
            val t = segment(progress, 0.55f, 0.75f)
            translationY = lerp(drop / 12, -drop / 30, t)
            scaleX = lerp(1.04f, 0.98f, t)
            scaleY = lerp(0.94f, 1.02f, t)
        }

        else -> {
            val t = segment(progress, 0.75f, 1f)
            translationY = lerp(-drop / 30, 0f, t)
            scaleX = lerp(0.98f, 1f, t)
            scaleY = lerp(1.02f, 1f, t)
        }
    }
}

/** Вырастание с перелетом. */
private fun GraphicsLayerScope.pop(progress: Float) {
    val scale = if (progress < 0.6f) {
        val t = segment(progress, 0f, 0.6f)
        alpha = t
        lerp(0.4f, 1.08f, t)
    } else {
        lerp(1.08f, 1f, segment(progress, 0.6f, 1f))
    }
    scaleX = scale
    scaleY = scale
}

/** Проявление с небольшим подъемом. */
private fun GraphicsLayerScope.fade(progress: Float) {
    alpha = progress
    translationY = FadeRise.toPx() * (1f - progress)
}

/** Реплика вырастает от хвостика. */
private fun GraphicsLayerScope.bubble(progress: Float) {
    alpha = progress.coerceIn(0f, 1f)
    val scale = lerp(0.85f, 1f, progress)
    scaleX = scale
    scaleY = scale
}

/** Доля [progress] внутри отрезка с торможением. */
private fun segment(progress: Float, start: Float, end: Float): Float =
    EntranceEasing.transform(((progress - start) / (end - start)).coerceIn(0f, 1f))
