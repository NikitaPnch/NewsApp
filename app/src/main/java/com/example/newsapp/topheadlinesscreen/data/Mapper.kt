package com.example.newsapp.topheadlinesscreen.data

import com.example.newsapp.bookmarks.ui.model.BookmarkModel
import com.example.newsapp.topheadlinesscreen.data.local.ArticleEntity
import com.example.newsapp.topheadlinesscreen.data.local.views.ArticleWithBookmark
import com.example.newsapp.topheadlinesscreen.data.remote.model.ArticleRemoteModel
import com.example.newsapp.topheadlinesscreen.model.ArticleModel

fun ArticleModel.mapToBookmarkModel(): BookmarkModel {
    return BookmarkModel(
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt
    )
}

fun ArticleWithBookmark.mapToUiModel(): ArticleModel {
    return ArticleModel(
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        isBookmarked = isBookmarked
    )
}

fun ArticleRemoteModel.mapToEntityModel(): ArticleEntity {
    return ArticleEntity(
        title = title,
        description = description ?: "",
        url = url,
        urlToImage = urlToImage ?: "",
        publishedAt = publishedAt
    )
}