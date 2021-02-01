package com.example.newsapp.bookmarks.ui

import com.github.terrakok.cicerone.androidx.FragmentScreen

object BookmarksScreen {
    fun get() = FragmentScreen {
        BookmarksFragment()
    }
}