package ru.aloyaloya.map.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.component.button.HerePrimaryButton
import ru.aloyaloya.design_system.component.emotion.EmotionTile
import ru.aloyaloya.design_system.component.sheet.HereBottomSheet
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.map.R
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.emoji
import ru.aloyaloya.ui.emotion.labelResId

private const val EMOTIONS_PER_ROW = 3

/**
 * Лист выбора эмоции, который открывается по FAB на карте.
 *
 * Кнопка «Дальше» активна только после выбора эмоции и открывает экран нового места.
 *
 * @param onDismissRequest Колбэк закрытия листа свайпом или тапом по затемнению.
 * @param onEmotionConfirmed Колбэк подтверждения выбранной эмоции.
 * @param modifier [Modifier], применяемый к листу.
 */
@Composable
fun EmotionPickerSheet(
    onDismissRequest: () -> Unit,
    onEmotionConfirmed: (Emotion) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedEmotion by rememberSaveable { mutableStateOf<Emotion?>(null) }

    HereBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(HereSize.Sheet.contentSpacing),
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = HereSize.Sheet.horizontalPadding)
                .padding(bottom = HereSize.Sheet.bottomPadding)
        ) {
            Text(
                text = stringResource(R.string.emotion_picker_title),
                style = MaterialTheme.typography.headlineSmall,
                color = HereTheme.colors.textPrimary
            )

            EmotionGrid(
                selectedEmotion = selectedEmotion,
                onEmotionClick = { selectedEmotion = it }
            )

            HerePrimaryButton(
                text = stringResource(R.string.emotion_picker_next),
                enabled = selectedEmotion != null,
                onClick = { selectedEmotion?.let(onEmotionConfirmed) }
            )
        }
    }
}

@Composable
private fun EmotionGrid(
    selectedEmotion: Emotion?,
    onEmotionClick: (Emotion) -> Unit
) {
    val spacing = HereSize.EmotionTile.spacing

    Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
        Emotion.entries.chunked(EMOTIONS_PER_ROW).forEach { rowEmotions ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowEmotions.forEach { emotion ->
                    EmotionTile(
                        emoji = emotion.emoji,
                        label = stringResource(emotion.labelResId),
                        color = emotion.color,
                        selected = emotion == selectedEmotion,
                        onClick = { onEmotionClick(emotion) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}