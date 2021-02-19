package com.example.newsapp

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newsapp.bookmarks.data.local.BookmarkEntity
import com.example.newsapp.bookmarks.data.local.BookmarksDao
import com.example.newsapp.topheadlinesscreen.data.local.ArticleEntity
import com.example.newsapp.topheadlinesscreen.data.local.ArticlesDao

@Database(entities = [BookmarkEntity::class, ArticleEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookmarksDao(): BookmarksDao
    abstract fun articlesDao(): ArticlesDao
}