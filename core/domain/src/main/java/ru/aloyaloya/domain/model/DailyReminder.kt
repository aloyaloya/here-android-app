package ru.aloyaloya.domain.model

import java.time.LocalTime

/**
 * Ежедневное напоминание записать день.
 *
 * @property enabled Включено ли напоминание.
 * @property time Время напоминания.
 */
data class DailyReminder(
    val enabled: Boolean,
    val time: LocalTime
)
