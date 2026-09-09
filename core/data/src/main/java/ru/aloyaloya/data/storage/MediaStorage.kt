package ru.aloyaloya.data.storage

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val MEDIA_DIRECTORY = "media"

/** Файлы медиа во внутреннем хранилище приложения. */
@Singleton
class MediaStorage @Inject constructor(context: Context) {

    private val root = File(context.filesDir, MEDIA_DIRECTORY)

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
