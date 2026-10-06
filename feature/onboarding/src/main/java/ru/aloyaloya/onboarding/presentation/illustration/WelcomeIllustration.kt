package ru.aloyaloya.onboarding.presentation.illustration

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.component.character.BubbleTail
import ru.aloyaloya.design_system.component.character.CharacterBubble
import ru.aloyaloya.design_system.component.character.CharacterEmotion
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.onboarding.R

/** Говорящий о море. */
private val SeaSpeaker = CharacterSpot(CharacterEmotion.HAPPY, 0.17f, 0.23f, HereSize.EmotionCharacter.large, LOOK_RIGHT)

/** Говорящий о свидании. */
private val DateSpeaker = CharacterSpot(CharacterEmotion.TENDER, 0.63f, 0.26f, HereSize.EmotionCharacter.medium)

/** Персонажи первого экрана. */
private val WelcomeCharacters = listOf(
    SeaSpeaker,
    DateSpeaker,
    CharacterSpot(CharacterEmotion.SURPRISED, 0.41f, 0.45f, HereSize.EmotionCharacter.small),
    CharacterSpot(CharacterEmotion.CALM, 0.18f, 0.64f, HereSize.EmotionCharacter.medium, LOOK_RIGHT),
    CharacterSpot(CharacterEmotion.SAD, 0.8f, 0.53f, HereSize.EmotionCharacter.small, LOOK_LEFT),
    CharacterSpot(CharacterEmotion.ANGRY, 0.52f, 0.71f, HereSize.EmotionCharacter.small)
)

/** Начало падений. */
private const val DROP_START_MILLIS = 100

/** Появление реплики о море. */
private const val SEA_BUBBLE_MILLIS = 1000

/** Появление реплики о свидании. */
private const val DATE_BUBBLE_MILLIS = 1150

/** Хвостик реплики о свидании от ее левого края. */
private val DateTailInset = HereSpacing.xl

/**
 * Карта с персонажами-эмоциями, двое из них говорят.
 */
@Composable
fun WelcomeIllustration(
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors
    val emotions = colors.emotions

    IllustrationScene(
        modifier = modifier,
        backdrop = { MapBackground() }
    ) {
        WelcomeCharacters.forEachIndexed { index, spot ->
            CharacterAt(
                spot = spot,
                delayMillis = DROP_START_MILLIS + index * DROP_STEP_MILLIS
            )
        }

        val seaPivot = TransformOrigin(0f, 0.5f)
        CharacterBubble(
            text = stringResource(R.string.onboarding_bubble_sea),
            containerColor = emotions.happy.solid,
            contentColor = colors.textPrimary,
            tail = BubbleTail.START,
            modifier = Modifier
                .pinTo(x = SeaSpeaker.right, y = SeaSpeaker.centerY, pivot = seaPivot)
                .entrance(kind = Entrance.BUBBLE, delayMillis = SEA_BUBBLE_MILLIS, origin = seaPivot)
        )

        val datePivot = TransformOrigin(0f, 0f)
        CharacterBubble(
            text = stringResource(R.string.onboarding_bubble_date),
            containerColor = emotions.tender.solid,
            contentColor = colors.textPrimary,
            tail = BubbleTail.TOP,
            tailInset = DateTailInset,
            modifier = Modifier
                .pinTo(x = DateSpeaker.centerX - DateTailInset, y = DateSpeaker.bottom, pivot = datePivot)
                .entrance(kind = Entrance.BUBBLE, delayMillis = DATE_BUBBLE_MILLIS, origin = datePivot)
        )
    }
}
