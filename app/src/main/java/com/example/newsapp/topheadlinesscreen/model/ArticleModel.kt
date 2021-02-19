package com.example.newsapp.topheadlinesscreen.model

import com.example.newsapp.Item

data class ArticleModel(
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val isBookmarked: Boolean
) : Item