package com.example.newsapp.bookmarks.ui

import com.example.newsapp.base.Event
import com.example.newsapp.bookmarks.ui.model.BookmarkModel

data class ViewState(
    val status: STATUS,
    val bookmarksList: List<BookmarkModel>
)

sealed class UiEvent : Event {
    data class OnBookmarkDeleteClick(val model: BookmarkModel) : UiEvent()
    object OnRefreshBookmarks : UiEvent()
}

sealed class DataEvent : Event {
    object OnLoadStarted : DataEvent()
    object RequestBookmarks : DataEvent()
    data class SuccessBookmarksRequest(val listBookmarksModel: List<BookmarkModel>) : DataEvent()
    data class SuccessBookmarkDelete(val listBookmarksModel: List<BookmarkModel>) : DataEvent()
}

enum class STATUS {
    LOAD,
    CONTENT,
    EMPTY,
    ERROR
}