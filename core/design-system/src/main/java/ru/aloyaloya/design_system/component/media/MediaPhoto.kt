package ru.aloyaloya.design_system.component.media

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.video.VideoFrameDecoder
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Снимок воспоминания или первый кадр ролика.
 *
 * @param uri Путь к файлу во внутреннем хранилище.
 * @param modifier [Modifier], задающий размер снимка.
 * @param video Файл — видео: показывается первый кадр.
 * @param shape Форма снимка.
 * @param contentScale Как снимок вписан в отведенное место.
 */
@Composable
fun MediaPhoto(
    uri: String,
    modifier: Modifier = Modifier,
    video: Boolean = false,
    shape: Shape = HereShape.tile,
    contentScale: ContentScale = ContentScale.Crop
) {
    AsyncImage(
        model = rememberMediaRequest(uri, video),
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier
            .clip(shape)
            .background(HereTheme.colors.surfaceMuted)
    )
}

/**
 * Запрос к Coil.
 */
@Composable
internal fun rememberMediaRequest(uri: String, video: Boolean): ImageRequest {
    val context = LocalPlatformContext.current

    return remember(uri, video) {
        ImageRequest.Builder(context)
            .data(uri)
            .apply {
                if (video) {
                    decoderFactory { result, options, _ ->
                        VideoFrameDecoder(result.source, options)
                    }
                }
            }
            .build()
    }
}
