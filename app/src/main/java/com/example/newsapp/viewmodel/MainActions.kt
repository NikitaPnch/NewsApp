package com.example.newsapp.viewmodel

interface MainActions : Action {
    class GetNews(val country: String) : MainActions
    class SearchNews : MainActions
}