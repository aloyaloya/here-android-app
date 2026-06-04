package ru.aloyaloya.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Сущность медиафайла, прикреплённого к воспоминанию.
 *
 * Хранит путь к файлу во внутреннем хранилище, но не сам файл.
 * При удалении родительского [MemoryEntity] все связанные медиа удаляются каскадно.
 *
 * @property id Уникальный идентификатор, генерируется автоматически.
 * @property memoryId Идентификатор воспоминания, к которому прикреплён файл.
 * @property uri Путь к файлу во внутреннем хранилище приложения.
 * @property type Тип медиафайла: фото или видео.
 */
@Entity(
    tableName = "memory_media",
    foreignKeys = [
        ForeignKey(
            entity = MemoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["memoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("memoryId")]
)
data class MemoryMediaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memoryId: Long,
    val uri: String,
    val type: MediaType
)
