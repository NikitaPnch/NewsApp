package com.example.newsapp.bookmarks.data

import com.example.newsapp.bookmarks.data.local.BookmarksDao
import com.example.newsapp.bookmarks.ui.model.BookmarkModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookmarksRepositoryImpl(private val bookmarksDao: BookmarksDao) : BookmarksRepository {
    override suspend fun saveBookmark(entity: BookmarkModel) = withContext(Dispatchers.IO) {
        bookmarksDao.create(entity.mapToEntityModel())
    }


    override suspend fun updateBookmark(entity: BookmarkModel) = withContext(Dispatchers.IO) {
        bookmarksDao.update(entity.mapToEntityModel())
    }

    override suspend fun deleteBookmark(entity: BookmarkModel) = withContext(Dispatchers.IO) {
        bookmarksDao.delete(entity.mapToEntityModel())
    }

    override suspend fun getAllBookmarks() = withContext(Dispatchers.IO) {
        bookmarksDao.read().let { list ->
            list.map { it.mapToUiModel() }
        }
    }
}