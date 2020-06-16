package com.example.newsapp.db.repositories

import androidx.lifecycle.LiveData
import com.example.newsapp.api.model.APINews
import com.example.newsapp.api.model.asDatabaseModel
import com.example.newsapp.db.AppDatabase
import com.example.newsapp.db.views.NewsWithBookmarks

class NewsRepository {

    private val newsDao = AppDatabase.INSTANCE.newsDao()

    val newsListLiveData: LiveData<List<NewsWithBookmarks>> = newsDao.queryNews()

    suspend fun updateNews(article: List<APINews.Article>) {
        newsDao.updateNews(article.asDatabaseModel())
    }
}