package com.example.newsapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import org.koin.core.component.KoinApiExtension

class NewsBroadcastReceiver : BroadcastReceiver() {

    private lateinit var workManager: WorkManager

    companion object {

        // создает интент для Receiver.kt
        private fun createIntent(context: Context): Intent {
            return Intent(context, NewsBroadcastReceiver::class.java).apply {
                action = "action.check.news"
            }
        }

        // включает alarm manager с выполнением задачи каждый час
        fun setupAlarmManager(context: Context) {
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarm.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + AlarmManager.INTERVAL_HALF_HOUR,
                AlarmManager.INTERVAL_HOUR,
                PendingIntent.getBroadcast(
                    context,
                    0,
                    createIntent(context),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }
    }

    @KoinApiExtension
    override fun onReceive(context: Context, intent: Intent) {
        if (!this::workManager.isInitialized) {
            workManager = WorkManager.getInstance(context)
        }
        workManager.enqueue(
            OneTimeWorkRequestBuilder<PushWorkManager>().build()
        )
    }
}