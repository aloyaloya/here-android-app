package ru.aloyaloya.design_system.component.button

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import ru.aloyaloya.design_system.extension.fabShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * FAB-кнопка с подписью вместо иконки.
 *
 * Стоит там же, где [HereFab], но называет действие словом: у режима выбора места
 * нет иконки, которая читалась бы однозначно.
 *
 * @param text Подпись кнопки.
 * @param onClick Колбэк нажатия.
 * @param modifier [Modifier], применяемый к кнопке.
 * @param container Цвет кнопки.
 * @param content Цвет подписи.
 */
@Composable
fun HereExtendedFab(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    container: Color = HereTheme.colors.accent,
    content: Color = HereTheme.colors.onAccent
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(HereSize.Fab.extendedHeight)
            .fabShadow(HereShape.pill)
            .clip(HereShape.pill)
            .background(container)
            .clickable(onClick = onClick)
            .padding(horizontal = HereSize.Fab.extendedPadding)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = content
        )
    }
}
