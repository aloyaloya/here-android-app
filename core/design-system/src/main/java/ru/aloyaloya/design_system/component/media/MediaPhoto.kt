package ru.aloyaloya.design_system.component.media

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Снимок воспоминания.
 *
 * @param uri Путь к файлу во внутреннем хранилище.
 * @param modifier [Modifier], задающий размер снимка.
 */
@Composable
fun MediaPhoto(
    uri: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = uri,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(HereShape.tile)
            .background(HereTheme.colors.surfaceMuted)
    )
}
