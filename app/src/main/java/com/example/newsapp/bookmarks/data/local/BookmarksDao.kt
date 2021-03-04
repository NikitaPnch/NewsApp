package com.example.newsapp.bookmarks.data.local

import androidx.room.*
import com.example.newsapp.bookmarks.di.BOOKMARKS_TABLE

@Dao
interface BookmarksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(entity: BookmarkEntity)

    @Query("SELECT * FROM $BOOKMARKS_TABLE")
    fun read(): List<BookmarkEntity>

    @Update
    fun update(entity: BookmarkEntity)

    @Delete
    fun delete(entity: BookmarkEntity)
}