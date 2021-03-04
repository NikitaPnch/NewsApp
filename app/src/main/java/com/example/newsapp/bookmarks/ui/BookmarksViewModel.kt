package com.example.newsapp.bookmarks.ui

import com.example.newsapp.base.BaseViewModel
import com.example.newsapp.base.Event
import com.example.newsapp.bookmarks.data.BookmarksInteractor

class BookmarksViewModel(private val interactor: BookmarksInteractor) : BaseViewModel<ViewState>() {

    override fun initialViewState(): ViewState = ViewState(STATUS.LOAD, emptyList())

    override suspend fun reduce(event: Event, previousState: ViewState): ViewState? {
        when (event) {
            is UiEvent.OnBookmarkDeleteClick -> {
                interactor.deleteBookmark(event.model).fold(
                    {
                        processDataEvent(DataEvent.ErrorBookmarksRequest)
                    },
                    {
                        processDataEvent(UiEvent.OnRefreshBookmarks)
                    }
                )
            }

            is DataEvent.RequestBookmarks,
            is UiEvent.OnRefreshBookmarks -> {
                processDataEvent(DataEvent.OnLoadStarted)
                interactor
                    .getAllBookmarks().fold(
                        {
                            processDataEvent(DataEvent.ErrorBookmarksRequest)
                        },
                        {
                            processDataEvent(DataEvent.SuccessBookmarksRequest(it))
                        }
                    )

            }
            is DataEvent.OnLoadStarted -> {
                return previousState.copy(status = STATUS.LOAD)
            }
            is DataEvent.SuccessBookmarksRequest -> {
                return previousState.copy(
                    status = if (event.listBookmarksModel.isEmpty()) STATUS.EMPTY else STATUS.CONTENT,
                    bookmarksList = event.listBookmarksModel
                )
            }
            is DataEvent.SuccessBookmarkDelete -> {
                return previousState.copy(
                    status = if (event.listBookmarksModel.isEmpty()) STATUS.EMPTY else STATUS.CONTENT,
                    bookmarksList = event.listBookmarksModel
                )
            }
            is DataEvent.ErrorBookmarksRequest -> {
                return previousState.copy(status = STATUS.ERROR)
            }
        }
        return null
    }
}