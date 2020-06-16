package com.example.newsapp.viewmodel

import com.example.newsapp.db.entities.DBNews

interface MainActions : Action {
    class GetNews(val country: String) : MainActions
    class SearchNews : MainActions
    class SetQuery(val query: String) : MainActions
    class SetFromDate(val fromDate: String) : MainActions
    class SetToDate(val toDate: String) : MainActions
    class SetSortBy(val sortBy: String) : MainActions
    class AddArticleToBookmarks(val dbNews: DBNews) : MainActions
    class RemoveArticleFromBookmarks(val url: String) : MainActions
}