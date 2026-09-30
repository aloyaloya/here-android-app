package ru.aloyaloya.here.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import ru.aloyaloya.domain.repository.SettingsRepository
import ru.aloyaloya.domain.scheduler.ReminderScheduler
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Реализация [ReminderScheduler] на неточном будильнике [AlarmManager].
 */
class ReminderSchedulerImpl @Inject constructor(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) : ReminderScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun sync() {
        val reminder = settingsRepository.reminder.value
        val alarm = alarmIntent()

        if (!reminder.enabled) {
            alarmManager.cancel(alarm)
            return
        }

        alarmManager.setWindow(
            AlarmManager.RTC_WAKEUP,
            nextTriggerMillis(reminder.time),
            WINDOW_MILLIS,
            alarm
        )
    }

    private fun alarmIntent(): PendingIntent = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE,
        Intent(context, ReminderReceiver::class.java).setAction(ReminderReceiver.ACTION_REMIND),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun nextTriggerMillis(time: LocalTime): Long {
        val now = ZonedDateTime.now()
        val today = now.with(time)
        val next = if (today.isAfter(now)) today else today.plusDays(1)

        return next.toInstant().toEpochMilli()
    }

    private companion object {
        const val REQUEST_CODE = 0
        val WINDOW_MILLIS = TimeUnit.MINUTES.toMillis(10)
    }
}
