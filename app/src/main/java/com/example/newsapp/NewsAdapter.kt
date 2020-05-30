package com.example.newsapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    private var articles: List<NewsModel.Article> = emptyList()
    private lateinit var recyclerView: RecyclerView

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val newsImage = itemView.findViewById<SimpleDraweeView>(R.id.sdv_image_news)
        val newsHeader = itemView.findViewById<TextView>(R.id.tv_header_news)
        val newsContent = itemView.findViewById<TextView>(R.id.tv_news_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_news, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = articles.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.newsImage.setImageURI(article.urlToImage)
        holder.newsHeader.text = article.title
        holder.newsContent.text = article.description
    }

    fun setData(articles: List<NewsModel.Article>) {
        this.articles = articles
    }

    fun getData(): List<NewsModel.Article> {
        return articles
    }

    fun appendTo(recyclerView: RecyclerView, parent: Context) {
        this.recyclerView = recyclerView
        this.recyclerView.layoutManager = LinearLayoutManager(parent)
        this.recyclerView.adapter = this
    }
}