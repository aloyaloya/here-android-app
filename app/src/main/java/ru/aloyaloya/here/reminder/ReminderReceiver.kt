package ru.aloyaloya.here.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.aloyaloya.here.HereApplication

/**
 * Показывает напоминание по будильнику и перепланирует его после перезагрузки и смены времени.
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val appComponent = (context.applicationContext as HereApplication).appComponent

        if (intent.action == ACTION_REMIND && appComponent.settingsRepository.reminder.value.enabled) {
            ReminderNotification.show(context)
        }

        appComponent.reminderScheduler.sync()
    }

    companion object {
        const val ACTION_REMIND = "ru.aloyaloya.here.action.REMIND"
    }
}
