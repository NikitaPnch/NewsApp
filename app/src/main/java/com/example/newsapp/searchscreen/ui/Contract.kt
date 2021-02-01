package com.example.newsapp.searchscreen.ui

import com.example.newsapp.base.Event
import com.example.newsapp.searchscreen.ui.model.SearchModel

data class ViewState(
    val status: STATUS,
    val query: String,
    val sortedBy: String?,
    val fromDate: String?,
    val toDate: String?,
    val searchList: List<SearchModel>,
    val errorMessage: String
)

sealed class UiEvent : Event {
    data class OnEditTextChanged(val query: String) : UiEvent()
    data class OnSortedByChanged(val sortedBy: String) : UiEvent()
    data class OnFromDateChanged(val fromDate: String) : UiEvent()
    data class OnToDateChanged(val toDate: String) : UiEvent()
    object OnShowResultClick : UiEvent()
    object OnClickFiltersClear : UiEvent()
}

sealed class DataEvent : Event {
    object OnStartLoad : DataEvent()
    data class OnSearchSuccess(val query: String, val searchList: List<SearchModel>) : DataEvent()
    data class OnSearchError(val query: String, val t: Throwable) : DataEvent()
}

enum class STATUS {
    LOAD,
    CONTENT,
    ERROR
}