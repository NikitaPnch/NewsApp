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
}