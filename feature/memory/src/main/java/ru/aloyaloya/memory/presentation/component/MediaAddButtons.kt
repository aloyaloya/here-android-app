package ru.aloyaloya.memory.presentation.component

import android.content.ContentResolver
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.component.media.MediaAddButton
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.domain.model.MediaType
import ru.aloyaloya.domain.model.MemoryMedia
import ru.aloyaloya.memory.R
import ru.aloyaloya.design_system.R as DesignSystemR

/** Сколько файлов можно прикрепить к одному воспоминанию. */
internal const val MEDIA_LIMIT = 10

private const val VIDEO_MIME_PREFIX = "video/"

/**
 * Ряд из двух кнопок добавления медиа: «Галерея» и «Камера», без камеры галерея на всю ширину.
 *
 * @param onMediaPicked Колбэк выбранных или снятых файлов.
 * @param modifier [Modifier], применяемый к ряду.
 * @param maxItems Сколько файлов еще можно добавить.
 */
@Composable
internal fun MediaAddButtons(
    onMediaPicked: (List<MemoryMedia>) -> Unit,
    modifier: Modifier = Modifier,
    maxItems: Int = MEDIA_LIMIT
) {
    val context = LocalContext.current
    val resolver = context.contentResolver
    val hasCamera = remember { context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(MEDIA_LIMIT)
    ) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        onMediaPicked(uris.take(maxItems).map { uri ->
            MemoryMedia(
                uri = uri.toString(),
                type = resolver.mediaType(uri)
            )
        })
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(HereSize.MediaAdd.spacing),
        modifier = modifier
    ) {
        MediaAddButton(
            text = stringResource(R.string.memory_media_gallery),
            icon = DesignSystemR.drawable.ic_photo_library,
            onClick = {
                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            },
            modifier = Modifier.weight(1f)
        )

        if (hasCamera) {
            CameraButton(
                onCaptured = { onMediaPicked(listOf(it)) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/** Что именно выбрали в пикере: адрес о типе файла не говорит, а MIME говорит. */
private fun ContentResolver.mediaType(uri: Uri): MediaType =
    if (getType(uri)?.startsWith(VIDEO_MIME_PREFIX) == true) MediaType.VIDEO else MediaType.PHOTO
