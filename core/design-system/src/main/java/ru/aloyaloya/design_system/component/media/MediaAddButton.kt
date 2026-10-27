package ru.aloyaloya.design_system.component.media

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Пунктирная кнопка добавления фото или видео: значок и подпись по центру.
 *
 * @param text Подпись кнопки.
 * @param icon Значок перед подписью.
 * @param onClick Колбэк нажатия.
 * @param modifier [Modifier], применяемый к кнопке.
 */
@Composable
fun MediaAddButton(
    text: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors
    val sizes = HereSize.MediaAdd

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(sizes.spacing, Alignment.CenterHorizontally),
        modifier = modifier
            .height(sizes.height)
            .drawBehind {
                drawOutline(
                    outline = HereShape.tile.createOutline(size, layoutDirection, this),
                    color = colors.outlineStrong,
                    style = Stroke(
                        width = sizes.dashWidth.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(sizes.dashLength.toPx(), sizes.dashGap.toPx())
                        )
                    )
                )
            }
            .clip(HereShape.tile)
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = colors.textPrimary,
            modifier = Modifier.size(sizes.iconSize)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary
        )
    }
}
