package ru.aloyaloya.memory.model

import ru.aloyaloya.domain.model.Emotion

/**
 * То, с чем открыли форму воспоминания.
 * Создание и редактирование - одна форма, но приходят они с разным.
 */
sealed class MemoryFormArgs {

    /**
     * @property emotion Эмоция, выбранная в листе на карте.
     * @property latitude Широта будущего воспоминания.
     * @property longitude Долгота будущего воспоминания.
     */
    data class New(
        val emotion: Emotion,
        val latitude: Double,
        val longitude: Double
    ) : MemoryFormArgs()

    /**
     * @property memoryId Идентификатор изменяемого воспоминания.
     */
    data class Edit(val memoryId: Long) : MemoryFormArgs()
}
