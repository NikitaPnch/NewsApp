package com.example.newsapp.topheadlinesscreen.data

import com.example.newsapp.bookmarks.data.BookmarksRepository
import com.example.newsapp.bookmarks.ui.model.BookmarkModel

class NewsInteractor(
    private val newsRepository: NewsRepository,
    private val bookmarksRepository: BookmarksRepository
) {

    fun getTopHeadlines(locale: String) = newsRepository.getTopHeadlines(locale)
    fun saveBookmark(bookmarkModel: BookmarkModel) = bookmarksRepository.saveBookmark(bookmarkModel)
    fun getAllNews() = newsRepository.getAllNews()
    fun deleteBookmark(bookmarkModel: BookmarkModel) =
        bookmarksRepository.deleteBookmark(bookmarkModel)
}