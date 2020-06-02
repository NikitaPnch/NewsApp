package com.example.newsapp.db.repositories

import androidx.lifecycle.LiveData
import com.example.newsapp.api.model.APINews
import com.example.newsapp.db.DBNews
import com.example.newsapp.db.dao.NewsDao

class NewsRepository (private val newsDao: NewsDao) {

    val newsListLiveData: LiveData<List<DBNews>> = newsDao.queryNews()

    suspend fun insertNews(article: List<APINews.Article>) {
        val dbNews = article.map {
            DBNews(
                url = it.url,
                source = DBNews.Source(
                    id = it.source.id,
                    name = it.source.name
                ),
                author = it.author,
                content = it.content,
                title = it.title,
                description = it.description,
                urlToImage = it.urlToImage,
                publishedAt = it.publishedAt
            )
        }

        newsDao.insertNews(dbNews)
    }

    suspend fun getNews(): List<DBNews> {
        return newsDao.getNews()
    }
}