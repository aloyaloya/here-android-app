package ru.aloyaloya.design_system.component.button

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Текстовая кнопка без фона: «Пропустить», «Не сейчас».
 *
 * @param text Подпись кнопки.
 * @param onClick Колбэк нажатия.
 * @param modifier [Modifier], применяемый к кнопке.
 * @param fontSize Кегль подписи.
 */
@Composable
fun HereTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = HereSize.TextButton.textSize
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(HereSize.TextButton.height)
            .clip(HereShape.pill)
            .clickable(onClick = onClick)
            .padding(horizontal = HereSize.TextButton.horizontalPadding)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = fontSize,
                fontWeight = FontWeight.SemiBold
            ),
            color = HereTheme.colors.textPrimary
        )
    }
}
