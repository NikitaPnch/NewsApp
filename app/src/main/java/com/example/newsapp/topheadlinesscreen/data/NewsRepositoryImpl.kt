package com.example.newsapp.topheadlinesscreen.data

import com.example.newsapp.extensions.getTimestampFromString
import com.example.newsapp.topheadlinesscreen.data.local.ArticleEntity
import com.example.newsapp.topheadlinesscreen.data.local.ArticlesDao
import com.example.newsapp.topheadlinesscreen.data.remote.NewsRemoteSource
import com.example.newsapp.topheadlinesscreen.model.ArticleModel
import io.reactivex.Single

class NewsRepositoryImpl(private val remote: NewsRemoteSource, private val local: ArticlesDao) :
    NewsRepository {

    override fun getTopHeadlines(locale: String): Single<List<ArticleModel>> =
        remote.getTopHeadlines(locale).map { list ->
            list.articles.map { model ->
                model.mapToEntityModel()
            }
        }.map {
            local.rewriteAndGetLocal(it)
        }.map { list ->
            list.map {
                it.mapToUiModel()
            }
        }

    override fun getAllNews(): Single<List<ArticleModel>> = local.read().map { list ->
        list.map {
            it.mapToUiModel()
        }
    }

    override fun checkHaveNewArticles(locale: String): Single<ArticleEntity?> {
        return remote.getTopHeadlines(locale).map {
            val sortedNetworkArticles =
                it.articles.sortedBy { getTimestampFromString(it.publishedAt) }
            val sortedLocalArticles =
                local.readLocal().sortedBy { getTimestampFromString(it.publishedAt) }
            if (sortedNetworkArticles.firstOrNull()
                    ?.mapToEntityModel()?.url != sortedLocalArticles.firstOrNull()?.url
            ) {
                sortedNetworkArticles.random().mapToEntityModel()
            } else {
                null
            }
        }
    }
}