package com.example.newsapp.topheadlinesscreen.data

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
            local.setArticles(it)
        }.map {
            local.readLocal()
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
}