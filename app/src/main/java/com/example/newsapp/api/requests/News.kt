package com.example.newsapp.api.requests

import com.example.newsapp.api.API
import com.example.newsapp.api.model.APINews
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface News {

    @GET("v2/top-headlines")
    fun getTopHeadlines(
        @Query("country") country: String? = null,
        @Query("pageSize") pageSize: Int? = API.PAGE_SIZE
    ): Single<APINews>

    @GET("v2/everything")
    fun searchEverything(
        @Query("q") query: String,
        @Query("sortBy") sortBy: String? = API.PUBLISHED_AT,
        @Query("language") language: String? = "ru",
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("pageSize") pageSize: Int? = API.PAGE_SIZE
    ): Single<APINews>
}