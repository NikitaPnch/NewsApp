package com.example.newsapp

interface Events {
    class NewsClickEvent(val url: String): Events
}