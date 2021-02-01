package com.example.newsapp.topheadlinesscreen.data.local.views

import androidx.room.Relation
import com.example.newsapp.bookmarks.data.local.BookmarkEntity
import com.example.newsapp.topheadlinesscreen.data.local.ArticleEntity

class ArticleWithBookmark : ArticleEntity() {

    @Relation(parentColumn = "url", entityColumn = "url", entity = BookmarkEntity::class)
    var bookmarks: List<BookmarkEntity>? = null
    val isBookmarked get() = bookmarks?.isNotEmpty() ?: false
}