package com.example.newsapp.ui

import com.example.newsapp.viewmodel.Action

interface MainActions : Action {
    class GetNews(val country: String) : MainActions
    class SearchNews(val query: String) : MainActions
}