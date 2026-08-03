package ru.aloyaloya.memory.model

import ru.aloyaloya.domain.model.Emotion

/**
 * Состояние UI экрана [ru.aloyaloya.memory.presentation.NewMemoryScreen].
 *
 * Экран — это форма, поэтому состояние одно: то, что пользователь успел заполнить.
 *
 * @property emotion Выбранная эмоция. Приходит из листа на карте, но ее можно сменить.
 * @property title Заголовок воспоминания.
 * @property description Описание воспоминания.
 * @property address Адрес выбранной точки.
 * @property saving Идет запись в базу.
 * @property saved Воспоминание записано — экран пора закрывать.
 */
data class NewMemoryUiState(
    val emotion: Emotion? = null,
    val title: String = "",
    val description: String = "",
    val address: String = "",
    val saving: Boolean = false,
    val saved: Boolean = false
) {
    /** Сохранять есть что, когда выбрана эмоция и заполнен заголовок. */
    val saveEnabled: Boolean
        get() = emotion != null && title.isNotBlank() && !saving
}
