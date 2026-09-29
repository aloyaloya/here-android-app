package ru.aloyaloya.map.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import ru.aloyaloya.design_system.component.emotion.EmotionBadge
import ru.aloyaloya.design_system.component.sheet.HereBottomSheet
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.map.R
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.emoji
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"))

/**
 * Лист с воспоминаниями, которые стоят в одной точке карты.
 *
 * @param memories Воспоминания точки, свежие сверху.
 * @param onMemoryClick Колбэк нажатия на воспоминание.
 * @param onDismissRequest Колбэк закрытия листа.
 */
@Composable
fun PlaceMemoriesSheet(
    memories: List<Memory>,
    onMemoryClick: (Long) -> Unit,
    onDismissRequest: () -> Unit
) {
    HereBottomSheet(onDismissRequest = onDismissRequest) {
        Column(
            verticalArrangement = Arrangement.spacedBy(HereSize.Sheet.contentSpacing),
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = HereSize.Sheet.horizontalPadding)
                .padding(bottom = HereSize.Sheet.bottomPadding)
        ) {
            Text(
                text = pluralStringResource(
                    R.plurals.place_memories_title,
                    memories.size,
                    memories.size
                ),
                style = MaterialTheme.typography.headlineSmall,
                color = HereTheme.colors.textPrimary
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(HereSize.PlaceMemories.rowSpacing),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                memories.forEach { memory ->
                    PlaceMemoryRow(
                        memory = memory,
                        onClick = { onMemoryClick(memory.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceMemoryRow(
    memory: Memory,
    onClick: () -> Unit
) {
    val sizes = HereSize.PlaceMemories

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(sizes.badgeSpacing),
        modifier = Modifier
            .fillMaxWidth()
            .clip(HereShape.tile)
            .background(HereTheme.colors.surface)
            .clickable(onClick = onClick)
            .padding(sizes.rowPadding)
    ) {
        EmotionBadge(
            emoji = memory.emotion.emoji,
            color = memory.emotion.color.soft
        )

        Column(verticalArrangement = Arrangement.spacedBy(sizes.textSpacing)) {
            Text(
                text = memory.title,
                style = MaterialTheme.typography.titleMedium,
                color = HereTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = DateFormat.format(
                    Instant.ofEpochMilli(memory.happenedAt).atZone(ZoneId.systemDefault())
                ),
                style = MaterialTheme.typography.bodySmall,
                color = HereTheme.colors.textTertiary
            )
        }
    }
}
