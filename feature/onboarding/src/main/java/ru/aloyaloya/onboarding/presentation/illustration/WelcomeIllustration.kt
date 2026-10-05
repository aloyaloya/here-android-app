package ru.aloyaloya.onboarding.presentation.illustration

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.aloyaloya.design_system.component.character.BubbleTail
import ru.aloyaloya.design_system.component.character.CharacterBubble
import ru.aloyaloya.design_system.component.character.CharacterEmotion
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.onboarding.R

/** Говорящий о море. */
private val SeaSpeaker = CharacterSpot(CharacterEmotion.HAPPY, 24.dp, 74.dp, 72.dp, LOOK_RIGHT)

/** Говорящий о свидании. */
private val DateSpeaker = CharacterSpot(CharacterEmotion.TENDER, 196.dp, 96.dp, 64.dp)

/** Персонажи первой страницы. */
private val WelcomeCharacters = listOf(
    SeaSpeaker,
    DateSpeaker,
    CharacterSpot(CharacterEmotion.SURPRISED, 120.dp, 196.dp, 58.dp),
    CharacterSpot(CharacterEmotion.CALM, 30.dp, 286.dp, 66.dp, LOOK_RIGHT),
    CharacterSpot(CharacterEmotion.SAD, 252.dp, 236.dp, 58.dp, LOOK_LEFT),
    CharacterSpot(CharacterEmotion.ANGRY, 160.dp, 330.dp, 52.dp)
)

/** Высота кадра макета: до верха панели и чуть под нее. */
private val FrameHeight = 508.dp

/** Карта с персонажами-эмоциями, двое из них говорят. */
@Composable
fun WelcomeIllustration(modifier: Modifier = Modifier) {
    val colors = HereTheme.colors
    val emotions = colors.emotions

    // TODO: сделать анимацию падения персонажей и появления реплик
    IllustrationScene(
        frameHeight = FrameHeight,
        modifier = modifier,
        backdrop = { MapBackground() }
    ) {
        WelcomeCharacters.forEach { CharacterAt(it) }

        CharacterBubble(
            text = stringResource(R.string.onboarding_bubble_sea),
            containerColor = emotions.happy.solid,
            contentColor = colors.textPrimary,
            tail = BubbleTail.START,
            modifier = Modifier.sceneOffset(
                x = 97.dp,
                y = 96.dp,
                anchorX = SeaSpeaker.centerX,
                anchorY = SeaSpeaker.centerY
            )
        )

        CharacterBubble(
            text = stringResource(R.string.onboarding_bubble_date),
            containerColor = emotions.tender.solid,
            contentColor = colors.textPrimary,
            tail = BubbleTail.TOP,
            tailInset = 25.dp,
            modifier = Modifier.sceneOffset(
                x = 204.dp,
                y = 171.dp,
                anchorX = DateSpeaker.centerX,
                anchorY = DateSpeaker.centerY
            )
        )
    }
}
