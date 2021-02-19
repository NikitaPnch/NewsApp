package com.example.newsapp.topheadlinesscreen.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newsapp.topheadlinesscreen.di.ARTICLES_TABLE

@Entity(tableName = ARTICLES_TABLE)
open class ArticleEntity(
    @PrimaryKey
    @ColumnInfo(name = "url")
    var url: String = "",
    @ColumnInfo(name = "title")
    var title: String = "",
    @ColumnInfo(name = "description")
    var description: String = "",
    @ColumnInfo(name = "urlToImage")
    var urlToImage: String = "",
    @ColumnInfo(name = "publishedAt")
    var publishedAt: String = ""
)