package com.example.newsapp.db.repositories

import androidx.lifecycle.LiveData
import com.example.newsapp.api.model.APINews
import com.example.newsapp.api.model.asDatabaseModel
import com.example.newsapp.db.AppDatabase
import com.example.newsapp.db.entities.DBNews
import com.example.newsapp.db.views.NewsWithBookmarks

class NewsRepository {

    private val newsDao = AppDatabase.INSTANCE.newsDao()

    val newsListLiveData: LiveData<List<NewsWithBookmarks>> = newsDao.queryNews()

    // перезагрузить список новостей
    suspend fun updateNews(articles: List<APINews.Article>) {
        newsDao.updateNews(articles.asDatabaseModel())
    }

    // перезагрузить список новостей
    suspend fun getNews(): List<DBNews> {
        return newsDao.getNews()
    }
}