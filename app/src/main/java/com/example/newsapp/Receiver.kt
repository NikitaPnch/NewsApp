package com.example.newsapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.newsapp.api.API
import com.example.newsapp.db.repositories.NewsRepository
import com.example.newsapp.extensions.NotificationHelper
import com.example.newsapp.extensions.getLocaleCountry
import com.example.newsapp.extensions.launchIO
import kotlinx.coroutines.rx2.await

class Receiver : BroadcastReceiver() {

    private var newsRepository: NewsRepository = NewsRepository()
    private val api by lazy { API() }
    private val notificationHelper by lazy { NotificationHelper() }

    companion object {

        // создает интент для Receiver.kt
        fun createIntent(context: Context): Intent? {
            val intent = Intent(context, Receiver::class.java)
            intent.action = "action.check.news"
            return intent
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        launchIO {
            val news = newsRepository.getNews()
            api.news.getTopHeadlines(getLocaleCountry(context.resources)).await().let {
                if (it.articles.firstOrNull()?.url != news.firstOrNull()?.url) {
                    notificationHelper.showNotification(context)
                }
            }
        }
    }
}