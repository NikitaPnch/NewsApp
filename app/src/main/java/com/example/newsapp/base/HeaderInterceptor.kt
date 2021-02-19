package com.example.newsapp.base

import com.example.newsapp.BuildConfig
import com.example.newsapp.Constants
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder =
            chain.request().newBuilder().addHeader(Constants.HEADER_API_KEY, BuildConfig.API_KEY)
        return chain.proceed(builder.build())
    }
}