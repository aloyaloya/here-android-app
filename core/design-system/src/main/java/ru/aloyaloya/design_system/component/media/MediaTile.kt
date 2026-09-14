package ru.aloyaloya.design_system.component.media

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

private val RemoveButtonBackground = Color.Black.copy(alpha = 0.55f)

/**
 * Плитка выбранного медиафайла с кнопкой «убрать».
 *
 * @param uri Путь к файлу или content-адрес только что выбранного файла.
 * @param onRemoveClick Колбэк удаления из подборки.
 * @param video Файл — видео: поверх кадра появляется значок.
 * @param modifier [Modifier], применяемый к плитке.
 */
@Composable
fun MediaTile(
    uri: String,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
    video: Boolean = false
) {
    Box(
        modifier = modifier
            .size(HereSize.MediaTile.size)
            .clip(HereShape.tile)
            .background(HereTheme.colors.surfaceMuted)
    ) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(HereSize.MediaTile.size)
        )

        if (video) {
            MediaPlayBadge(modifier = Modifier.align(Alignment.Center))
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(HereSize.MediaTile.removeMargin)
                .size(HereSize.MediaTile.removeSize)
                .clip(CircleShape)
                .background(RemoveButtonBackground)
                .clickable(onClick = onRemoveClick)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.media_remove),
                tint = Color.White,
                modifier = Modifier.size(HereSize.MediaTile.removeIconSize)
            )
        }
    }
}
