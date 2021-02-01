package com.example.newsapp.topheadlinesscreen

import com.example.newsapp.Item
import com.example.newsapp.R
import com.example.newsapp.extensions.createImageRequest
import com.example.newsapp.topheadlinesscreen.model.ArticleModel
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_top_headlines.*


fun articleAdapterDelegate(
    onClickNews: (String) -> Unit,
    onClickBookmark: (ArticleModel) -> Unit
): AdapterDelegate<List<Item>> =
    adapterDelegateLayoutContainer<ArticleModel, Item>(
        R.layout.item_top_headlines
    ) {

        cl_news_item_container.setOnClickListener {
            onClickNews(item.url)
        }

        ll_bookmark.setOnClickListener {
            onClickBookmark(item)
        }

        bind {
            if (item.isBookmarked) {
                iv_bookmark.setImageResource(R.drawable.ic_baseline_bookmark_24)
            } else {
                iv_bookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
            }
            tv_header_news.text = item.title
            tv_news_description.text = item.description
            sdv_image_news.createImageRequest(item.urlToImage)
        }
    }