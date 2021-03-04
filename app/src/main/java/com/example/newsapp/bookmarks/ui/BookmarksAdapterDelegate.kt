package com.example.newsapp.bookmarks.ui

import com.example.newsapp.Item
import com.example.newsapp.R
import com.example.newsapp.bookmarks.ui.model.BookmarkModel
import com.example.newsapp.databinding.ItemTopHeadlinesBinding
import com.example.newsapp.extensions.loadImage
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

fun bookmarkAdapterDelegate(
    onClick: (String) -> Unit,
    onClickBookmark: (BookmarkModel) -> Unit
): AdapterDelegate<List<Item>> =
    adapterDelegateViewBinding<BookmarkModel, Item, ItemTopHeadlinesBinding>(
        viewBinding = { layoutInflater, parent ->
            ItemTopHeadlinesBinding.inflate(layoutInflater, parent, false)
        }
    ) {
        with(binding) {
            clNewsItemContainer.setOnClickListener {
                onClick(item.url)
            }

            llBookmark.setOnClickListener {
                onClickBookmark(item)
            }

            ivBookmark.setImageResource(R.drawable.ic_baseline_bookmark_24)

            bind {
                tvHeaderNews.text = item.title
                tvNewsDescription.text = item.description
                sdvImageNews.loadImage(item.urlToImage)
            }
        }
    }
