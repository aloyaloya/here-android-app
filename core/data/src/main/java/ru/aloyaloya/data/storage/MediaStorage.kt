package ru.aloyaloya.data.storage

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
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
        val file = File(root, UUID.randomUUID().toString())

        runCatching {
            root.mkdirs()
            resolver.openInputStream(Uri.parse(uri))?.use { input ->
                file.outputStream().use(input::copyTo)
            } ?: error("Не удалось открыть $uri")
            file.path
        }.getOrElse {
            file.delete()
            null
        }
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
}
