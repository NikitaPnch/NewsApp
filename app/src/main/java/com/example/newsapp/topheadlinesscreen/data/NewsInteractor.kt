package com.example.newsapp.topheadlinesscreen.data

import com.example.newsapp.bookmarks.data.BookmarksRepository
import com.example.newsapp.bookmarks.ui.model.BookmarkModel
import com.example.newsapp.extensions.Either
import com.example.newsapp.extensions.attempt
import com.example.newsapp.extensions.getTimestampFromString
import com.example.newsapp.topheadlinesscreen.data.local.ArticleEntity
import com.example.newsapp.topheadlinesscreen.model.ArticleModel

class NewsInteractor(
    private val newsRepository: NewsRepository,
    private val bookmarksRepository: BookmarksRepository
) {

    suspend fun getTopHeadlines(locale: String): Either<Throwable, List<ArticleModel>> {
        return attempt {
            newsRepository.getTopHeadlines(locale).let {
                it.sortedByDescending { getTimestampFromString(it.publishedAt) }
            }
        }
    }

    suspend fun getNews(): Either<Throwable, List<ArticleModel>> {
        return attempt {
            newsRepository.getAllNews().let {
                it.sortedByDescending { getTimestampFromString(it.publishedAt) }
            }
        }
    }

    suspend fun saveBookmark(bookmarkModel: BookmarkModel): Either<Throwable, Unit> = attempt {
        bookmarksRepository.saveBookmark(bookmarkModel)
    }

    suspend fun deleteBookmark(bookmarkModel: BookmarkModel): Either<Throwable, Unit> = attempt {
        bookmarksRepository.deleteBookmark(bookmarkModel)
    }


    suspend fun checkHaveNewArticles(locale: String): Either<Throwable, ArticleEntity?> {
        return attempt { newsRepository.checkHaveNewArticles(locale) }
    }
}