package ru.aloyaloya.design_system.component.media

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Снимок воспоминания.
 *
 * @param uri Путь к файлу во внутреннем хранилище.
 * @param modifier [Modifier], задающий размер снимка.
 * @param shape Форма снимка.
 * @param contentScale Как снимок вписан в отведенное место.
 */
@Composable
fun MediaPhoto(
    uri: String,
    modifier: Modifier = Modifier,
    shape: Shape = HereShape.tile,
    contentScale: ContentScale = ContentScale.Crop
) {
    AsyncImage(
        model = uri,
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier
            .clip(shape)
            .background(HereTheme.colors.surfaceMuted)
    )
}
