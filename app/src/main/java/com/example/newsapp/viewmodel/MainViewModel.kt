package com.example.newsapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsapp.api.API
import com.example.newsapp.api.model.APINews
import com.example.newsapp.db.entities.DBNews
import com.example.newsapp.db.repositories.BookmarkRepository
import com.example.newsapp.db.repositories.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext

class MainViewModel : BaseViewModel() {

    private val api by lazy { API() }
    private var totalResults: Int? = null
    private var newsRepository: NewsRepository = NewsRepository()
    private var bookmarkRepository: BookmarkRepository = BookmarkRepository()
    var topHeadlinesLiveData = newsRepository.newsListLiveData
    var bookmarksLiveData = bookmarkRepository.bookmarkListLiveData
    val searchLiveData = MutableLiveData(emptyList<APINews.Article>())
    val isLoading = MutableLiveData(false)
    val isLoadingSearch = MutableLiveData(false)

    val fromDate: MutableLiveData<String> = MutableLiveData()
    val toDate: MutableLiveData<String> = MutableLiveData()
    val sortBy: MutableLiveData<String> = MutableLiveData()
    private var query: String = ""

    override suspend fun listen(action: Action) {
        super.listen(action)

        when (action) {
            is MainActions.GetNews -> getNews(action.country)
            is MainActions.SearchNews -> searchNews()
            is MainActions.SetQuery -> setQuery(action.query)
            is MainActions.SetFromDate -> setFromDate(action.fromDate)
            is MainActions.SetToDate -> setToDate(action.toDate)
            is MainActions.SetSortBy -> setSortBy(action.sortBy)
            is MainActions.AddArticleToBookmarks -> addArticleToBookmarks(action.dbNews)
            is MainActions.RemoveArticleFromBookmarks -> removeArticleFromBookmarks(action.url)
        }
    }

    private suspend fun getNews(country: String) {
        isLoading.value = true
        withContext(Dispatchers.IO) {
            api.news.getTopHeadlines(country).await().let {
                newsRepository.updateNews(it.articles)
                withContext(Dispatchers.Main) {
                    totalResults = it.totalResults
                    isLoading.value = false
                }
            }
        }
    }

    private suspend fun searchNews() {
        isLoadingSearch.value = true
        withContext(Dispatchers.IO) {
            api.news.searchEverything(
                query,
                sortBy.value,
                fromDate.value,
                toDate.value
            ).await().let {
                withContext(Dispatchers.Main) {
                    totalResults = it.totalResults
                    searchLiveData.value = it.articles
                    isLoadingSearch.value = false
                }
            }
        }
    }

    private fun setQuery(text: String) {
        query = text
    }

    private fun setFromDate(date: String) {
        fromDate.value = date
    }

    private fun setToDate(date: String) {
        toDate.value = date
    }

    private fun setSortBy(sort: String) {
        sortBy.value = sort
    }

    private suspend fun addArticleToBookmarks(dbNews: DBNews) =
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkRepository.insertBookmark(dbNews)
        }

    private suspend fun removeArticleFromBookmarks(url: String) =
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkRepository.deleteBookmarkByUrl(url)
        }
}