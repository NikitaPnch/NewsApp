package com.example.newsapp.searchscreen.data

import com.example.newsapp.searchscreen.ui.model.SearchModel
import io.reactivex.Single

interface SearchRepository {
    fun searchEverything(
        query: String,
        sortBy: String?,
        from: String?,
        to: String?,
        language: String?
    ): Single<List<SearchModel>>
}