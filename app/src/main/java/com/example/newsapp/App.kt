package com.example.newsapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.newsapp.bookmarks.di.bookmarksModule
import com.example.newsapp.notifications.NotificationHelper
import com.example.newsapp.searchscreen.di.searchScreenModule
import com.example.newsapp.topheadlinesscreen.di.mainScreenModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class App : Application() {

    private val notificationHelper by inject<NotificationHelper>()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidContext(this@App)
            androidLogger(Level.ERROR)
            modules(
                networkModule,
                databaseModule,
                notificationModule,
                mainScreenModule,
                bookmarksModule,
                localeModule,
                searchScreenModule
            )
        }
        with(notificationHelper) {
            createNotificationChannel(this@App)
            createLowPriorityNotificationChannel(this@App)
        }

        // чтобы приложение использовало темную тему в зависимости от системы
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}