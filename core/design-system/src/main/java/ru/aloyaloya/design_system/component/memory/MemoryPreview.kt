package ru.aloyaloya.design_system.component.memory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import ru.aloyaloya.design_system.component.button.HereTonalButton
import ru.aloyaloya.design_system.component.emotion.EmotionBadge
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Превью воспоминания: иконка эмоции, заголовок, дата с адресом и что внутри.
 *
 * @param emoji Эмодзи эмоции.
 * @param emotionColor Мягкий тон эмоции из палитры: фон иконки.
 * @param title Заголовок воспоминания.
 * @param subtitle Дата и адрес одной строкой.
 * @param openText Подпись кнопки перехода к воспоминанию.
 * @param onOpenClick Колбэк перехода к воспоминанию.
 * @param modifier [Modifier], применяемый к превью.
 */
@Composable
fun MemoryPreview(
    emoji: String,
    emotionColor: Color,
    title: String,
    subtitle: String,
    openText: String,
    onOpenClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HereTheme.colors

    Column(
        verticalArrangement = Arrangement.spacedBy(HereSize.MemoryPreview.dividerSpacing),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HereSize.MemoryPreview.headerSpacing),
            modifier = Modifier.fillMaxWidth()
        ) {
            EmotionBadge(emoji = emoji, color = emotionColor)

            Column(
                verticalArrangement = Arrangement.spacedBy(HereSize.MemoryPreview.titleSpacing),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        HereTonalButton(
            text = openText,
            onClick = onOpenClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
