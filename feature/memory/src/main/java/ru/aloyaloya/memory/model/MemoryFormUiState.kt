package ru.aloyaloya.memory.model

import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.domain.model.MemoryMedia
import ru.aloyaloya.mapkit.model.MapPoint
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Состояние UI экрана [ru.aloyaloya.memory.presentation.MemoryFormScreen].
 *
 * @property emotion Выбранная эмоция. Приходит из листа на карте, но ее можно сменить.
 * @property happenedAt Когда событие произошло. По умолчанию — момент открытия экрана,
 * дату и время можно поменять. В миллисекунды переводится при сохранении.
 * @property title Заголовок воспоминания.
 * @property description Описание воспоминания.
 * @property point Точка воспоминания: у нового приходит из маршрута, у существующего
 * читается из базы, поэтому до загрузки ее нет.
 * @property media Прикрепленные снимки: у новых адрес из пикера, у сохраненных путь к файлу.
 * @property address Адрес выбранной точки или null, пока он не определён или определить не удалось.
 * @property emotionByDate Эмоция последнего воспоминания каждого дня: лист выбора даты
 * показывает ее пином в ячейке дня.
 * @property editing Форма открыта на существующем воспоминании.
 * @property saving Идет запись в базу.
 * @property saved Воспоминание записано — экран пора закрывать.
 */
data class MemoryFormUiState(
    val happenedAt: LocalDateTime,
    val emotion: Emotion? = null,
    val title: String = "",
    val description: String = "",
    val point: MapPoint? = null,
    val media: List<MemoryMedia> = emptyList(),
    val address: String? = null,
    val emotionByDate: Map<LocalDate, Emotion> = emptyMap(),
    val editing: Boolean = false,
    val saving: Boolean = false,
    val saved: Boolean = false,
    val activeSheet: MemoryFormSheet? = null
) {
    val saveEnabled: Boolean
        get() = emotion != null && point != null && title.isNotBlank() && !saving
}
