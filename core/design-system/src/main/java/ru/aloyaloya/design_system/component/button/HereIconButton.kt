package ru.aloyaloya.design_system.component.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import ru.aloyaloya.design_system.extension.overlayShadow
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Круглая кнопка с иконкой поверх содержимого экрана.
 *
 * @param icon Иконка кнопки.
 * @param contentDescription Описание действия для программ чтения с экрана.
 * @param onClick Колбэк нажатия.
 * @param modifier [Modifier], применяемый к кнопке.
 */
@Composable
fun HereIconButton(
    @DrawableRes icon: Int,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .size(HereSize.IconButton.size)
            .overlayShadow(CircleShape)
            .clip(CircleShape)
            .background(colors.surface.copy(alpha = HereSize.IconButton.backgroundAlpha))
            .clickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(icon),
            tint = colors.textPrimary,
            contentDescription = contentDescription,
            modifier = Modifier.size(HereSize.IconButton.iconSize)
        )
    }
}
