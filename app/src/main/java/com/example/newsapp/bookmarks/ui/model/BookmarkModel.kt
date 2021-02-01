package com.example.newsapp.bookmarks.ui.model

import com.example.newsapp.Item

data class BookmarkModel(
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String
) : Item