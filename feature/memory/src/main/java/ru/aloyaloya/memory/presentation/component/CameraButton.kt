package ru.aloyaloya.memory.presentation.component

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import ru.aloyaloya.design_system.component.media.MediaAddButton
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.MediaType
import ru.aloyaloya.domain.model.MemoryMedia
import ru.aloyaloya.memory.R
import java.io.File
import java.util.UUID
import ru.aloyaloya.design_system.R as DesignSystemR

private const val CAMERA_DIRECTORY = "camera"
private const val CAMERA_AUTHORITY_SUFFIX = ".camera"
private const val PHOTO_EXTENSION = "jpg"
private const val VIDEO_EXTENSION = "mp4"

/**
 * Кнопка камеры: меню «сфотографировать» или «снять видео» и запуск камеры.
 *
 * @param onCaptured Колбэк снятого фото или ролика.
 * @param modifier [Modifier], применяемый к кнопке.
 */
@Composable
internal fun CameraButton(
    onCaptured: (MemoryMedia) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var menuOpen by remember { mutableStateOf(false) }
    var pendingPath by rememberSaveable { mutableStateOf<String?>(null) }

    fun finish(success: Boolean, type: MediaType) {
        val file = pendingPath?.let(::File) ?: return
        pendingPath = null

        if (success && file.length() > 0) {
            onCaptured(MemoryMedia(uri = context.cameraUri(file).toString(), type = type))
        } else {
            file.delete()
        }
    }

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        finish(success, MediaType.PHOTO)
    }
    val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
        finish(success, MediaType.VIDEO)
    }

    fun capture(type: MediaType) {
        menuOpen = false
        val file = context.newCameraFile(if (type == MediaType.VIDEO) VIDEO_EXTENSION else PHOTO_EXTENSION)
        val uri = context.cameraUri(file)
        pendingPath = file.path

        try {
            if (type == MediaType.VIDEO) videoLauncher.launch(uri) else photoLauncher.launch(uri)
        } catch (_: ActivityNotFoundException) {
            pendingPath = null
            file.delete()
        }
    }

    Box(modifier = modifier) {
        MediaAddButton(
            text = stringResource(R.string.memory_media_camera),
            icon = DesignSystemR.drawable.ic_photo_camera,
            onClick = { menuOpen = true },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = menuOpen,
            onDismissRequest = { menuOpen = false },
            shape = HereShape.tile,
            containerColor = HereTheme.colors.surface
        ) {
            CameraMenuItem(
                text = stringResource(R.string.memory_camera_photo),
                onClick = { capture(MediaType.PHOTO) }
            )
            CameraMenuItem(
                text = stringResource(R.string.memory_camera_video),
                onClick = { capture(MediaType.VIDEO) }
            )
        }
    }
}

/**
 * Пункт меню камеры.
 *
 * @param text Подпись пункта.
 * @param onClick Колбэк нажатия.
 */
@Composable
private fun CameraMenuItem(
    text: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = HereTheme.colors.textPrimary
            )
        },
        onClick = onClick
    )
}

/** Пустой файл во внутреннем хранилище, куда камера запишет снимок: кэш система чистит при нехватке места. */
private fun Context.newCameraFile(extension: String): File {
    val directory = File(filesDir, CAMERA_DIRECTORY).apply { mkdirs() }
    return File(directory, "${UUID.randomUUID()}.$extension").apply { createNewFile() }
}

/** Адрес файла для чужого приложения: прямой путь камере не открыть. */
private fun Context.cameraUri(file: File): Uri =
    FileProvider.getUriForFile(this, packageName + CAMERA_AUTHORITY_SUFFIX, file)
