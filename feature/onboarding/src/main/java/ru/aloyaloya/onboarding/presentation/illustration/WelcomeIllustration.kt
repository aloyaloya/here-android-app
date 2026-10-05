package ru.aloyaloya.onboarding.presentation.illustration

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
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

/** Начало падений. */
private const val DROP_START_MILLIS = 100

/** Появление реплики о море. */
private const val SEA_BUBBLE_MILLIS = 1000

/** Появление реплики о свидании. */
private const val DATE_BUBBLE_MILLIS = 1150

/**
 * Карта с персонажами-эмоциями, двое из них говорят.
 *
 * @param active Текущая ли страница.
 */
@Composable
fun WelcomeIllustration(
    active: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors
    val emotions = colors.emotions

    IllustrationScene(
        frameHeight = FrameHeight,
        modifier = modifier,
        backdrop = { MapBackground() }
    ) {
        WelcomeCharacters.forEachIndexed { index, spot ->
            CharacterAt(
                spot = spot,
                active = active,
                delayMillis = DROP_START_MILLIS + index * DROP_STEP_MILLIS
            )
        }

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
            ).entrance(
                active = active,
                kind = Entrance.BUBBLE,
                delayMillis = SEA_BUBBLE_MILLIS,
                origin = TransformOrigin(0f, 0.5f)
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
            ).entrance(
                active = active,
                kind = Entrance.BUBBLE,
                delayMillis = DATE_BUBBLE_MILLIS,
                origin = TransformOrigin(0.25f, 0f)
            )
        )
    }
}
