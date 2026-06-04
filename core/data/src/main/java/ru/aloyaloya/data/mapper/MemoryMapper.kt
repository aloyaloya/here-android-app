package ru.aloyaloya.data.mapper

import ru.aloyaloya.database.entity.Emotion as EntityEmotion
import ru.aloyaloya.database.entity.MediaType as EntityMediaType
import ru.aloyaloya.database.entity.MemoryEntity
import ru.aloyaloya.database.entity.MemoryMediaEntity
import ru.aloyaloya.database.entity.MemoryWithMedia
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.domain.model.MediaType
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.model.MemoryMedia

/**
 * Маппер между Entity-слоем (Room) и Domain-слоем.
 */
internal object MemoryMapper {

    fun MemoryWithMedia.toDomain(): Memory = Memory(
        id = memory.id,
        title = memory.title,
        description = memory.description,
        latitude = memory.latitude,
        longitude = memory.longitude,
        emotion = memory.emotion.toDomain(),
        createdAt = memory.createdAt,
        media = media.map { it.toDomain() }
    )

    fun Memory.toEntity(): MemoryEntity = MemoryEntity(
        id = id,
        title = title,
        description = description,
        latitude = latitude,
        longitude = longitude,
        emotion = emotion.toEntity(),
        createdAt = createdAt
    )

    fun Memory.toMediaEntities(memoryId: Long): List<MemoryMediaEntity> =
        media.map { it.toEntity(memoryId) }

    private fun MemoryMediaEntity.toDomain(): MemoryMedia = MemoryMedia(
        id = id,
        uri = uri,
        type = type.toDomain()
    )

    private fun MemoryMedia.toEntity(memoryId: Long): MemoryMediaEntity = MemoryMediaEntity(
        id = id,
        memoryId = memoryId,
        uri = uri,
        type = type.toEntity()
    )

    private fun EntityEmotion.toDomain(): Emotion = when (this) {
        EntityEmotion.HAPPY -> Emotion.HAPPY
        EntityEmotion.SAD -> Emotion.SAD
        EntityEmotion.EXCITED -> Emotion.EXCITED
        EntityEmotion.CALM -> Emotion.CALM
        EntityEmotion.ANGRY -> Emotion.ANGRY
        EntityEmotion.SURPRISED -> Emotion.SURPRISED
    }

    private fun Emotion.toEntity(): EntityEmotion = when (this) {
        Emotion.HAPPY -> EntityEmotion.HAPPY
        Emotion.SAD -> EntityEmotion.SAD
        Emotion.EXCITED -> EntityEmotion.EXCITED
        Emotion.CALM -> EntityEmotion.CALM
        Emotion.ANGRY -> EntityEmotion.ANGRY
        Emotion.SURPRISED -> EntityEmotion.SURPRISED
    }

    private fun EntityMediaType.toDomain(): MediaType = when (this) {
        EntityMediaType.PHOTO -> MediaType.PHOTO
        EntityMediaType.VIDEO -> MediaType.VIDEO
    }

    private fun MediaType.toEntity(): EntityMediaType = when (this) {
        MediaType.PHOTO -> EntityMediaType.PHOTO
        MediaType.VIDEO -> EntityMediaType.VIDEO
    }
}
