package ru.aloyaloya.data.storage

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val MEDIA_DIRECTORY = "media"

/** Файлы медиа во внутреннем хранилище приложения. */
@Singleton
class MediaStorage @Inject constructor(context: Context) {

    private val resolver: ContentResolver = context.contentResolver

    private val root = File(context.filesDir, MEDIA_DIRECTORY)

    suspend fun save(uri: String): String? = withContext(Dispatchers.IO) {
        val source = Uri.parse(uri)
        val file = File(root, fileName(source))

        runCatching {
            root.mkdirs()
            resolver.openInputStream(source)?.use { input ->
                file.outputStream().use(input::copyTo)
            } ?: error("Не удалось открыть $uri")
            file.path
        }.getOrElse {
            file.delete()
            null
        }
    }

    /** Сохраняет поток в новый файл с расширением [extension] и возвращает путь к нему. */
    suspend fun save(input: InputStream, extension: String?): String = withContext(Dispatchers.IO) {
        val name = UUID.randomUUID().toString()
        val file = File(root, if (extension == null) name else "$name.$extension")

        root.mkdirs()
        file.outputStream().use(input::copyTo)
        file.path
    }

    suspend fun delete(paths: List<String>) {
        if (paths.isEmpty()) return

        withContext(Dispatchers.IO) {
            val rootPath = root.canonicalPath

            paths.forEach { path ->
                val file = File(path)
                if (file.canonicalPath.startsWith(rootPath)) file.delete()
            }
        }
    }

    /** Удаляет все сохраненные медиафайлы вместе с папкой. */
    suspend fun clear() {
        withContext(Dispatchers.IO) { root.deleteRecursively() }
    }

    /** Имя копии с расширением: по нему Coil и плеер понимают, что за файл внутри. */
    private fun fileName(uri: Uri): String {
        val name = UUID.randomUUID().toString()
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(resolver.getType(uri))

        return if (extension == null) name else "$name.$extension"
    }
}
