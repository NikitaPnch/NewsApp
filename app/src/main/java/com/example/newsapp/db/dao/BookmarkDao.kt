package com.example.newsapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.db.entities.DBBookmark

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookmark(bookmark: DBBookmark)

    @Query("DELETE FROM DBBookmark WHERE url = :url")
    fun deleteBookmarkByUrl(url: String)

    @Query("SELECT * FROM DBBookmark")
    fun queryBookmarks(): LiveData<List<DBBookmark>>
}