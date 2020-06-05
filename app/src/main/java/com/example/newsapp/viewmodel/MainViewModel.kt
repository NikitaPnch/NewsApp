package com.example.newsapp.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.newsapp.api.API
import com.example.newsapp.db.repositories.NewsRepository
import com.example.newsapp.ui.MainActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext

class MainViewModel : BaseViewModel() {

    private val api by lazy { API() }
    private var totalResults: Int? = null
    private var repository: NewsRepository = NewsRepository()
    var newsListLiveData = repository.newsListLiveData
    val isLoading = MutableLiveData(false)

    override suspend fun listen(action: Action) {
        super.listen(action)

        when (action) {
            is MainActions.GetNews -> getNews(action.country)
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
}