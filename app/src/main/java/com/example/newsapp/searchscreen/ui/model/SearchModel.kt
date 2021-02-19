package com.example.newsapp.searchscreen.ui.model

import com.example.newsapp.Item

data class SearchModel(
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String
) : Item