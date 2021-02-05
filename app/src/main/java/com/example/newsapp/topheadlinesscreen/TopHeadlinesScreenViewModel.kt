package com.example.newsapp.topheadlinesscreen

import com.example.newsapp.base.BaseViewModel
import com.example.newsapp.base.Event
import com.example.newsapp.extensions.LocaleResolver
import com.example.newsapp.topheadlinesscreen.data.NewsInteractor
import com.example.newsapp.topheadlinesscreen.data.mapToBookmarkModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TopHeadlinesScreenViewModel(
    private val localeResolver: LocaleResolver,
    private val interactor: NewsInteractor
) :
    BaseViewModel<ViewState>() {

    init {
        processDataEvent(UiEvent.OnRefreshNews)
    }

    override fun initialViewState(): ViewState = ViewState(STATUS.LOAD, emptyList(), "")

    override fun reduce(event: Event, previousState: ViewState): ViewState? {
        when (event) {
            is UiEvent.OnBookmarkClick -> {
                val action = if (event.articleModel.isBookmarked) {
                    interactor.deleteBookmark(event.articleModel.mapToBookmarkModel())
                } else {
                    interactor.saveBookmark(event.articleModel.mapToBookmarkModel())
                }
                action.flatMap {
                    interactor.getNews()
                }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            processDataEvent(DataEvent.SuccessBookmarkStatusChanged(it))
                        },
                        {
                            processDataEvent(DataEvent.ErrorNewsRequest(it.localizedMessage ?: ""))
                        }
                    )
            }

            is UiEvent.OnRefreshNews -> {
                interactor.getTopHeadlines(localeResolver.getLocaleCountry())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            processDataEvent(DataEvent.SuccessNewsRequest(it))
                        },
                        {
                            processDataEvent(DataEvent.ErrorNewsRequest(it.localizedMessage ?: ""))
                        }
                    )
            }

            is DataEvent.SuccessNewsRequest -> {
                return previousState.copy(
                    status = STATUS.CONTENT,
                    articleList = event.listArticleModel
                )
            }

            is DataEvent.OnLoadData -> {
                return previousState.copy(
                    status = STATUS.LOAD
                )
            }

            is DataEvent.SuccessBookmarkStatusChanged -> {
                return previousState.copy(
                    status = STATUS.CONTENT,
                    articleList = event.listArticleModel
                )
            }

            is DataEvent.ErrorNewsRequest -> {
                return previousState.copy(status = STATUS.ERROR, errorMessage = event.errorMessage)
            }
        }

        return null
    }
}