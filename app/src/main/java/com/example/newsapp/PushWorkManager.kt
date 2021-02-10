package com.example.newsapp

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.example.newsapp.extensions.LocaleResolver
import com.example.newsapp.extensions.NotificationHelper
import com.example.newsapp.topheadlinesscreen.data.NewsInteractor
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class PushWorkManager(private val appContext: Context, workerParameters: WorkerParameters) :
    RxWorker(appContext, workerParameters), KoinComponent {

    private val newsInteractor by inject<NewsInteractor>()
    private val notificationHelper by inject<NotificationHelper>()
    private val localeResolver by inject<LocaleResolver>()

    override fun createWork(): Single<Result> {
        return newsInteractor.checkHaveNewArticles(localeResolver.getLocaleCountry())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                notificationHelper.showNotification(appContext, it.title, it.description, it.url)
                Result.success()
            }
    }
}