package com.example.newsapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.db.DBNews

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNews(news: List<DBNews>)

    @Query("SELECT * FROM DBNews")
    fun queryNews(): LiveData<List<DBNews>>

    @Query("SELECT * FROM DBNews")
    fun getNews(): List<DBNews>
}