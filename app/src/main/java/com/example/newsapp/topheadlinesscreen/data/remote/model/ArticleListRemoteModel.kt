package com.example.newsapp.topheadlinesscreen.data.remote.model

import com.google.gson.annotations.SerializedName

data class ArticleListRemoteModel(
    @SerializedName("articles")
    val articles: List<ArticleRemoteModel>
)