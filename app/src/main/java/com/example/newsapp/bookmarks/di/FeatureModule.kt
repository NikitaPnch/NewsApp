package com.example.newsapp.bookmarks.di

import com.example.newsapp.bookmarks.data.BookmarksInteractor
import com.example.newsapp.bookmarks.data.BookmarksRepository
import com.example.newsapp.bookmarks.data.BookmarksRepositoryImpl
import com.example.newsapp.bookmarks.ui.BookmarksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

const val BOOKMARKS_TABLE = "BOOKMARKS_TABLE"

val bookmarksModule = module {

    single<BookmarksRepository> {
        BookmarksRepositoryImpl(get())
    }

    single {
        BookmarksInteractor(get())
    }

    viewModel {
        BookmarksViewModel(get())
    }
}