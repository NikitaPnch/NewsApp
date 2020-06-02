package com.example.newsapp.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class DBNews(

    @PrimaryKey
    val url: String = "",
    @Embedded
    val source: Source = Source("", ""),
    val author: String? = "",
    val title: String? = "",
    val description: String? = "",
    val urlToImage: String? = null,
    val publishedAt: String = "",
    val content: String? = ""
) {
    data class Source(
        val id: String?,
        val name: String?
    )
}