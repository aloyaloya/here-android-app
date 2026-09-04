package ru.aloyaloya.map.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.component.memory.MemoryPreview
import ru.aloyaloya.design_system.component.sheet.HereBottomSheet
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.map.R
import ru.aloyaloya.map.model.SelectedMemory
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.emoji
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val SEPARATOR = " · "
private val DateFormat = DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru"))

/**
 * Лист воспоминания, который открывается нажатием на метку карты.
 *
 * @param selected Выбранное воспоминание вместе с адресом его точки.
 * @param onDismissRequest Колбэк закрытия листа свайпом или тапом по затемнению.
 * @param onOpenClick Колбэк перехода к воспоминанию.
 * @param modifier [Modifier], применяемый к листу.
 */
@Composable
fun MemoryPreviewSheet(
    selected: SelectedMemory,
    onDismissRequest: () -> Unit,
    onOpenClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val memory = selected.memory

    HereBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = HereSize.Sheet.horizontalPadding)
                .padding(bottom = HereSize.Sheet.bottomPadding)
        ) {
            MemoryPreview(
                emoji = memory.emotion.emoji,
                emotionColor = memory.emotion.color.soft,
                title = memory.title,
                subtitle = subtitle(
                    happenedAt = memory.happenedAt,
                    address = selected.address
                ),
                openText = stringResource(R.string.memory_preview_open),
                onOpenClick = onOpenClick
            )
        }
    }
}

@Composable
private fun subtitle(happenedAt: Long, address: String?): String {
    val date = Instant.ofEpochMilli(happenedAt)
        .atZone(ZoneId.systemDefault())
        .format(DateFormat)

    return if (address == null) date else date + SEPARATOR + address
}
