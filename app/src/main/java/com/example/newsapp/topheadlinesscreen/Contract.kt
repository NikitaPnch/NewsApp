package com.example.newsapp.topheadlinesscreen

import com.example.newsapp.base.Event
import com.example.newsapp.topheadlinesscreen.model.ArticleModel

data class ViewState(
    val status: STATUS,
    val articleList: List<ArticleModel>,
    val errorMessage: String
)

sealed class UiEvent : Event {
    object OnRefreshNews : UiEvent()
    object GetCurrentNews : UiEvent()
    data class OnBookmarkClick(val articleModel: ArticleModel) : UiEvent()
}

sealed class DataEvent : Event {
    object OnLoadData : DataEvent()
    data class SuccessNewsRequest(val listArticleModel: List<ArticleModel>) : DataEvent()
    data class ErrorNewsRequest(val errorMessage: String) : DataEvent()
    data class SuccessCurrentNewsRequest(val listArticleModel: List<ArticleModel>) : DataEvent()
}

enum class STATUS {
    LOAD,
    CONTENT,
    CURRENT_CONTENT,
    ERROR
}