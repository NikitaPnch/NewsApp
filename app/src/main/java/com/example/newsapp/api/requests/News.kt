package com.example.newsapp.api.requests

import com.example.newsapp.NewsModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface News {

    @GET("v2/top-headlines")
    fun getTopHeadlines(
        @Query("country") country: String
    ): Single<NewsModel>
}