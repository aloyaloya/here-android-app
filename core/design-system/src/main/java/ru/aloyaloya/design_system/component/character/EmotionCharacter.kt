package ru.aloyaloya.design_system.component.character

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.delay
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.extension.rememberLoopsEnabled
import ru.aloyaloya.design_system.theme.HereSize
import kotlin.random.Random

/** Персонаж: три слоя одной эмоции или «ты». */
enum class CharacterEmotion(
    @param:DrawableRes val body: Int,
    @param:DrawableRes val eyes: Int,
    @param:DrawableRes val face: Int
) {
    HAPPY(R.drawable.character_body_happy, R.drawable.character_eyes_happy, R.drawable.character_face_happy),
    TENDER(R.drawable.character_body_tender, R.drawable.character_eyes_tender, R.drawable.character_face_tender),
    SURPRISED(R.drawable.character_body_surprised, R.drawable.character_eyes_surprised, R.drawable.character_face_surprised),
    CALM(R.drawable.character_body_calm, R.drawable.character_eyes_calm, R.drawable.character_face_calm),
    SAD(R.drawable.character_body_sad, R.drawable.character_eyes_sad, R.drawable.character_face_sad),
    ANGRY(R.drawable.character_body_angry, R.drawable.character_eyes_angry, R.drawable.character_face_angry),
    YOU(R.drawable.character_body_you, R.drawable.character_eyes_you, R.drawable.character_face_you)
}

/** Полный вдох и выдох. */
private const val BREATH_MILLIS = 2600

/** Моргание. */
private const val BLINK_MILLIS = 120

/** Пауза между морганиями: у каждого персонажа своя. */
private val BlinkPause = 3000L..5000L

/** Поворот взгляда. */
private const val LOOK_MILLIS = 300

/** Смена лица. */
private const val FACE_MILLIS = 150

/** Подъем на вдохе в долях высоты. */
private const val BREATH_LIFT = 0.04f

/** Растяжение по Y на вдохе. */
private const val BREATH_STRETCH = 0.02f

/** Сжатие глаз в середине моргания. */
private const val BLINK_SQUEEZE = 0.08f

/** Опора дыхания: низ персонажа. */
private val BreathOrigin = TransformOrigin(0.5f, 0.95f)

/** Центр глаз в слое. */
private val EyesOrigin = TransformOrigin(0.5f, 0.37f)

/**
 * Персонаж-эмоция из трех слоев: тело, глаза, лицо.
 *
 * @param emotion Эмоция персонажа, смена — через crossfade.
 * @param size Ширина персонажа, высота в [HereSize.EmotionCharacter.heightRatio] раз больше.
 * @param look Взгляд: −1 влево, 0 прямо, 1 вправо.
 * @param animated Дышит и моргает ли персонаж.
 */
@Composable
fun EmotionCharacter(
    emotion: CharacterEmotion,
    size: Dp,
    modifier: Modifier = Modifier,
    look: Float = 0f,
    animated: Boolean = true
) {
    val loops = animated && rememberLoopsEnabled()
    val breath = remember { Animatable(0f) }
    val blink = remember { Animatable(1f) }
    val lookShift by animateFloatAsState(
        targetValue = look,
        animationSpec = tween(LOOK_MILLIS),
        label = "look"
    )

    LaunchedEffect(loops) {
        if (!loops) {
            breath.snapTo(0f)
            return@LaunchedEffect
        }
        breath.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(BREATH_MILLIS / 2, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(loops) {
        blink.snapTo(1f)
        while (loops) {
            delay(Random.nextLong(BlinkPause.first, BlinkPause.last))
            blink.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = BLINK_MILLIS
                    BLINK_SQUEEZE at BLINK_MILLIS / 2
                }
            )
        }
    }

    Crossfade(
        targetState = emotion,
        animationSpec = tween(FACE_MILLIS),
        label = "face",
        modifier = modifier
            .size(width = size, height = size * HereSize.EmotionCharacter.heightRatio)
            .graphicsLayer {
                transformOrigin = BreathOrigin
                translationY = -BREATH_LIFT * this.size.height * breath.value
                scaleY = 1f + BREATH_STRETCH * breath.value
            }
    ) { shown ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(shown.body),
                contentDescription = null,
                modifier = Modifier.matchParentSize()
            )
            Image(
                painter = painterResource(shown.eyes),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        transformOrigin = EyesOrigin
                        translationX = lookShift * size.toPx() * HereSize.EmotionCharacter.lookShift
                        scaleY = blink.value
                    }
            )
            Image(
                painter = painterResource(shown.face),
                contentDescription = null,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}
