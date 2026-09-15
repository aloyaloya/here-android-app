package ru.aloyaloya.design_system.component.media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.theme.HereSize

private val BadgeBackground = Color.Black.copy(alpha = 0.45f)

/**
 * Значок видео поверх кадра: без него ролик не отличить от снимка.
 *
 * @param modifier [Modifier], применяемый к значку.
 * @param size Диаметр значка.
 * @param iconSize Размер треугольника внутри.
 */
@Composable
fun MediaPlayBadge(
    modifier: Modifier = Modifier,
    size: Dp = HereSize.MediaBadge.size,
    iconSize: Dp = HereSize.MediaBadge.iconSize
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .background(color = BadgeBackground, shape = CircleShape)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_play),
            contentDescription = stringResource(R.string.media_video),
            tint = Color.White,
            modifier = Modifier.size(iconSize)
        )
    }
}
