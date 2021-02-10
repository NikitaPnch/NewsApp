package com.example.newsapp.topheadlinesscreen.data

import com.example.newsapp.bookmarks.data.BookmarksRepository
import com.example.newsapp.bookmarks.ui.model.BookmarkModel
import com.example.newsapp.extensions.getTimestampFromString
import com.example.newsapp.topheadlinesscreen.data.local.ArticleEntity
import com.example.newsapp.topheadlinesscreen.model.ArticleModel
import io.reactivex.Single

class NewsInteractor(
    private val newsRepository: NewsRepository,
    private val bookmarksRepository: BookmarksRepository
) {

    fun getTopHeadlines(locale: String): Single<List<ArticleModel>> {
        return newsRepository.getTopHeadlines(locale).map {
            it.sortedByDescending { getTimestampFromString(it.publishedAt) }
        }
    }

    fun getNews(): Single<List<ArticleModel>> {
        return newsRepository.getAllNews().map {
            it.sortedByDescending { getTimestampFromString(it.publishedAt) }
        }
    }

    fun saveBookmark(bookmarkModel: BookmarkModel) = bookmarksRepository.saveBookmark(bookmarkModel)
    fun deleteBookmark(bookmarkModel: BookmarkModel) =
        bookmarksRepository.deleteBookmark(bookmarkModel)

    fun checkHaveNewArticles(locale: String): Single<ArticleEntity?> {
        return newsRepository.checkHaveNewArticles(locale)
    }
}