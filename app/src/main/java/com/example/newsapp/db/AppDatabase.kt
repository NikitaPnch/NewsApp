package com.example.newsapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newsapp.db.dao.BookmarkDao
import com.example.newsapp.db.dao.NewsDao
import com.example.newsapp.db.entities.DBBookmark
import com.example.newsapp.db.entities.DBNews

@Database(
    entities = [DBNews::class, DBBookmark::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val newsDao: NewsDao
    abstract val bookmarkDao: BookmarkDao
}