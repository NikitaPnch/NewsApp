package com.example.newsapp.api.requests

import com.example.newsapp.api.API
import com.example.newsapp.api.model.APINews
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface News {

    /** Получает главные заголовки за последнее время
     * @param country передать страну в формате двух букв по стандарту ISO 3166-1
     * @param pageSize кол-во результатов на страницу, мин = 20, макс = 100
     */
    @GET("v2/top-headlines")
    fun getTopHeadlines(
        @Query("country") country: String? = null,
        @Query("pageSize") pageSize: Int? = API.PAGE_SIZE
    ): Single<APINews>

    /** Поиск по новостям
     * @param q строка запроса
     * @param sortBy фильтр результатов
     * @param from поиск от конкретной даты
     * @param to поиск по конкретную даты
     * @param language на каком языке нужно найти новости, две буквы по стандарту ISO-639-1
     * @param pageSize кол-во результатов на страницу, мин = 20, макс = 100
     */
    @GET("v2/everything")
    fun searchEverything(
        @Query("q") query: String,
        @Query("sortBy") sortBy: String? = API.PUBLISHED_AT,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("language") language: String?,
        @Query("pageSize") pageSize: Int? = API.PAGE_SIZE
    ): Single<APINews>
}