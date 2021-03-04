package com.example.newsapp.searchscreen.data.remote

class SearchRemoteSource(private val searchApi: SearchApi) {
    suspend fun searchEverything(
        query: String,
        sortBy: String?,
        from: String?,
        to: String?,
        language: String?
    ) = searchApi.searchEverything(
        query = query,
        sortBy = sortBy,
        from = from,
        to = to,
        language = language
    )
}