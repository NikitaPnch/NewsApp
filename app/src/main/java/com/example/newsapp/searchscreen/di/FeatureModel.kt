package com.example.newsapp.searchscreen.di

import com.example.newsapp.searchscreen.data.SearchInteractor
import com.example.newsapp.searchscreen.data.SearchRepository
import com.example.newsapp.searchscreen.data.SearchRepositoryImpl
import com.example.newsapp.searchscreen.data.remote.SearchApi
import com.example.newsapp.searchscreen.data.remote.SearchRemoteSource
import com.example.newsapp.searchscreen.ui.SearchScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val searchScreenModule = module {

    single<SearchApi> {
        get<Retrofit>().create(SearchApi::class.java)
    }

    single<SearchRemoteSource> {
        SearchRemoteSource(get())
    }

    single<SearchRepository> {
        SearchRepositoryImpl(get())
    }

    single<SearchInteractor> {
        SearchInteractor(get())
    }

    viewModel<SearchScreenViewModel> {
        SearchScreenViewModel(get(), get())
    }
}