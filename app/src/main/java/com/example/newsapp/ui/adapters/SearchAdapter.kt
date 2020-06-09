package com.example.newsapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.Events
import com.example.newsapp.R
import com.example.newsapp.api.model.APINews
import com.example.newsapp.extensions.createImageRequest
import com.facebook.drawee.view.SimpleDraweeView
import io.reactivex.subjects.PublishSubject

class SearchAdapter(private val busEvent: PublishSubject<Any>) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var articles: List<APINews.Article> = emptyList()
    private lateinit var recyclerView: RecyclerView
    private var isLoaderVisible = false

    companion object {
        private const val TYPE_NEWS = 0
        private const val TYPE_LOADING = 1
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val newsContainer: ConstraintLayout =
            itemView.findViewById(R.id.cl_search_item_container)
        private val newsImage: SimpleDraweeView = itemView.findViewById(R.id.sdv_search_image)
        private val newsHeader: TextView = itemView.findViewById(R.id.tv_search_title)
        private val newsContent: TextView = itemView.findViewById(R.id.tv_search_description)

        override fun onBind(position: Int) {
            super.onBind(position)
            val article = articles[position]

            article.urlToImage?.let {
                newsImage.visibility = View.VISIBLE
                newsImage.createImageRequest(article.urlToImage)
            } ?: let {
                newsImage.visibility = View.GONE
            }

            newsHeader.text = article.title
            newsContent.text = article.description
            newsContainer.setOnClickListener {
                busEvent.onNext(
                    Events.NewsClickEvent(
                        article.url
                    )
                )
            }
        }
    }

    inner class ProgressHolder(itemView: View) : BaseViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_NEWS -> ViewHolder(
                inflater.inflate(
                    R.layout.item_search, parent, false
                )
            )
            else -> ProgressHolder(inflater.inflate(R.layout.item_loading, parent, false))
        }
    }

    override fun getItemCount(): Int = articles.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == articles.size - 1) TYPE_LOADING else TYPE_NEWS
        } else TYPE_NEWS
    }

    fun setData(articles: List<APINews.Article>) {
        this.articles = articles
        notifyDataSetChanged()
    }

    fun appendTo(recyclerView: RecyclerView, layoutManager: LinearLayoutManager) {
        this.recyclerView = recyclerView
        this.recyclerView.layoutManager = layoutManager
        this.recyclerView.adapter = this
    }
}