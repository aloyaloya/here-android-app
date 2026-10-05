package ru.aloyaloya.onboarding.presentation.illustration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.aloyaloya.design_system.component.character.BubbleTail
import ru.aloyaloya.design_system.component.character.CharacterBubble
import ru.aloyaloya.design_system.component.character.CharacterEmotion
import ru.aloyaloya.design_system.component.character.EmotionCharacter
import ru.aloyaloya.design_system.extension.overlayShadow
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

/** Верх «ты» от верха экрана. */
private val YouTop = 172.dp

/** Центр прицела от верха экрана. */
private val PickerCenter = 210.dp

/** Центр прицела на шаге [LocationStep.BLOCKED]: панель там выше. */
private val BlockedPickerCenter = 190.dp

/**
 * Карта с персонажами: «ты» до отказа, прицел ручного выбора после.
 *
 * @param step Шаг страницы геолокации.
 */
@Composable
fun LocationIllustration(
    step: LocationStep,
    modifier: Modifier = Modifier
) {
    val characters = when (step) {
        LocationStep.REQUEST -> RequestCharacters
        LocationStep.DENIED -> DeniedCharacters
        LocationStep.BLOCKED -> BlockedCharacters
    }

    // TODO: сделать анимацию пульса ореола, появления «ты» и падения прицела
    IllustrationScene(
        frameHeight = if (step == LocationStep.BLOCKED) BlockedFrameHeight else FrameHeight,
        modifier = modifier,
        backdrop = { MapBackground() }
    ) {
        when (step) {
            LocationStep.REQUEST -> You()
            LocationStep.DENIED -> Picker(frameCenter = PickerCenter)
            LocationStep.BLOCKED -> Picker(frameCenter = BlockedPickerCenter)
        }

        characters.forEach { CharacterAt(it) }
    }
}

/** «Ты» с ореолом и репликой под ним по центру сцены. */
@Composable
private fun SceneScope.You() {
    val colors = HereTheme.colors
    val sizes = HereSize.LocationIllustration
    val youHeight = sizes.youSize * HereSize.EmotionCharacter.heightRatio
    val top = YouTop + shiftY(YouTop + youHeight / 2)

    Box(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .offset(y = top + youHeight / 2 - sizes.youHaloSize / 2)
            .size(sizes.youHaloSize)
            .background(
                color = colors.accent.copy(alpha = sizes.youHaloAlpha),
                shape = CircleShape
            )
    )

    EmotionCharacter(
        emotion = CharacterEmotion.YOU,
        size = sizes.youSize,
        modifier = Modifier
            .align(Alignment.TopCenter)
            .offset(y = top)
    )

    CharacterBubble(
        text = stringResource(R.string.onboarding_location_me),
        containerColor = colors.accent,
        contentColor = colors.onAccent,
        tail = BubbleTail.TOP,
        modifier = Modifier
            .align(Alignment.TopCenter)
            .offset(y = top + youHeight)
    )
}

/**
 * Прицел ручного выбора места с подсказкой над ним.
 *
 * @param frameCenter Центр головки прицела от верха кадра макета.
 */
@Composable
private fun SceneScope.Picker(frameCenter: Dp) {
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
            tail = BubbleTail.BOTTOM
        )
    }
}
