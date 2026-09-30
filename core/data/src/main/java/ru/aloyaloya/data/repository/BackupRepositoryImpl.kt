package ru.aloyaloya.data.repository

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import ru.aloyaloya.data.mapper.MemoryMapper.toDomain
import ru.aloyaloya.data.mapper.MemoryMapper.toEntity
import ru.aloyaloya.data.storage.MediaStorage
import ru.aloyaloya.database.dao.MemoryDao
import ru.aloyaloya.database.dao.MemoryMediaDao
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.domain.model.MediaType
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.model.MemoryMedia
import ru.aloyaloya.domain.repository.BackupRepository
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject

private const val FORMAT_VERSION = 1
private const val MEMORIES_ENTRY = "memories.json"
private const val MEDIA_PREFIX = "media/"

/**
 * Реализация [BackupRepository]: `memories.json` и папка `media/` в одном zip.
 */
class BackupRepositoryImpl @Inject constructor(
    context: Context,
    private val memoryDao: MemoryDao,
    private val memoryMediaDao: MemoryMediaDao,
    private val mediaStorage: MediaStorage
) : BackupRepository {

    private val resolver = context.contentResolver

    override suspend fun export(destination: String): Int = withContext(Dispatchers.IO) {
        val memories = memoryDao.observeAll().first().map { it.toDomain() }
        val output = resolver.openOutputStream(Uri.parse(destination))
            ?: error("Не удалось открыть $destination")

        ZipOutputStream(output).use { zip ->
            zip.putNextEntry(ZipEntry(MEMORIES_ENTRY))
            zip.write(memories.toJson().toString().toByteArray())
            zip.closeEntry()

            memories.flatMap { it.media }.forEach { media ->
                val file = File(media.uri)
                if (!file.exists()) return@forEach

                zip.putNextEntry(ZipEntry(MEDIA_PREFIX + file.name))
                file.inputStream().use { it.copyTo(zip) }
                zip.closeEntry()
            }
        }
        memories.size
    }

    override suspend fun import(source: String): Int = withContext(Dispatchers.IO) {
        val input = resolver.openInputStream(Uri.parse(source))
            ?: error("Не удалось открыть $source")

        ZipInputStream(input).use { zip ->
            val first = zip.nextEntry
            require(first?.name == MEMORIES_ENTRY) { "Это не резервная копия Here" }

            val known = memoryDao.observeAll().first().mapTo(mutableSetOf()) { it.memory.createdAt }
            val memories = JSONObject(zip.readBytes().decodeToString())
                .getJSONArray("memories")
                .toMemories()
                .filterNot { it.createdAt in known }

            val needed = memories.flatMapTo(mutableSetOf()) { memory -> memory.media.map { it.uri } }
            val saved = mutableMapOf<String, String>()

            generateSequence { zip.nextEntry }
                .filter { it.name in needed }
                .forEach { entry ->
                    saved[entry.name] = mediaStorage.save(zip, entry.name.substringAfterLast('.', "").ifEmpty { null })
                }

            memories.forEach { memory ->
                val memoryId = memoryDao.insert(memory.toEntity())
                val media = memory.media.mapNotNull { item ->
                    saved[item.uri]?.let { item.copy(uri = it).toEntity(memoryId) }
                }
                if (media.isNotEmpty()) memoryMediaDao.insertAll(media)
            }
            memories.size
        }
    }

    private fun List<Memory>.toJson(): JSONObject = JSONObject()
        .put("version", FORMAT_VERSION)
        .put("memories", JSONArray(map { it.toJson() }))

    private fun Memory.toJson(): JSONObject = JSONObject()
        .put("title", title)
        .put("description", description)
        .put("latitude", latitude)
        .put("longitude", longitude)
        .put("emotion", emotion.name)
        .put("createdAt", createdAt)
        .put("happenedAt", happenedAt)
        .put("media", JSONArray(media.map { item ->
            JSONObject()
                .put("file", MEDIA_PREFIX + File(item.uri).name)
                .put("type", item.type.name)
        }))

    private fun JSONArray.toMemories(): List<Memory> = List(length()) { index ->
        val json = getJSONObject(index)
        val media = json.getJSONArray("media")

        Memory(
            title = json.getString("title"),
            description = json.getString("description"),
            latitude = json.getDouble("latitude"),
            longitude = json.getDouble("longitude"),
            emotion = Emotion.valueOf(json.getString("emotion")),
            createdAt = json.getLong("createdAt"),
            happenedAt = json.getLong("happenedAt"),
            media = List(media.length()) { mediaIndex ->
                val item = media.getJSONObject(mediaIndex)
                MemoryMedia(
                    uri = item.getString("file"),
                    type = MediaType.valueOf(item.getString("type"))
                )
            }
        )
    }
}
