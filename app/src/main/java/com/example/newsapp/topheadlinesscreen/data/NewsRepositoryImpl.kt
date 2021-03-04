package com.example.newsapp.topheadlinesscreen.data

import com.example.newsapp.extensions.getTimestampFromString
import com.example.newsapp.topheadlinesscreen.data.local.ArticleEntity
import com.example.newsapp.topheadlinesscreen.data.local.ArticlesDao
import com.example.newsapp.topheadlinesscreen.data.remote.NewsRemoteSource
import com.example.newsapp.topheadlinesscreen.model.ArticleModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsRepositoryImpl(private val remote: NewsRemoteSource, private val local: ArticlesDao) :
    NewsRepository {

    override suspend fun getTopHeadlines(locale: String): List<ArticleModel> {
        return remote.getTopHeadlines(locale).let { list ->
            list.articles.map { model ->
                model.mapToEntityModel()
            }
        }.let {
            withContext(Dispatchers.IO) {
                local.rewriteAndGetLocal(it)
            }
        }.let { list ->
            list.map {
                it.mapToUiModel()
            }
        }
    }

    override suspend fun getAllNews(): List<ArticleModel> {
        return withContext(Dispatchers.IO) {
            local.read().let { list ->
                list.map {
                    it.mapToUiModel()
                }
            }
        }
    }

    override suspend fun checkHaveNewArticles(locale: String): ArticleEntity? {
        return withContext(Dispatchers.IO) {
            remote.getTopHeadlines(locale).let {
                val sortedNetworkArticles =
                    it.articles.sortedBy { getTimestampFromString(it.publishedAt) }
                val sortedLocalArticles =
                    local.readLocal().sortedBy { getTimestampFromString(it.publishedAt) }
                if (sortedNetworkArticles.firstOrNull()
                        ?.mapToEntityModel()?.url != sortedLocalArticles.firstOrNull()?.url
                ) {
                    local.rewriteAndGetLocal(sortedNetworkArticles.map { it.mapToEntityModel() })
                        .random()
                } else {
                    null
                }
            }
        }
    }
}