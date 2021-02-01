package com.example.newsapp.topheadlinesscreen.data.local

import androidx.room.*
import com.example.newsapp.topheadlinesscreen.data.local.views.ArticleWithBookmark
import com.example.newsapp.topheadlinesscreen.di.ARTICLES_TABLE
import io.reactivex.Single

@Dao
interface ArticlesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(entity: ArticleEntity): Single<Unit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setArticles(articles: List<ArticleEntity>)

    @Query("SELECT * FROM $ARTICLES_TABLE")
    fun read(): Single<List<ArticleWithBookmark>>

    @Query("SELECT * FROM $ARTICLES_TABLE")
    fun readLocal(): List<ArticleWithBookmark>

    @Update
    fun update(entity: ArticleEntity): Single<Unit>

    @Delete
    fun delete(entity: ArticleEntity): Single<Unit>
}