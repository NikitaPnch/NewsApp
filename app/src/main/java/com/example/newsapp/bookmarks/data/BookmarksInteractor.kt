package com.example.newsapp.bookmarks.data

import com.example.newsapp.bookmarks.ui.model.BookmarkModel

class BookmarksInteractor(private val repository: BookmarksRepository) {

    fun getAllBookmarks() = repository.getAllBookmarks()

    fun deleteBookmark(bookmark: BookmarkModel) = repository.deleteBookmark(bookmark)
}