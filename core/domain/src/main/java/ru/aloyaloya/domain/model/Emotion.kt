package ru.aloyaloya.domain.model

/**
 * Эмоция, связанная с воспоминанием.
 *
 * Отражает настроение пользователя в момент создания точки на карте.
 * Порядок значений — это порядок плиток в листе выбора эмоции.
 */
enum class Emotion {
    HAPPY,
    TENDER,
    SURPRISED,
    CALM,
    SAD,
    ANGRY
}
