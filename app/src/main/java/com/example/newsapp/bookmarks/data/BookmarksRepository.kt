package com.example.newsapp.bookmarks.data

import com.example.newsapp.bookmarks.ui.model.BookmarkModel

interface BookmarksRepository {
    suspend fun saveBookmark(entity: BookmarkModel)
    suspend fun updateBookmark(entity: BookmarkModel)
    suspend fun deleteBookmark(entity: BookmarkModel)
    suspend fun getAllBookmarks(): List<BookmarkModel>
}