package ru.aloyaloya.domain.scheduler

/**
 * Планировщик ежедневного напоминания по текущим настройкам.
 */
interface ReminderScheduler {

    fun sync()
}
