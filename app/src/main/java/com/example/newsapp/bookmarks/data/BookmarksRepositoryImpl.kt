package com.example.newsapp.bookmarks.data

import com.example.newsapp.bookmarks.data.local.BookmarksDao
import com.example.newsapp.bookmarks.ui.model.BookmarkModel

class BookmarksRepositoryImpl(private val bookmarksDao: BookmarksDao) : BookmarksRepository {
    override fun saveBookmark(entity: BookmarkModel) =
        bookmarksDao.create(entity.mapToEntityModel())

    override fun updateBookmark(entity: BookmarkModel) =
        bookmarksDao.update(entity.mapToEntityModel())

    override fun deleteBookmark(entity: BookmarkModel) =
        bookmarksDao.delete(entity.mapToEntityModel())

    override fun getAllBookmarks() = bookmarksDao.read().map { list ->
        list.map { it.mapToUiModel() }
    }
}