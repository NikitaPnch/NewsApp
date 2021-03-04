package com.example.newsapp.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.newsapp.extensions.LocaleResolver
import com.example.newsapp.topheadlinesscreen.data.NewsInteractor
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class PushWorkManager(appContext: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(appContext, workerParameters), KoinComponent {

    companion object {
        const val TAG = "com.example.newsapp.notifications.PushWorkManager"
    }

    private val newsInteractor by inject<NewsInteractor>()
    private val notificationHelper by inject<NotificationHelper>()
    private val localeResolver by inject<LocaleResolver>()

    override suspend fun doWork(): Result {
        setForegroundAsync(notificationHelper.createForegroundInfo(applicationContext))
        return newsInteractor.checkHaveNewArticles(localeResolver.getLocaleCountry()).fold(
            {
                Result.retry()
            },
            {
                it?.let {
                    notificationHelper.showNotification(
                        applicationContext,
                        it.title,
                        it.description,
                        it.url
                    )
                }
                Result.success()
            }
        )
    }
}