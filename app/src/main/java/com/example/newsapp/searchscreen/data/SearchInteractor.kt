package com.example.newsapp.searchscreen.data

import com.example.newsapp.searchscreen.ui.model.SearchModel
import io.reactivex.Single

class SearchInteractor(
    private val searchRepository: SearchRepository
) {
    fun searchEverything(
        query: String,
        sortBy: String?,
        from: String?,
        to: String?,
        language: String?
    ): Single<List<SearchModel>> {
        return searchRepository.searchEverything(query, sortBy, from, to, language)
    }
}