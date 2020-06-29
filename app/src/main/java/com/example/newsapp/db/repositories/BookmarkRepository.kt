package com.example.newsapp.db.repositories

import androidx.lifecycle.LiveData
import com.example.newsapp.db.AppDatabase
import com.example.newsapp.db.entities.DBBookmark
import com.example.newsapp.db.entities.DBNews

class BookmarkRepository {

    private val bookmarkDao = AppDatabase.INSTANCE.bookmarkDao()

    // хранит записи из таблицы в формате LiveData
    val bookmarkListLiveData: LiveData<List<DBBookmark>> = bookmarkDao.queryBookmarks()

    // вставить закладку в таблицу
    suspend fun insertBookmark(dbNews: DBNews) {
        val dbBookmark = DBBookmark(
            url = dbNews.url,
            urlToImage = dbNews.urlToImage,
            source = DBBookmark.Source(
                id = dbNews.source.id,
                name = dbNews.source.name
            ),
            author = dbNews.author,
            title = dbNews.title,
            description = dbNews.description,
            publishedAt = dbNews.publishedAt,
            content = dbNews.content
        )
        bookmarkDao.insertBookmark(dbBookmark)
    }

    // удалить закладку из таблицы по url
    suspend fun deleteBookmarkByUrl(url: String) {
        bookmarkDao.deleteBookmarkByUrl(url)
    }
}