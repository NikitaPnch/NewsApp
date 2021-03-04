package com.example.newsapp.bookmarks.data

import com.example.newsapp.bookmarks.ui.model.BookmarkModel
import com.example.newsapp.extensions.Either
import com.example.newsapp.extensions.attempt

class BookmarksInteractor(private val repository: BookmarksRepository) {

    suspend fun getAllBookmarks(): Either<Throwable, List<BookmarkModel>> =
        attempt { repository.getAllBookmarks() }

    suspend fun deleteBookmark(bookmark: BookmarkModel): Either<Throwable, Unit> =
        attempt { repository.deleteBookmark(bookmark) }
}