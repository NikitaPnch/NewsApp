package com.example.newsapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.api.API
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    private val api by lazy { API() }
    var news = MutableLiveData<NewsModel>()

    fun getNews(country: String) = viewModelScope.launch(Dispatchers.Default) {
        api.news.getTopHeadlines(country)
            .subscribeOn(Schedulers.io())
            .await().let {
                withContext(Dispatchers.Main) {
                    news.value = it
                }
            }
    }
}