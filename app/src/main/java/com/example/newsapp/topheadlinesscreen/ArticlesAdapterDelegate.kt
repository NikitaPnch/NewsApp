package com.example.newsapp.topheadlinesscreen

import com.example.newsapp.Item
import com.example.newsapp.R
import com.example.newsapp.databinding.ItemTopHeadlinesBinding
import com.example.newsapp.extensions.createImageRequest
import com.example.newsapp.topheadlinesscreen.model.ArticleModel
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

fun articleAdapterDelegate(
    onClickNews: (String) -> Unit,
    onClickBookmark: (ArticleModel) -> Unit
): AdapterDelegate<List<Item>> =
    adapterDelegateViewBinding<ArticleModel, Item, ItemTopHeadlinesBinding>(
        viewBinding = { layoutInflater, parent ->
            ItemTopHeadlinesBinding.inflate(layoutInflater, parent, false)
        }
    ) {
        with(binding) {
            clNewsItemContainer.setOnClickListener {
                onClickNews(item.url)
            }

            llBookmark.setOnClickListener {
                onClickBookmark(item)
            }

            bind {
                if (item.isBookmarked) {
                    ivBookmark.setImageResource(R.drawable.ic_baseline_bookmark_24)
                } else {
                    ivBookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
                }
                tvHeaderNews.text = item.title
                tvNewsDescription.text = item.description
                sdvImageNews.createImageRequest(item.urlToImage)
            }
        }
    }