package com.example.newsapp.searchscreen.ui

import com.github.terrakok.cicerone.androidx.FragmentScreen

object SearchScreen {
    fun get() = FragmentScreen {
        SearchFragment()
    }
}