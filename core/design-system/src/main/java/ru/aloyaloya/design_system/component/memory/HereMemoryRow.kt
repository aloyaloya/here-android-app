package ru.aloyaloya.design_system.component.memory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import ru.aloyaloya.design_system.component.character.CharacterEmotion
import ru.aloyaloya.design_system.component.emotion.EmotionBadge
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Строка воспоминания в списке: персонаж эмоции, заголовок и подпись под ним.
 *
 * @param character Персонаж эмоции.
 * @param color Мягкий тон эмоции из палитры.
 * @param title Заголовок воспоминания.
 * @param subtitle Подпись под заголовком.
 * @param onClick Колбэк нажатия на строку.
 * @param modifier [Modifier], применяемый к строке.
 * @param memoryId Воспоминание, к которому перелетает персонаж, или `null`.
 */
@Composable
fun HereMemoryRow(
    character: CharacterEmotion,
    color: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    memoryId: Long? = null
) {
    val sizes = HereSize.MemoryRow

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(sizes.badgeSpacing),
        modifier = modifier
            .fillMaxWidth()
            .clip(HereShape.tile)
            .background(HereTheme.colors.surface)
            .clickable(onClick = onClick)
            .padding(sizes.padding)
    ) {
        EmotionBadge(
            character = character,
            color = color,
            memoryId = memoryId
        )

        Column(verticalArrangement = Arrangement.spacedBy(sizes.textSpacing)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = HereTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = HereTheme.colors.textTertiary
            )
        }
    }
}
