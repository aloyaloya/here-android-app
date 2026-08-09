package ru.aloyaloya.design_system.component.media

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Кнопка добавления фото или видео — пунктирный квадрат в ряду медиа.
 *
 * @param onClick Колбэк нажатия.
 * @param modifier [Modifier], применяемый к плитке.
 */
@Composable
fun MediaAddTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(HereSize.MediaTile.size)
            .drawBehind {
                drawOutline(
                    outline = HereShape.tile.createOutline(size, layoutDirection, this),
                    color = colors.outline,
                    style = Stroke(
                        width = HereSize.MediaTile.addBorder.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(
                                HereSize.MediaTile.addDash.toPx(),
                                HereSize.MediaTile.addGap.toPx()
                            )
                        )
                    )
                )
            }
            .clickable(onClick = onClick)
    ) {
        Text(
            text = stringResource(R.string.media_add),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textTertiary
        )
    }
}
