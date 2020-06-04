package com.example.newsapp.db.repositories

import androidx.lifecycle.LiveData
import com.example.newsapp.api.model.APINews
import com.example.newsapp.api.model.asDatabaseModel
import com.example.newsapp.db.AppDatabase
import com.example.newsapp.db.DBNews

class NewsRepository {

    private val newsDao = AppDatabase.INSTANCE.newsDao()

    val newsListLiveData: LiveData<List<DBNews>> = newsDao.queryNews()

    suspend fun insertNews(article: List<APINews.Article>) {
        newsDao.insertNews(article.asDatabaseModel())
    }

    suspend fun getNews(): List<DBNews> {
        return newsDao.getNews()
    }
}