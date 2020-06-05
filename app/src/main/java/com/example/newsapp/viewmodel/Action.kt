package com.example.newsapp.viewmodel

interface Action {
    class Error(val errorMessage: Throwable) : Action
}