package com.example.newsapp.topheadlinesscreen.data.remote

class NewsRemoteSource(private val newsApi: NewsApi) {
    suspend fun getTopHeadlines(locale: String) = newsApi.getTopHeadlines(locale)
}