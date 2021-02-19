package com.example.newsapp

import android.app.Application
import com.example.newsapp.bookmarks.di.bookmarksModule
import com.example.newsapp.extensions.NotificationHelper
import com.example.newsapp.extensions.getImagePipelineConfig
import com.example.newsapp.searchscreen.di.searchScreenModule
import com.example.newsapp.topheadlinesscreen.di.mainScreenModule
import com.facebook.drawee.backends.pipeline.Fresco
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class App : Application() {

    val notificationHelper by inject<NotificationHelper>()

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this, getImagePipelineConfig(this))
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
        notificationHelper.createNotificationChannel(this)
    }
}