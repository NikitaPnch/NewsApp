package com.example.newsapp.bookmarks.data

import com.example.newsapp.bookmarks.ui.model.BookmarkModel
import io.reactivex.Single

interface BookmarksRepository {

    fun saveBookmark(entity: BookmarkModel): Single<Unit>

    fun updateBookmark(entity: BookmarkModel): Single<Unit>

    fun deleteBookmark(entity: BookmarkModel): Single<Unit>

    fun getAllBookmarks(): Single<List<BookmarkModel>>
}