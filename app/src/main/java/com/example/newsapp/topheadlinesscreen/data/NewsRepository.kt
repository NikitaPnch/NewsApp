package com.example.newsapp.topheadlinesscreen.data

import com.example.newsapp.topheadlinesscreen.data.local.ArticleEntity
import com.example.newsapp.topheadlinesscreen.model.ArticleModel
import io.reactivex.Single

interface NewsRepository {

    fun getTopHeadlines(locale: String): Single<List<ArticleModel>>
    fun getAllNews(): Single<List<ArticleModel>>
    fun checkHaveNewArticles(locale: String): Single<ArticleEntity?>
}