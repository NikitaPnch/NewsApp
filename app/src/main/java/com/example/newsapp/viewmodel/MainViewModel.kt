package com.example.newsapp.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.newsapp.api.API
import com.example.newsapp.api.model.APINews
import com.example.newsapp.db.repositories.NewsRepository
import com.example.newsapp.ui.MainActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext

class MainViewModel : BaseViewModel() {

    private val api by lazy { API() }
    private var totalResults: Int? = null
    private var repository: NewsRepository = NewsRepository()
    var topHeadlinesLiveData = repository.newsListLiveData
    val searchLiveData = MutableLiveData(emptyList<APINews.Article>())
    val isLoading = MutableLiveData(false)
    val isLoadingSearch = MutableLiveData(false)

    override suspend fun listen(action: Action) {
        super.listen(action)

        when (action) {
            is MainActions.GetNews -> getNews(action.country)
            is MainActions.SearchNews -> searchNews(action.query)
        }
    }

    private suspend fun getNews(country: String) {
        isLoading.value = true
        withContext(Dispatchers.IO) {
            api.news.getTopHeadlines(country).await().let {
                repository.updateNews(it.articles)
                withContext(Dispatchers.Main) {
                    totalResults = it.totalResults
                    isLoading.value = false
                }
            }
        }
    }

    private suspend fun searchNews(query: String) {
        isLoadingSearch.value = true
        withContext(Dispatchers.IO) {
            api.news.searchEverything(query).await().let {
                withContext(Dispatchers.Main) {
                    totalResults = it.totalResults
                    searchLiveData.value = it.articles
                    isLoadingSearch.value = false
                }
            }
        }
    }
}