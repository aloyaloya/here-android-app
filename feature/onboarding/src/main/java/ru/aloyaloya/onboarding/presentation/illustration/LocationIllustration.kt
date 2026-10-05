package ru.aloyaloya.onboarding.presentation.illustration

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import ru.aloyaloya.design_system.component.character.BubbleTail
import ru.aloyaloya.design_system.component.character.CharacterBubble
import ru.aloyaloya.design_system.component.character.CharacterEmotion
import ru.aloyaloya.design_system.component.character.EmotionCharacter
import ru.aloyaloya.design_system.extension.overlayShadow
import ru.aloyaloya.design_system.extension.rememberLoopsEnabled
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.onboarding.R
import ru.aloyaloya.onboarding.presentation.LocationStep

/** Персонажи вокруг «ты». */
private val RequestCharacters = listOf(
    CharacterSpot(CharacterEmotion.HAPPY, 22.dp, 70.dp, 60.dp, LOOK_RIGHT),
    CharacterSpot(CharacterEmotion.TENDER, 252.dp, 60.dp, 58.dp, LOOK_LEFT),
    CharacterSpot(CharacterEmotion.CALM, 30.dp, 290.dp, 56.dp, LOOK_RIGHT),
    CharacterSpot(CharacterEmotion.SURPRISED, 262.dp, 280.dp, 54.dp, LOOK_LEFT)
)

/** Персонажи вокруг прицела после отказа. */
private val DeniedCharacters = listOf(
    CharacterSpot(CharacterEmotion.SURPRISED, 20.dp, 52.dp, 58.dp, LOOK_RIGHT),
    CharacterSpot(CharacterEmotion.CALM, 282.dp, 60.dp, 54.dp, LOOK_LEFT),
    CharacterSpot(CharacterEmotion.TENDER, 34.dp, 290.dp, 54.dp, LOOK_RIGHT),
    CharacterSpot(CharacterEmotion.HAPPY, 264.dp, 286.dp, 54.dp, LOOK_LEFT)
)

/** Персонажи вокруг прицела, когда геолокация выключена в настройках. */
private val BlockedCharacters = listOf(
    CharacterSpot(CharacterEmotion.SAD, 20.dp, 36.dp, 58.dp, LOOK_RIGHT),
    CharacterSpot(CharacterEmotion.CALM, 282.dp, 40.dp, 54.dp, LOOK_LEFT),
    CharacterSpot(CharacterEmotion.SURPRISED, 40.dp, 236.dp, 50.dp, LOOK_RIGHT),
    CharacterSpot(CharacterEmotion.TENDER, 262.dp, 236.dp, 50.dp, LOOK_LEFT)
)

/** Высота кадра макета до запроса и после отказа. */
private val FrameHeight = 448.dp

/** Высота кадра макета, когда геолокация выключена в настройках: панель там выше. */
private val BlockedFrameHeight = 368.dp

/** Ореол в начале волны. */
private const val HALO_START_SCALE = 0.6f

/** Ореол в конце волны. */
private const val HALO_END_SCALE = 2f

/** Прозрачность ореола в начале волны. */
private const val HALO_START_ALPHA = 0.55f

/** Верх «ты» от верха экрана. */
private val YouTop = 172.dp

/** Центр прицела от верха экрана. */
private val PickerCenter = 210.dp

/** Центр прицела на шаге [LocationStep.BLOCKED]: панель там выше. */
private val BlockedPickerCenter = 190.dp

/** Появление «ты». */
private const val YOU_POP_MILLIS = 200

/** Появление реплики «Это ты». */
private const val YOU_BUBBLE_MILLIS = 700

/** Появление подсказки над прицелом. */
private const val PICK_HINT_MILLIS = 500

/** Волна ореола. */
private const val HALO_MILLIS = 1800

/** Сдвиг второй волны ореола. */
private const val HALO_SHIFT_MILLIS = HALO_MILLIS / 2

/** Переход прицела на новую высоту. */
private const val PICKER_MOVE_MILLIS = 300

/**
 * Карта с персонажами: «ты» до отказа, прицел ручного выбора после.
 *
 * @param step Шаг страницы геолокации.
 * @param active Текущая ли страница.
 */
@Composable
fun LocationIllustration(
    step: LocationStep,
    active: Boolean,
    modifier: Modifier = Modifier
) {
    val characters = when (step) {
        LocationStep.REQUEST -> RequestCharacters
        LocationStep.DENIED -> DeniedCharacters
        LocationStep.BLOCKED -> BlockedCharacters
    }

    IllustrationScene(
        frameHeight = if (step == LocationStep.BLOCKED) BlockedFrameHeight else FrameHeight,
        modifier = modifier,
        backdrop = { MapBackground() }
    ) {
        if (step == LocationStep.REQUEST) {
            You(active = active)
        } else {
            val center by animateDpAsState(
                targetValue = if (step == LocationStep.BLOCKED) BlockedPickerCenter else PickerCenter,
                animationSpec = tween(PICKER_MOVE_MILLIS),
                label = "picker"
            )
            Picker(frameCenter = center, active = active)
        }

        characters.forEachIndexed { index, spot ->
            CharacterAt(
                spot = spot,
                active = active,
                delayMillis = index * DROP_STEP_MILLIS
            )
        }
    }
}

