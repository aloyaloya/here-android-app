package ru.aloyaloya.design_system.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Компактная кнопка-пилюля на приглушенном акцентном фоне.
 *
 * В отличие от [HerePrimaryButton] занимает только свою ширину и обходится без тени.
 *
 * @param text Подпись кнопки.
 * @param onClick Колбэк нажатия.
 * @param modifier [Modifier], применяемый к кнопке.
 */
@Composable
fun HereTonalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = HereSize.TonalButton.iconSpacing,
            alignment = Alignment.CenterHorizontally
        ),
        modifier = modifier
            .height(HereSize.TonalButton.height)
            .clip(HereShape.pill)
            .background(colors.accentContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = HereSize.TonalButton.horizontalPadding)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = colors.accent
        )
    }
}
