package com.example.newsapp.topheadlinesscreen.data.remote

import com.example.newsapp.Constants
import com.example.newsapp.topheadlinesscreen.data.remote.model.ArticleListRemoteModel
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    /** Получает главные заголовки за последнее время
     * @param country передать страну в формате двух букв по стандарту ISO 3166-1
     * @param pageSize кол-во результатов на страницу, мин = 20, макс = 100
     */
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String? = null,
        @Query("pageSize") pageSize: Int? = Constants.PAGE_SIZE
    ): ArticleListRemoteModel
}