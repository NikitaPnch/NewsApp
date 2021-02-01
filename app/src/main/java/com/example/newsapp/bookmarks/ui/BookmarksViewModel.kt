package com.example.newsapp.bookmarks.ui

import com.example.newsapp.base.BaseViewModel
import com.example.newsapp.base.Event
import com.example.newsapp.bookmarks.data.BookmarksInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BookmarksViewModel(private val interactor: BookmarksInteractor) : BaseViewModel<ViewState>() {

    init {
        processDataEvent(DataEvent.RequestBookmarks)
    }

    override fun initialViewState(): ViewState = ViewState(STATUS.LOAD, emptyList())

    override fun reduce(event: Event, previousState: ViewState): ViewState? {
        when (event) {
            is UiEvent.OnBookmarkDeleteClick -> {
                interactor.deleteBookmark(event.model)
                    .flatMap {
                        interactor.getAllBookmarks()
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            processDataEvent(DataEvent.SuccessBookmarkDelete(it))
                        },
                        {
                            it
                        }
                    )
            }
            is DataEvent.RequestBookmarks,
            is UiEvent.OnRefreshBookmarks -> {
                processDataEvent(DataEvent.OnLoadStarted)
                interactor
                    .getAllBookmarks()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            processDataEvent(DataEvent.SuccessBookmarksRequest(it))
                        },
                        {
                            it
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
        }
        return null
    }
}