package com.example.newsapp.searchscreen.data

import com.example.newsapp.searchscreen.data.remote.SearchRemoteSource
import com.example.newsapp.searchscreen.ui.model.SearchModel

class SearchRepositoryImpl(private val searchRemoteSource: SearchRemoteSource) : SearchRepository {
    override suspend fun searchEverything(
        query: String,
        sortBy: String?,
        from: String?,
        to: String?,
        language: String?
    ): List<SearchModel> {
        return searchRemoteSource.searchEverything(query, sortBy, from, to, language).let {
            it.articles.map {
                it.mapToUiModel()
            }
        }
    }
}