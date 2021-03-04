package com.example.newsapp.searchscreen.ui

import com.example.newsapp.Item
import com.example.newsapp.databinding.ItemSearchBinding
import com.example.newsapp.extensions.loadImage
import com.example.newsapp.searchscreen.ui.model.SearchModel
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

fun searchAdapterDelegate(
    onClickNews: (String) -> Unit,
): AdapterDelegate<List<Item>> =
    adapterDelegateViewBinding<SearchModel, Item, ItemSearchBinding>(
        viewBinding = { layoutInflater, parent ->
            ItemSearchBinding.inflate(layoutInflater, parent, false)
        }
    ) {
        with(binding) {
            clSearchItemContainer.setOnClickListener {
                onClickNews(item.url)
            }

            bind {
                tvSearchHeader.text = item.title
                tvSearchDescription.text = item.description
                sdvSearchImage.loadImage(item.urlToImage)
            }
        }
    }