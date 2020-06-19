package com.example.newsapp

import android.app.Application
import com.example.newsapp.extensions.appModule
import com.facebook.drawee.backends.pipeline.Fresco
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        Timber.plant(Timber.DebugTree())
        startKoin {
            modules(appModule)
        }
    }
}