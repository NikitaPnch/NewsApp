package com.example.newsapp.topheadlinesscreen.data

import com.example.newsapp.topheadlinesscreen.data.local.ArticleEntity
import com.example.newsapp.topheadlinesscreen.model.ArticleModel

interface NewsRepository {

    suspend fun getTopHeadlines(locale: String): List<ArticleModel>
    suspend fun getAllNews(): List<ArticleModel>
    suspend fun checkHaveNewArticles(locale: String): ArticleEntity?
}