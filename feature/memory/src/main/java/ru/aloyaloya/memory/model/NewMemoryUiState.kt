package ru.aloyaloya.memory.model

import ru.aloyaloya.domain.model.Emotion
import java.time.LocalDateTime

/**
 * Состояние UI экрана [ru.aloyaloya.memory.presentation.NewMemoryScreen].
 *
 * Экран — это форма, поэтому состояние одно: то, что пользователь успел заполнить.
 *
 * @property emotion Выбранная эмоция. Приходит из листа на карте, но ее можно сменить.
 * @property happenedAt Когда событие произошло. По умолчанию — момент открытия экрана,
 * дату и время можно поменять. В миллисекунды переводится при сохранении.
 * @property title Заголовок воспоминания.
 * @property description Описание воспоминания.
 * @property address Адрес выбранной точки или null, пока он не определён или определить не удалось.
 * @property saving Идет запись в базу.
 * @property saved Воспоминание записано — экран пора закрывать.
 */
data class NewMemoryUiState(
    val happenedAt: LocalDateTime,
    val emotion: Emotion? = null,
    val title: String = "",
    val description: String = "",
    val address: String? = null,
    val saving: Boolean = false,
    val saved: Boolean = false
) {
    /** Сохранять есть что, когда выбрана эмоция и заполнен заголовок. */
    val saveEnabled: Boolean
        get() = emotion != null && title.isNotBlank() && !saving
}
