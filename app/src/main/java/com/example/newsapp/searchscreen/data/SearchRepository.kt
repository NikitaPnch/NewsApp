package com.example.newsapp.searchscreen.data

import com.example.newsapp.searchscreen.ui.model.SearchModel

interface SearchRepository {
    suspend fun searchEverything(
        query: String,
        sortBy: String?,
        from: String?,
        to: String?,
        language: String?
    ): List<SearchModel>
}