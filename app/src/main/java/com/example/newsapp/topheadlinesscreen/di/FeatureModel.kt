package com.example.newsapp.topheadlinesscreen.di

import com.example.newsapp.topheadlinesscreen.TopHeadlinesScreenViewModel
import com.example.newsapp.topheadlinesscreen.data.NewsInteractor
import com.example.newsapp.topheadlinesscreen.data.NewsRepository
import com.example.newsapp.topheadlinesscreen.data.NewsRepositoryImpl
import com.example.newsapp.topheadlinesscreen.data.remote.NewsApi
import com.example.newsapp.topheadlinesscreen.data.remote.NewsRemoteSource
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

const val ARTICLES_TABLE = "ARTICLES_TABLE"

val mainScreenModule = module {

    single<NewsApi> {
        get<Retrofit>().create(NewsApi::class.java)
    }

    single<NewsRemoteSource> {
        NewsRemoteSource(get())
    }

    single<NewsRepository> {
        NewsRepositoryImpl(get(), get())
    }

    single<NewsInteractor> {
        NewsInteractor(get(), get())
    }

    viewModel<TopHeadlinesScreenViewModel> {
        TopHeadlinesScreenViewModel(get(), get())
    }
}