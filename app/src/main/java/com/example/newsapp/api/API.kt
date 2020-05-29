package com.example.newsapp.api

import com.example.newsapp.api.requests.News
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class API {

    companion object {
        private const val BASE_URL = "https://newsapi.org/"
        private const val KEY = "4f26925b85824439a8d15410472beff9"
    }

    private val logging = HttpLoggingInterceptor(
        object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.tag("OkHttp").d(message)
            }
        }
    ).apply {
        setLevel(HttpLoggingInterceptor.Level.BASIC)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addNetworkInterceptor {
            val requestBuilder = it.request().newBuilder()
            requestBuilder.addHeader("x-api-key", KEY)
            it.proceed(requestBuilder.build())
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val news: News by lazy { retrofit.create(News::class.java) }
}