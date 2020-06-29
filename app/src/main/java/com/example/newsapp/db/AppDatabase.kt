package com.example.newsapp.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.newsapp.db.dao.BookmarkDao
import com.example.newsapp.db.dao.NewsDao
import com.example.newsapp.db.entities.DBBookmark
import com.example.newsapp.db.entities.DBNews
import splitties.init.appCtx

@Database(
    entities = [DBNews::class, DBBookmark::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        val INSTANCE by lazy {
            Room.databaseBuilder(appCtx, AppDatabase::class.java, "AppDatabase")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}