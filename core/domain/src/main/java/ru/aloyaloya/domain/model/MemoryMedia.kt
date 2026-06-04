package ru.aloyaloya.domain.model

/**
 * Медиафайл, прикреплённый к воспоминанию.
 *
 * @property id Уникальный идентификатор.
 * @property uri Путь к файлу во внутреннем хранилище приложения.
 * @property type Тип медиафайла: фото или видео.
 */
data class MemoryMedia(
    val id: Long = 0,
    val uri: String,
    val type: MediaType
)
