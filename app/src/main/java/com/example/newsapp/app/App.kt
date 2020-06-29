package com.example.newsapp.app

import android.app.Application
import com.example.newsapp.app.di.*
import com.example.newsapp.extensions.getImagePipelineConfig
import com.facebook.drawee.backends.pipeline.Fresco
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Fresco.initialize(this, getImagePipelineConfig(this))
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidContext(this@App)
            androidLogger(Level.DEBUG)
            modules(
                listOf(
                    viewModelModule,
                    apiModule,
                    netModule,
                    databaseModule,
                    repositoryModule
                )
            )
        }
    }
}