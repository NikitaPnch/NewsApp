package com.example.newsapp.bookmarks.ui

import com.example.newsapp.Item
import com.example.newsapp.R
import com.example.newsapp.bookmarks.ui.model.BookmarkModel
import com.example.newsapp.extensions.createImageRequest
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_top_headlines.*

fun bookmarkAdapterDelegate(
    onClick: (String) -> Unit,
    onClickBookmark: (BookmarkModel) -> Unit
): AdapterDelegate<List<Item>> =
    adapterDelegateLayoutContainer<BookmarkModel, Item>(
        R.layout.item_top_headlines
    ) {

        cl_news_item_container.setOnClickListener {
            onClick(item.url)
        }

        ll_bookmark.setOnClickListener {
            onClickBookmark(item)
        }

        iv_bookmark.setImageResource(R.drawable.ic_baseline_bookmark_24)

        bind {
            tv_header_news.text = item.title
            tv_news_description.text = item.description
            sdv_image_news.createImageRequest(item.urlToImage)
        }
    }
