package com.example.newsapp.searchscreen.ui

import com.example.newsapp.base.BaseViewModel
import com.example.newsapp.base.Event
import com.example.newsapp.extensions.LocaleResolver
import com.example.newsapp.searchscreen.data.SearchInteractor

class SearchScreenViewModel(
    private val searchInteractor: SearchInteractor,
    private val localeResolver: LocaleResolver
) :
    BaseViewModel<ViewState>() {
    override fun initialViewState() =
        ViewState(
            status = STATUS.CONTENT,
            query = "",
            sortedBy = null,
            fromDate = null,
            toDate = null,
            searchList = emptyList(),
            errorMessage = ""
        )

    override suspend fun reduce(event: Event, previousState: ViewState): ViewState? {

        when (event) {
            is UiEvent.OnEditTextChanged -> {
                processDataEvent(DataEvent.OnStartLoad)
                searchInteractor.searchEverything(
                    query = event.query,
                    sortBy = previousState.sortedBy,
                    from = previousState.fromDate,
                    to = previousState.toDate,
                    language = localeResolver.getLocaleLanguage()
                ).fold(
                    {
                        processDataEvent(DataEvent.OnSearchError(event.query, it))
                    },
                    {
                        processDataEvent(DataEvent.OnSearchSuccess(event.query, it))
                    }
                )
            }
            is DataEvent.OnSearchSuccess -> {
                return previousState.copy(
                    status = STATUS.CONTENT,
                    query = event.query,
                    searchList = event.searchList
                )
            }
            is DataEvent.OnSearchError -> {
                return previousState.copy(
                    status = STATUS.ERROR,
                    query = event.query,
                    errorMessage = event.t.localizedMessage ?: ""
                )
            }
            is DataEvent.OnStartLoad -> {
                return previousState.copy(status = STATUS.LOAD)
            }
            is UiEvent.OnClickFiltersClear -> {
                return previousState.copy(sortedBy = null, fromDate = null, toDate = null)
            }
            is UiEvent.OnSortedByChanged -> {
                return previousState.copy(sortedBy = event.sortedBy)
            }
            is UiEvent.OnFromDateChanged -> {
                return previousState.copy(fromDate = event.fromDate)
            }
            is UiEvent.OnToDateChanged -> {
                return previousState.copy(toDate = event.toDate)
            }
            is UiEvent.OnShowResultClick -> {
                processDataEvent(DataEvent.OnStartLoad)
                searchInteractor.searchEverything(
                    previousState.query,
                    previousState.sortedBy,
                    previousState.fromDate,
                    previousState.toDate,
                    localeResolver.getLocaleLanguage()
                )
                    .fold(
                        {
                            processDataEvent(DataEvent.OnSearchError(previousState.query, it))
                        },
                        {
                            processDataEvent(DataEvent.OnSearchSuccess(previousState.query, it))
                        }
                    )
            }
        }

        return null
    }
}