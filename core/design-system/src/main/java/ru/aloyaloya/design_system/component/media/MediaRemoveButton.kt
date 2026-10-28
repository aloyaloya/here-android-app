package ru.aloyaloya.design_system.component.media

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.theme.HereSize

private val RemoveButtonBackground = Color.Black.copy(alpha = 0.55f)

/**
 * Круглая кнопка «убрать» поверх снимка: маленький крестик в зоне касания побольше.
 *
 * @param onClick Колбэк удаления из подборки.
 * @param modifier [Modifier], применяемый к кнопке.
 */
@Composable
fun MediaRemoveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sizes = HereSize.MediaRemove

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(sizes.touchSize)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(sizes.size)
                .clip(CircleShape)
                .background(RemoveButtonBackground)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.media_remove),
                tint = Color.White,
                modifier = Modifier.size(sizes.iconSize)
            )
        }
    }
}
