package com.example.newsapp.bookmarks.data.local

import androidx.room.*
import com.example.newsapp.bookmarks.di.BOOKMARKS_TABLE
import io.reactivex.Single

@Dao
interface BookmarksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(entity: BookmarkEntity): Single<Unit>

    @Query("SELECT * FROM $BOOKMARKS_TABLE")
    fun read(): Single<List<BookmarkEntity>>

    @Update
    fun update(entity: BookmarkEntity): Single<Unit>

    @Delete
    fun delete(entity: BookmarkEntity): Single<Unit>
}