package com.example.newsapp.db.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.newsapp.api.model.APINews
import com.example.newsapp.api.model.asDatabaseModel
import com.example.newsapp.api.requests.News
import com.example.newsapp.db.dao.NewsDao
import com.example.newsapp.db.entities.DBNews
import com.example.newsapp.db.views.NewsWithBookmarks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import timber.log.Timber

class NewsRepository(private val api: News, private val newsDao: NewsDao) {

    val newsListLiveData: LiveData<List<NewsWithBookmarks>> = newsDao.queryNews()
    val searchLiveData = MutableLiveData(emptyList<APINews.Article>())

    // перезагрузить список новостей
    suspend fun updateNews(articles: List<APINews.Article>) {
        newsDao.updateNews(articles.asDatabaseModel())
    }

    // перезагрузить список новостей
    suspend fun getNews(): List<DBNews> {
        return newsDao.getNews()
    }

    suspend fun getTopHeadlines(country: String) = withContext(Dispatchers.IO) {
        api.getTopHeadlinesAsync(country).await().let {
            Timber.d("debug: пошел запрос")
            updateNews(it.articles)
        }
    }

    suspend fun searchEverything(
        country: String,
        query: String,
        sortBy: String?,
        fromDate: String?,
        toDate: String?
    ) = withContext(Dispatchers.IO) {
        api.searchEverythingAsync(
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
}