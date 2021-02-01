package com.example.newsapp.searchscreen.ui

import com.example.newsapp.Item
import com.example.newsapp.R
import com.example.newsapp.extensions.createImageRequest
import com.example.newsapp.searchscreen.ui.model.SearchModel
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_search.*

fun searchAdapterDelegate(
    onClickNews: (String) -> Unit,
): AdapterDelegate<List<Item>> =
    adapterDelegateLayoutContainer<SearchModel, Item>(
        R.layout.item_search
    ) {

        cl_search_item_container.setOnClickListener {
            onClickNews(item.url)
        }

        bind {
            tv_search_header.text = item.title
            tv_search_description.text = item.description
            sdv_search_image.createImageRequest(item.urlToImage)
        }
    }