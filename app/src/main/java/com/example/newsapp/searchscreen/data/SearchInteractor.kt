package com.example.newsapp.searchscreen.data

import com.example.newsapp.extensions.Either
import com.example.newsapp.extensions.attempt
import com.example.newsapp.searchscreen.ui.model.SearchModel

class SearchInteractor(
    private val searchRepository: SearchRepository
) {
    suspend fun searchEverything(
        query: String,
        sortBy: String?,
        from: String?,
        to: String?,
        language: String?
    ): Either<Throwable, List<SearchModel>> {
        return attempt { searchRepository.searchEverything(query, sortBy, from, to, language) }
    }
}