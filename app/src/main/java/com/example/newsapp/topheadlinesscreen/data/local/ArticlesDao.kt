package com.example.newsapp.topheadlinesscreen.data.local

import androidx.room.*
import com.example.newsapp.topheadlinesscreen.data.local.views.ArticleWithBookmark

@Dao
interface ArticlesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(entity: ArticleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setArticles(articles: List<ArticleEntity>)

    @Query("SELECT * FROM ARTICLES_TABLE")
    fun read(): List<ArticleWithBookmark>

    @Query("SELECT * FROM ARTICLES_TABLE")
    fun readLocal(): List<ArticleWithBookmark>

    @Update
    fun update(entity: ArticleEntity)

    @Delete
    fun delete(entity: ArticleEntity)

    @Query("DELETE FROM ARTICLES_TABLE")
    fun nukeTable()

    @Transaction
    fun rewriteAndGetLocal(articles: List<ArticleEntity>): List<ArticleWithBookmark> {
        nukeTable()
        setArticles(articles)
        return readLocal()
    }
}