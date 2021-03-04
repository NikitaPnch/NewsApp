package com.example.newsapp.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import com.example.newsapp.MainActivity
import com.example.newsapp.R
import java.util.*

class NotificationHelper {

    companion object {
        const val LOW = "LOW"
        const val HIGH = "HIGH"
        const val SHOW_NEWS = "SHOW_NEWS"

        private const val PUSH_SERVICE_ID = 100
        private const val PUSH_ID = 101
    }

    // показывает уведомление
    fun showNotification(context: Context, title: String, text: String, url: String) {
        val contentIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java).apply {
                putExtra(SHOW_NEWS, url)
            }, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val mBuilder = NotificationCompat.Builder(
            context,
            HIGH
        )
            .setSmallIcon(R.drawable.ic_stat_news)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(contentIntent)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
            .setAutoCancel(true)
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(PUSH_ID, mBuilder.build())
    }

    fun createForegroundInfo(context: Context): ForegroundInfo {
        val intent =
            WorkManager.getInstance(context).createCancelPendingIntent(UUID.randomUUID())
        val notification =
            NotificationCompat.Builder(context, LOW)
                .setContentTitle(context.getString(R.string.refresh_news))
                .setTicker(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.loading))
                .setSmallIcon(R.drawable.ic_stat_news)
                .setOngoing(true)
                .setProgress(0, 0, true)
                .addAction(
                    android.R.drawable.ic_delete,
                    context.getString(R.string.cancel),
                    intent
                )
                .build()
        return ForegroundInfo(PUSH_SERVICE_ID, notification)
    }

    // создает канал уведомлений для для Android выше 7.0
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(HIGH, name, importance)
            mChannel.apply {
                description = descriptionText
                setImportance(NotificationManager.IMPORTANCE_HIGH)
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager =
                context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    fun createLowPriorityNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.service_channel_name)
            val descriptionText = context.getString(R.string.service_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(LOW, name, importance)
            mChannel.apply {
                description = descriptionText
                setImportance(NotificationManager.IMPORTANCE_LOW)
            }
            val notificationManager =
                context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}