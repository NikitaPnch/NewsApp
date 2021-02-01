package com.example.newsapp.searchscreen.data.remote

import com.example.newsapp.Constants
import com.example.newsapp.topheadlinesscreen.data.remote.model.ArticleListRemoteModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    /** Поиск по новостям
     * @param q строка запроса
     * @param sortBy сортировка результатов
     * @param from поиск от конкретной даты
     * @param to поиск по конкретную даты
     * @param language на каком языке нужно найти новости, две буквы по стандарту ISO-639-1
     * @param pageSize кол-во результатов на страницу, мин = 20, макс = 100
     */
    @GET("v2/everything")
    fun searchEverything(
        @Query("q") query: String,
        @Query("sortBy") sortBy: String? = Constants.PUBLISHED_AT,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("language") language: String?,
        @Query("pageSize") pageSize: Int? = Constants.PAGE_SIZE
    ): Single<ArticleListRemoteModel>
}