package com.example.newsapp.db.views

import androidx.room.Relation
import com.example.newsapp.db.entities.DBBookmark
import com.example.newsapp.db.entities.DBNews

class NewsWithBookmarks : DBNews() {

    @Relation(parentColumn = "url", entityColumn = "url", entity = DBBookmark::class)
    var bookmarks: List<DBBookmark>? = null
    val isBookmarked get() = bookmarks?.isNotEmpty() ?: false
}