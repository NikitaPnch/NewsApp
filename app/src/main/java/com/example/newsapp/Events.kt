package com.example.newsapp

import com.example.newsapp.db.entities.DBNews

interface Events {
    class ArticleClickEvent(val url: String) : Events
    class AddArticleToBookmarks(val dbNews: DBNews) : Events
    class RemoveArticleFromBookmarks(val url: String) : Events
}