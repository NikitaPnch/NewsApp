package com.example.newsapp.viewmodel

interface MainActions : Action {
    class GetNews(val country: String) : MainActions
    class SearchNews : MainActions
    class SetQuery(val query: String) : MainActions
    class SetFromDate(val fromDate: String) : MainActions
    class SetToDate(val toDate: String) : MainActions
    class SetSortBy(val sortBy: String) : MainActions
}