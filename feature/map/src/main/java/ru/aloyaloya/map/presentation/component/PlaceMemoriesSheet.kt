package ru.aloyaloya.map.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import ru.aloyaloya.design_system.component.memory.HereMemoryRow
import ru.aloyaloya.design_system.component.sheet.HereBottomSheet
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
                verticalArrangement = Arrangement.spacedBy(HereSize.MemoryRow.spacing),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                memories.forEach { memory ->
                    HereMemoryRow(
                        emoji = memory.emotion.emoji,
                        color = memory.emotion.color.soft,
                        title = memory.title,
                        subtitle = DateFormat.format(
                            Instant.ofEpochMilli(memory.happenedAt).atZone(ZoneId.systemDefault())
                        ),
                        onClick = { onMemoryClick(memory.id) }
                    )
                }
            }
        }
    }
}
