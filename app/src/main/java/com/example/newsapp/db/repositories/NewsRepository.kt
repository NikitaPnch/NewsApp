package com.example.newsapp.db.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.newsapp.api.API
import com.example.newsapp.api.model.APINews
import com.example.newsapp.api.model.asDatabaseModel
import com.example.newsapp.db.AppDatabase
import com.example.newsapp.db.entities.DBNews
import com.example.newsapp.db.views.NewsWithBookmarks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx3.await
import kotlinx.coroutines.withContext

class NewsRepository {

    private val api by lazy { API() }

    private val newsDao = AppDatabase.INSTANCE.newsDao()

    val newsListLiveData: LiveData<List<NewsWithBookmarks>> = newsDao.queryNews()
    val searchLiveData = MutableLiveData(emptyList<APINews.Article>())

    // перезагрузить список новостей
    private suspend fun updateNews(articles: List<APINews.Article>) {
        newsDao.updateNews(articles.asDatabaseModel())
    }

    // получить текущие новости из бд
    private suspend fun getNews(): List<DBNews> {
        return newsDao.getNews()
    }

    // получает свежие новости из текущей страны
    suspend fun getTopHeadlines(country: String) = withContext(Dispatchers.IO) {
        api.news.getTopHeadlines(country).await().let {
            updateNews(it.articles)
        }
    }

    // ищет любые новости с текущими фильтрами
    suspend fun searchEverything(
        query: String,
        sortBy: String?,
        fromDate: String?,
        toDate: String?,
        country: String
    ) = withContext(Dispatchers.IO) {
        api.news.searchEverything(
            query,
            sortBy,
            fromDate,
            toDate,
            country
        ).await().let {
            withContext(Dispatchers.Main) {
                searchLiveData.value = it.articles
            }
        }
    }

    // проверяет не повилось ли новых новостей
    suspend fun checkHaveNewArticles(country: String) = withContext(Dispatchers.IO) {
        val news = getNews()
        api.news.getTopHeadlines(country).await().let {
            it.articles.firstOrNull()?.url != news.firstOrNull()?.url
        }
    }
}