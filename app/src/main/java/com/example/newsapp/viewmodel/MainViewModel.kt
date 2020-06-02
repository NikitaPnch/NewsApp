package com.example.newsapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.api.API
import com.example.newsapp.db.AppDatabase
import com.example.newsapp.db.DBNews
import com.example.newsapp.db.repositories.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import timber.log.Timber

class MainViewModel : ViewModel() {

    private val api by lazy { API() }
    private var totalResults: Int? = null

    private var repository: NewsRepository
    var newsListLiveDatabase: LiveData<List<DBNews>>

    init {
        val newsDao = AppDatabase.INSTANCE.newsDao()
        repository = NewsRepository(newsDao)
        newsListLiveDatabase = repository.newsListLiveData
    }

    fun getNews(country: String) = viewModelScope.launch(Dispatchers.Main) {
        runCatching {
            withContext(Dispatchers.IO) {
                api.news.getTopHeadlines(country).await().let {
                    repository.insertNews(it.articles)
                    withContext(Dispatchers.Main) {
                        totalResults = it.totalResults
                    }
                }
            }
        }.onFailure {
            Timber.tag("Error").e(it)
        }
    }
}