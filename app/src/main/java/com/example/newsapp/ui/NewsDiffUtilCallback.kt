package com.example.newsapp.ui

import androidx.recyclerview.widget.DiffUtil
import com.example.newsapp.api.model.APINews

class NewsDiffUtilCallback(private val oldList: List<APINews.Article>,
                           private val newList: List<APINews.Article>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.url == newItem.url
    }

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.title == newItem.title && oldItem.description == newItem.description
    }
}