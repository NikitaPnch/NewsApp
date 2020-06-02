package com.example.newsapp.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.newsapp.db.dao.NewsDao
import splitties.init.appCtx

@Database(
    entities = [DBNews::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao

    companion object {
        val INSTANCE by lazy {
            Room.databaseBuilder(appCtx, AppDatabase::class.java, "AppDatabase")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}