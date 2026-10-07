package ru.aloyaloya.onboarding.presentation.illustration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import ru.aloyaloya.design_system.component.character.CharacterEmotion
import ru.aloyaloya.design_system.component.character.EmotionCharacter
import ru.aloyaloya.design_system.component.emotion.EmotionTag
import ru.aloyaloya.design_system.component.media.MediaPlayBadge
import ru.aloyaloya.design_system.format.DayMonthFormat
import ru.aloyaloya.design_system.format.TimeFormat
import ru.aloyaloya.design_system.format.currentLocale
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.onboarding.R
import ru.aloyaloya.ui.emotion.character
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.labelResId
import java.time.LocalDateTime

/** Момент воспоминания на иллюстрации. */
private val MemoryMoment = LocalDateTime.of(2026, 7, 15, 18, 40)

/** Проявление карточки. */
private const val CARD_MILLIS = 120

/** Падение «Нежности». */
private const val TENDER_DROP_MILLIS = 300

/** Падение «Покоя». */
private const val CALM_DROP_MILLIS = 600

/**
 * Карточка воспоминания между двумя персонажами.
 */
@Composable
fun MemoryIllustration(
    modifier: Modifier = Modifier
) {
    IllustrationGroup(modifier = modifier) {
        EmotionCharacter(
            emotion = CharacterEmotion.TENDER,
            size = HereSize.EmotionCharacter.medium,
            look = LOOK_RIGHT,
            modifier = Modifier
                .padding(start = HereSpacing.s)
                .entrance(kind = Entrance.DROP, delayMillis = TENDER_DROP_MILLIS)
        )

        MemoryCard(
            modifier = Modifier.entrance(kind = Entrance.FADE, delayMillis = CARD_MILLIS)
        )

        EmotionCharacter(
            emotion = CharacterEmotion.CALM,
            size = HereSize.EmotionCharacter.small,
            look = LOOK_LEFT,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = HereSpacing.m)
                .entrance(kind = Entrance.DROP, delayMillis = CALM_DROP_MILLIS)
        )
    }
}

/**
 * Карточка воспоминания: эмоция, название, время, заметка и превью снимков.
 *
 * @param modifier [Modifier], применяемый к карточке.
 */
@Composable
private fun MemoryCard(modifier: Modifier = Modifier) {
    val colors = HereTheme.colors
    val emotion = Emotion.TENDER
    val dateFormat = DayMonthFormat.withLocale(currentLocale())
    val timeFormat = TimeFormat.withLocale(currentLocale())

    Column(
        verticalArrangement = Arrangement.spacedBy(HereSpacing.m),
        modifier = modifier
            .fillMaxWidth()
            .illustrationCard()
            .padding(HereSpacing.xl)
    ) {
        EmotionTag(
            character = emotion.character,
            label = stringResource(emotion.labelResId),
            color = emotion.color.soft
        )

        Column(verticalArrangement = Arrangement.spacedBy(HereSpacing.xs)) {
            Text(
                text = stringResource(R.string.onboarding_card_title),
                style = MaterialTheme.typography.titleLarge,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${MemoryMoment.format(dateFormat)} · ${MemoryMoment.format(timeFormat)}",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary
            )
        }

        Text(
            text = stringResource(R.string.onboarding_card_note),
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textBody,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Row(horizontalArrangement = Arrangement.spacedBy(HereSpacing.s)) {
            LandscapePreview()
            PortraitPreview()
            VideoPreview()
        }
    }
}

/** Превью-пейзаж: холм и солнце. */
@Composable
private fun LandscapePreview() {
    val emotions = HereTheme.colors.emotions
    val sizes = HereSize.OnboardingIllustration

    Box(modifier = Modifier.preview(emotions.sad.soft)) {
        Box(
            modifier = Modifier
                .offset(x = -HereSpacing.m, y = sizes.previewSize * 0.6f)
                .requiredSize(sizes.previewSize * 1.4f, sizes.previewSize * 0.77f)
                .background(color = emotions.calm.solid, shape = CircleShape)
        )

        Box(
            modifier = Modifier
                .offset(x = sizes.previewSize * 0.6f, y = sizes.previewSize / 6)
                .size(sizes.previewSize * 0.22f)
                .background(color = emotions.happy.solid, shape = CircleShape)
        )
    }
}

/** Превью-портрет: круг у нижнего края. */
@Composable
private fun PortraitPreview() {
    val emotions = HereTheme.colors.emotions
    val sizes = HereSize.OnboardingIllustration

    Box(modifier = Modifier.preview(emotions.tender.solid)) {
        Box(
            modifier = Modifier
                .offset(x = -HereSpacing.s, y = sizes.previewSize / 2)
                .requiredSize(sizes.previewSize * 0.77f)
                .background(color = emotions.happy.soft, shape = CircleShape)
        )
    }
}

/** Превью видео со значком воспроизведения. */
@Composable
private fun VideoPreview() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.preview(HereTheme.colors.emotions.surprised.solid)
    ) {
        MediaPlayBadge()
    }
}

/**
 * Квадрат превью с обрезкой по форме плитки.
 *
 * @param color Фон превью.
 */
private fun Modifier.preview(color: Color): Modifier = this
    .size(HereSize.OnboardingIllustration.previewSize)
    .clip(HereShape.tile)
    .background(color)
