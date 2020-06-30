package com.example.newsapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.newsapp.db.repositories.NewsRepository
import com.example.newsapp.extensions.NotificationHelper
import com.example.newsapp.extensions.getLocaleCountry
import com.example.newsapp.extensions.launchIO

class Receiver : BroadcastReceiver() {

    private val newsRepository: NewsRepository = NewsRepository()
    private val notificationHelper by lazy { NotificationHelper() }

    companion object {

        // создает интент для Receiver.kt
        private fun createIntent(context: Context): Intent? {
            val intent = Intent(context, Receiver::class.java)
            intent.action = "action.check.news"
            return intent
        }

        // включает alarm manager с выполнением задачи каждый час
        fun setupAlarmManager(context: Context) {
            val intent = createIntent(context)
            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarm.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR, pendingIntent
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        launchIO {
            newsRepository.checkHaveNewArticles(getLocaleCountry(context.resources)).let {
                if (it) {
                    notificationHelper.showNotification(context)
                }
            }
        }
    }
}