package com.example.newsapp.searchscreen.data

import com.example.newsapp.searchscreen.data.remote.SearchRemoteSource
import com.example.newsapp.searchscreen.ui.model.SearchModel
import io.reactivex.Single

class SearchRepositoryImpl(private val searchRemoteSource: SearchRemoteSource) : SearchRepository {
    override fun searchEverything(
        query: String,
        sortBy: String?,
        from: String?,
        to: String?,
        language: String?
    ): Single<List<SearchModel>> {
        return searchRemoteSource.searchEverything(query, sortBy, from, to, language).map {
            it.articles.map {
                it.mapToUiModel()
            }
        }
    }
}