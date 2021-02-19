package com.example.newsapp.bookmarks.data

import com.example.newsapp.bookmarks.data.local.BookmarkEntity
import com.example.newsapp.bookmarks.ui.model.BookmarkModel

fun BookmarkEntity.mapToUiModel(): BookmarkModel {
    return BookmarkModel(title, description, url, urlToImage, publishedAt)
}

fun BookmarkModel.mapToEntityModel(): BookmarkEntity {
    return BookmarkEntity(
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt
    )
}