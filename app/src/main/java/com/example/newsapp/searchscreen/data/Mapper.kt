package com.example.newsapp.searchscreen.data

import com.example.newsapp.searchscreen.ui.model.SearchModel
import com.example.newsapp.topheadlinesscreen.data.remote.model.ArticleRemoteModel

fun ArticleRemoteModel.mapToUiModel(): SearchModel {
    return SearchModel(title, description ?: "", url, urlToImage ?: "")
}