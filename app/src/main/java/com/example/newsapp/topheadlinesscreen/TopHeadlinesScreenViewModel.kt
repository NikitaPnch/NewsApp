package com.example.newsapp.topheadlinesscreen

import com.example.newsapp.base.BaseViewModel
import com.example.newsapp.base.Event
import com.example.newsapp.extensions.LocaleResolver
import com.example.newsapp.topheadlinesscreen.data.NewsInteractor
import com.example.newsapp.topheadlinesscreen.data.mapToBookmarkModel

class TopHeadlinesScreenViewModel(
    private val localeResolver: LocaleResolver,
    private val interactor: NewsInteractor
) :
    BaseViewModel<ViewState>() {

    init {
        processDataEvent(UiEvent.OnRefreshNews)
    }

    override fun initialViewState(): ViewState = ViewState(STATUS.LOAD, emptyList(), "")

    override suspend fun reduce(event: Event, previousState: ViewState): ViewState? {
        when (event) {
            is UiEvent.OnBookmarkClick -> {
                val action = if (event.articleModel.isBookmarked) {
                    interactor.deleteBookmark(event.articleModel.mapToBookmarkModel())
                } else {
                    interactor.saveBookmark(event.articleModel.mapToBookmarkModel())
                }
                action.fold(
                    {
                        processDataEvent(DataEvent.ErrorNewsRequest(it.localizedMessage ?: ""))
                    },
                    {
                        processDataEvent(UiEvent.GetCurrentNews)
                    }
                )
            }

            is UiEvent.GetCurrentNews -> {
                interactor.getNews().fold(
                    {
                        processDataEvent(DataEvent.ErrorNewsRequest(it.localizedMessage ?: ""))
                    },
                    {
                        processDataEvent(DataEvent.SuccessCurrentNewsRequest(it))
                    }
                )
            }

            is UiEvent.OnRefreshNews -> {
                processDataEvent(DataEvent.OnLoadData)
                interactor.getTopHeadlines(localeResolver.getLocaleCountry()).fold(
                    {
                        processDataEvent(DataEvent.ErrorNewsRequest(it.localizedMessage ?: ""))
                    },
                    {
                        processDataEvent(DataEvent.SuccessNewsRequest(it))
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

            is DataEvent.SuccessCurrentNewsRequest -> {
                return previousState.copy(
                    status = STATUS.CURRENT_CONTENT,
                    articleList = event.listArticleModel
                )
            }

            is DataEvent.ErrorNewsRequest -> {
                return previousState.copy(
                    status = STATUS.ERROR,
                    errorMessage = event.errorMessage
                )
            }
        }

        return null
    }
}