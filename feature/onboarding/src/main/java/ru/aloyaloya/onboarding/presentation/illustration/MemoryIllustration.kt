package ru.aloyaloya.onboarding.presentation.illustration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
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
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.emoji
import ru.aloyaloya.ui.emotion.labelResId
import java.time.LocalDateTime

/** Момент воспоминания на иллюстрации. */
private val MemoryMoment = LocalDateTime.of(2026, 7, 15, 18, 40)

/** Эмоции превью снимков на иллюстрации. */
private val PreviewEmotions = listOf(Emotion.HAPPY, Emotion.CALM, Emotion.SAD)

/** Карточка воспоминания: эмоция, название, время, заметка и превью снимков. */
@Composable
fun MemoryIllustration(modifier: Modifier = Modifier) {
    val colors = HereTheme.colors
    val emotion = Emotion.TENDER
    val dateFormat = DayMonthFormat.withLocale(currentLocale())
    val timeFormat = TimeFormat.withLocale(currentLocale())

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(emotion.color.soft)
            .padding(HereSpacing.l)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(HereSpacing.s),
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colors.surface, shape = HereShape.tile)
                .padding(HereSpacing.l)
        ) {
            EmotionTag(
                emoji = emotion.emoji,
                label = stringResource(emotion.labelResId),
                color = emotion.color.soft
            )

            Text(
                text = stringResource(R.string.onboarding_card_title),
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${MemoryMoment.format(dateFormat)} · ${MemoryMoment.format(timeFormat)}",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )

            Text(
                text = stringResource(R.string.onboarding_card_note),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textBody,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(horizontalArrangement = Arrangement.spacedBy(HereSpacing.s)) {
                PreviewEmotions.forEachIndexed { index, previewEmotion ->
                    MediaPreview(
                        emotion = previewEmotion,
                        video = index == PreviewEmotions.lastIndex
                    )
                }
            }
        }
    }
}

/**
 * Превью снимка из фигур в цветах эмоции.
 *
 * @param emotion Эмоция, задающая цвета.
 * @param video Показывать ли значок видео.
 */
@Composable
private fun MediaPreview(
    emotion: Emotion,
    video: Boolean
) {
    val sizes = HereSize.OnboardingIllustration

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(sizes.previewSize)
            .clip(HereShape.tile)
            .background(emotion.color.soft)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(sizes.previewSize / 2)
                .background(color = emotion.color.solid, shape = CircleShape)
        )

        if (video) {
            MediaPlayBadge(
                size = sizes.previewBadgeSize,
                iconSize = sizes.previewBadgeIconSize
            )
        }
    }
}
