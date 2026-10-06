package ru.aloyaloya.settings.presentation

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import ru.aloyaloya.design_system.extension.overlayShadow
import ru.aloyaloya.design_system.format.currentLocale
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.settings.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import ru.aloyaloya.design_system.R as DesignR

/**
 * Вечер: часы, дата и превью напоминания, в [NotificationsStep.BLOCKED] приглушенное.
 *
 * @param step Шаг экрана.
 * @param time Время напоминания.
 */
@Composable
fun NotificationsIllustration(
    step: NotificationsStep,
    time: LocalTime,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors
    val locale = currentLocale()
    val dateFormat = remember(locale) {
        DateTimeFormatter.ofPattern(DateFormat.getBestDateTimePattern(locale, "EEEEdMMMM"), locale)
    }

    // TODO: появление часов и карточки, смена шага — crossfade 150 мс
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HereSpacing.xl, Alignment.CenterVertically),
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(
                start = HereSpacing.screenHorizontal,
                end = HereSpacing.screenHorizontal,
                bottom = HereSize.PermissionSheet.illustrationOverlap
            )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = time.format(reminderTimeFormat(locale)),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Light,
                color = colors.textPrimary
            )

            Text(
                text = LocalDate.now().format(dateFormat),
                style = MaterialTheme.typography.labelLarge,
                color = colors.textBody
            )
        }

        Box {
            NotificationPreview(
                modifier = Modifier.alpha(
                    if (step == NotificationsStep.BLOCKED) HereSize.NotificationIllustration.mutedAlpha else 1f
                )
            )

            if (step == NotificationsStep.BLOCKED) {
                MutedBadge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(
                            x = -HereSpacing.m,
                            y = -HereSize.NotificationIllustration.badgeSize / 2
                        )
                )
            }
        }
    }
}

/**
 * Превью уведомления-напоминания.
 *
 * @param modifier [Modifier], применяемый к карточке.
 */
@Composable
private fun NotificationPreview(modifier: Modifier = Modifier) {
    val colors = HereTheme.colors
    val sizes = HereSize.NotificationIllustration

    Row(
        horizontalArrangement = Arrangement.spacedBy(HereSpacing.m),
        modifier = modifier
            .fillMaxWidth()
            .overlayShadow(HereShape.tile)
            .background(color = colors.surface, shape = HereShape.tile)
            .then(
                if (colors.isDark) {
                    Modifier.border(HereSize.PermissionSheet.darkBorder, colors.outline, HereShape.tile)
                } else {
                    Modifier
                }
            )
            .padding(HereSpacing.l)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(sizes.avatarSize)
                .background(color = colors.surfaceMuted, shape = CircleShape)
        ) {
            Icon(
                painter = painterResource(DesignR.drawable.ic_place),
                contentDescription = null,
                tint = colors.textPrimary,
                modifier = Modifier.size(sizes.iconSize)
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(HereSpacing.xs),
            modifier = Modifier.weight(1f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.notifications_preview_app),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    painter = painterResource(DesignR.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = colors.textSecondary,
                    modifier = Modifier
                        .size(sizes.iconSize)
                        .rotate(90f)
                )
            }

            Text(
                text = stringResource(R.string.notifications_preview_title),
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary
            )

            Text(
                text = stringResource(R.string.notifications_preview_text),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textBody
            )
        }
    }
}

/**
 * Значок «уведомления выключены» на углу превью.
 *
 * @param modifier [Modifier], применяемый к значку.
 */
@Composable
private fun MutedBadge(modifier: Modifier = Modifier) {
    val colors = HereTheme.colors
    val sizes = HereSize.NotificationIllustration

    // TODO: появление значка — pop
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(sizes.badgeSize)
            .overlayShadow(CircleShape)
            .background(color = colors.surface, shape = CircleShape)
            .border(sizes.badgeBorder, colors.outlineStrong, CircleShape)
    ) {
        Icon(
            painter = painterResource(DesignR.drawable.ic_notifications_off),
            contentDescription = null,
            tint = colors.textPrimary,
            modifier = Modifier.size(sizes.iconSize)
        )
    }
}

/**
 * Формат времени напоминания по локали: 21:00 или 9:00 PM.
 *
 * @param locale Локаль интерфейса.
 */
fun reminderTimeFormat(locale: Locale): DateTimeFormatter =
    DateTimeFormatter.ofPattern(DateFormat.getBestDateTimePattern(locale, "jmm"), locale)