/**
 * «Ты» с пульсирующим ореолом и репликой под ним по центру сцены.
 *
 * @param active Текущая ли страница.
 */
@Composable
private fun SceneScope.You(active: Boolean) {
    val colors = HereTheme.colors
    val sizes = HereSize.LocationIllustration
    val youHeight = sizes.youSize * HereSize.EmotionCharacter.heightRatio
    val top = YouTop + shiftY(YouTop + youHeight / 2)
    val haloModifier = Modifier
        .align(Alignment.TopCenter)
        .offset(y = top + youHeight / 2 - sizes.youHaloSize / 2)
        .size(sizes.youHaloSize)

    if (rememberLoopsEnabled()) {
        HaloWave(shiftMillis = 0, modifier = haloModifier)
        HaloWave(shiftMillis = HALO_SHIFT_MILLIS, modifier = haloModifier)
    } else {
        Halo(modifier = haloModifier)
    }

    EmotionCharacter(
        emotion = CharacterEmotion.YOU,
        size = sizes.youSize,
        modifier = Modifier
            .align(Alignment.TopCenter)
            .offset(y = top)
            .entrance(active = active, kind = Entrance.POP, delayMillis = YOU_POP_MILLIS)
    )

    CharacterBubble(
        text = stringResource(R.string.onboarding_location_me),
        containerColor = colors.accent,
        contentColor = colors.onAccent,
        tail = BubbleTail.TOP,
        modifier = Modifier
            .align(Alignment.TopCenter)
            .offset(y = top + youHeight)
            .entrance(
                active = active,
                kind = Entrance.BUBBLE,
                delayMillis = YOU_BUBBLE_MILLIS,
                origin = TransformOrigin(0.5f, 0f)
            )
    )
}

/**
 * Волна ореола: расходится и гаснет по кругу.
 *
 * @param shiftMillis Сдвиг волны от начала цикла.
 */
@Composable
private fun HaloWave(
    shiftMillis: Int,
    modifier: Modifier = Modifier
) {
    val wave = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        wave.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(HALO_MILLIS, easing = EaseOut),
                initialStartOffset = StartOffset(shiftMillis, StartOffsetType.FastForward)
            )
        )
    }

    Halo(
        modifier = modifier.graphicsLayer {
            val scale = lerp(HALO_START_SCALE, HALO_END_SCALE, wave.value)
            scaleX = scale
            scaleY = scale
            alpha = HALO_START_ALPHA * (1f - wave.value)
        }
    )
}

/** Круг ореола. */
@Composable
private fun Halo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            color = HereTheme.colors.accent.copy(alpha = HereSize.LocationIllustration.youHaloAlpha),
            shape = CircleShape
        )
    )
}

/**
 * Прицел ручного выбора места с подсказкой над ним.
 *
 * @param frameCenter Центр головки прицела от верха кадра макета.
 * @param active Текущая ли страница.
 */
@Composable
private fun SceneScope.Picker(frameCenter: Dp, active: Boolean) {
    val center = frameCenter + shiftY(frameCenter)
    val colors = HereTheme.colors
    val sizes = HereSize.LocationIllustration
    val headTop = center - sizes.pickerHeadSize
    val shadowAlpha = if (colors.isDark) sizes.darkPickerShadowAlpha else sizes.pickerShadowAlpha

    Box(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .offset(y = center + sizes.pickerStemHeight - sizes.pickerShadowHeight)
            .size(sizes.pickerShadowWidth, sizes.pickerShadowHeight)
            .background(color = Color.Black.copy(alpha = shadowAlpha), shape = CircleShape)
    )

    Box(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .offset(y = center - sizes.pickerBorder)
            .size(sizes.pickerStemWidth, sizes.pickerStemHeight)
            .background(color = colors.textPrimary, shape = CircleShape)
    )

    Box(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .offset(y = headTop)
            .entrance(active = active, kind = Entrance.DROP)
            .size(sizes.pickerHeadSize)
            .overlayShadow(CircleShape)
            .background(color = colors.textPrimary, shape = CircleShape)
            .border(width = sizes.pickerBorder, color = colors.surface, shape = CircleShape)
    )

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .align(Alignment.TopCenter)
            .height(headTop - HereSpacing.s)
    ) {
        CharacterBubble(
            text = stringResource(R.string.onboarding_pick_hint),
            containerColor = colors.surface,
            contentColor = colors.textPrimary,
            tail = BubbleTail.BOTTOM,
            modifier = Modifier.entrance(
                active = active,
                kind = Entrance.BUBBLE,
                delayMillis = PICK_HINT_MILLIS,
                origin = TransformOrigin(0.5f, 1f)
            )
        )
    }
}
