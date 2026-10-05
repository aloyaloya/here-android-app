package ru.aloyaloya.onboarding.presentation.illustration

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
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
private val RequestCharacters = corners(
    CharacterEmotion.HAPPY,
    CharacterEmotion.TENDER,
    CharacterEmotion.CALM,
    CharacterEmotion.SURPRISED
)

/** Персонажи вокруг прицела после отказа. */
private val DeniedCharacters = corners(
    CharacterEmotion.SURPRISED,
    CharacterEmotion.CALM,
    CharacterEmotion.TENDER,
    CharacterEmotion.HAPPY
)

/** Персонажи вокруг прицела, когда геолокация выключена в настройках. */
private val BlockedCharacters = corners(
    CharacterEmotion.SAD,
    CharacterEmotion.CALM,
    CharacterEmotion.SURPRISED,
    CharacterEmotion.TENDER
)

/** Персонажи по углам сцены: центр остается для «ты» и прицела. */
private fun corners(
    topStart: CharacterEmotion,
    topEnd: CharacterEmotion,
    bottomStart: CharacterEmotion,
    bottomEnd: CharacterEmotion
): List<CharacterSpot> = listOf(
    CharacterSpot(topStart, 0.15f, 0.2f, HereSize.EmotionCharacter.medium, LOOK_RIGHT),
    CharacterSpot(topEnd, 0.84f, 0.2f, HereSize.EmotionCharacter.small, LOOK_LEFT),
    CharacterSpot(bottomStart, 0.16f, 0.72f, HereSize.EmotionCharacter.small, LOOK_RIGHT),
    CharacterSpot(bottomEnd, 0.82f, 0.71f, HereSize.EmotionCharacter.small, LOOK_LEFT)
)

/** Ореол в начале волны. */
private const val HALO_START_SCALE = 0.6f

/** Ореол в конце волны. */
private const val HALO_END_SCALE = 2f

/** Прозрачность ореола в начале волны. */
private const val HALO_START_ALPHA = 0.55f

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
        modifier = modifier,
        backdrop = { MapBackground() }
    ) {
        if (step == LocationStep.REQUEST) {
            You(active = active)
        } else {
            Picker(active = active)
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
    val you = CharacterSpot(CharacterEmotion.YOU, 0.5f, 0.5f, HereSize.EmotionCharacter.large)
    val haloModifier = Modifier
        .pinTo(x = you.centerX, y = you.centerY)
        .size(HereSize.LocationIllustration.youHaloSize)

    if (rememberLoopsEnabled()) {
        HaloWave(shiftMillis = 0, modifier = haloModifier)
        HaloWave(shiftMillis = HALO_SHIFT_MILLIS, modifier = haloModifier)
    } else {
        Halo(modifier = haloModifier)
    }

    EmotionCharacter(
        emotion = you.emotion,
        size = you.size,
        modifier = Modifier
            .pinTo(x = you.centerX, y = you.centerY)
            .entrance(active = active, kind = Entrance.POP, delayMillis = YOU_POP_MILLIS)
    )

    val bubblePivot = TransformOrigin(0.5f, 0f)
    CharacterBubble(
        text = stringResource(R.string.onboarding_location_me),
        containerColor = colors.accent,
        contentColor = colors.onAccent,
        tail = BubbleTail.TOP,
        modifier = Modifier
            .pinTo(x = you.centerX, y = you.bottom, pivot = bubblePivot)
            .entrance(active = active, kind = Entrance.BUBBLE, delayMillis = YOU_BUBBLE_MILLIS, origin = bubblePivot)
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
 * Прицел ручного выбора места по центру сцены с подсказкой над ним.
 *
 * @param active Текущая ли страница.
 */
@Composable
private fun SceneScope.Picker(active: Boolean) {
    val colors = HereTheme.colors
    val sizes = HereSize.LocationIllustration
    val x = width / 2
    val tip = height / 2
    val shadowAlpha = if (colors.isDark) sizes.darkPickerShadowAlpha else sizes.pickerShadowAlpha
    val bottomCenter = TransformOrigin(0.5f, 1f)

    Box(
        modifier = Modifier
            .pinTo(x = x, y = tip + sizes.pickerStemHeight, pivot = bottomCenter)
            .size(sizes.pickerShadowWidth, sizes.pickerShadowHeight)
            .background(color = Color.Black.copy(alpha = shadowAlpha), shape = CircleShape)
    )

    Box(
        modifier = Modifier
            .pinTo(x = x, y = tip - sizes.pickerBorder, pivot = TransformOrigin(0.5f, 0f))
            .size(sizes.pickerStemWidth, sizes.pickerStemHeight)
            .background(color = colors.textPrimary, shape = CircleShape)
    )

    Box(
        modifier = Modifier
            .pinTo(x = x, y = tip, pivot = bottomCenter)
            .entrance(active = active, kind = Entrance.DROP)
            .size(sizes.pickerHeadSize)
            .overlayShadow(CircleShape)
            .background(color = colors.textPrimary, shape = CircleShape)
            .border(width = sizes.pickerBorder, color = colors.surface, shape = CircleShape)
    )

    CharacterBubble(
        text = stringResource(R.string.onboarding_pick_hint),
        containerColor = colors.surface,
        contentColor = colors.textPrimary,
        tail = BubbleTail.BOTTOM,
        modifier = Modifier
            .pinTo(x = x, y = tip - sizes.pickerHeadSize - HereSpacing.s, pivot = bottomCenter)
            .entrance(active = active, kind = Entrance.BUBBLE, delayMillis = PICK_HINT_MILLIS, origin = bottomCenter)
    )
}
