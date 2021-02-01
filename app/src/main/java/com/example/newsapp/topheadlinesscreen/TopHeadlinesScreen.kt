package com.example.newsapp.topheadlinesscreen

import com.github.terrakok.cicerone.androidx.FragmentScreen

object TopHeadlinesScreen {
    fun get() = FragmentScreen {
        TopHeadlinesFragment()
    }
}