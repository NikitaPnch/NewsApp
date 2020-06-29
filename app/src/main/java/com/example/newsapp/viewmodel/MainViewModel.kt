package com.example.newsapp.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.newsapp.api.API
import com.example.newsapp.api.model.APINews
import com.example.newsapp.db.entities.DBNews
import com.example.newsapp.db.repositories.BookmarkRepository
import com.example.newsapp.db.repositories.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext

class MainViewModel : BaseViewModel() {

    private val api by lazy { API() }
    private val newsRepository: NewsRepository = NewsRepository()
    private val bookmarkRepository: BookmarkRepository = BookmarkRepository()

    var topHeadlinesLiveData = newsRepository.newsListLiveData
    var bookmarksLiveData = bookmarkRepository.bookmarkListLiveData

    val searchLiveData = MutableLiveData(emptyList<APINews.Article>())
    val isLoading = MutableLiveData(false)
    val isLoadingSearch = MutableLiveData(false)

    private var query: String = ""
    val fromDate: MutableLiveData<String> = MutableLiveData()
    val toDate: MutableLiveData<String> = MutableLiveData()
    private val sortBy: MutableLiveData<String> = MutableLiveData()

    override suspend fun listen(action: Action) {
        super.listen(action)

        when (action) {
            is MainActions.GetNews -> getTopHeadlines(action.country)
            is MainActions.SearchNews -> searchEverything(action.country)
            is MainActions.SetQuery -> setQuery(action.query)
            is MainActions.SetFromDate -> setFromDate(action.fromDate)
            is MainActions.SetToDate -> setToDate(action.toDate)
            is MainActions.SetSortBy -> setSortBy(action.sortBy)
            is MainActions.ClearAllFilters -> setDefaultFilters()
            is MainActions.AddArticleToBookmarks -> addArticleToBookmarks(action.dbNews)
            is MainActions.RemoveArticleFromBookmarks -> removeArticleFromBookmarks(action.url)
        }
    }

    // получает свежие новости из текущей страны
    private suspend fun getTopHeadlines(country: String) {
        isLoading.value = true
        withContext(Dispatchers.IO) {
            api.news.getTopHeadlines(country).await().let {
                newsRepository.updateNews(it.articles)
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                }
            }
        }
    }

    // ищет любые новости с текущими фильтрами
    private suspend fun searchEverything(country: String) {
        isLoadingSearch.value = true
        withContext(Dispatchers.IO) {
            api.news.searchEverything(
                query,
                sortBy.value,
                fromDate.value,
                toDate.value,
                country
            ).await().let {
                withContext(Dispatchers.Main) {
                    searchLiveData.value = it.articles
                    isLoadingSearch.value = false
                }
            }
        }
    }

    // устанавливает значение поиска
    private fun setQuery(text: String) {
        query = text
    }

    // устанавливает даты поиска от определенного числа
    private fun setFromDate(date: String) {
        fromDate.value = date
    }

    // устанавливает даты поиска до определенного числа
    private fun setToDate(date: String) {
        toDate.value = date
    }

    // устанавливает как нужно отсортировать новости новости
    private fun setSortBy(sort: String) {
        sortBy.value = sort
    }

    // устанавливает переменные с фильтрами по умолчанию
    private fun setDefaultFilters() {
        sortBy.value = API.PUBLISHED_AT
        fromDate.value = null
        toDate.value = null
    }

    // добавляет в таблицу новость-закладку
    private suspend fun addArticleToBookmarks(dbNews: DBNews) =
        withContext(Dispatchers.IO) {
            bookmarkRepository.insertBookmark(dbNews)
        }

    // убирает из таблицы новость-закладку
    private suspend fun removeArticleFromBookmarks(url: String) =
        withContext(Dispatchers.IO) {
            bookmarkRepository.deleteBookmarkByUrl(url)
        }
}