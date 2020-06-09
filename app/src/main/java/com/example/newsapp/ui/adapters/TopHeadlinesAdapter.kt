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
import com.example.newsapp.db.DBNews
import com.example.newsapp.extensions.createImageRequest
import com.facebook.drawee.view.SimpleDraweeView
import io.reactivex.subjects.PublishSubject

class TopHeadlinesAdapter(private val busEvent: PublishSubject<Any>) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var articles: List<DBNews> = emptyList()
    private lateinit var recyclerView: RecyclerView
    private var isLoaderVisible = false

    companion object {
        private const val TYPE_NEWS = 0
        private const val TYPE_LOADING = 1
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val newsContainer: ConstraintLayout =
            itemView.findViewById(R.id.cl_news_item_container)
        private val newsImage: SimpleDraweeView = itemView.findViewById(R.id.sdv_image_news)
        private val newsHeader: TextView = itemView.findViewById(R.id.tv_header_news)
        private val newsContent: TextView = itemView.findViewById(R.id.tv_news_content)
        private val divider: View = itemView.findViewById(R.id.view_item_divider)

        override fun onBind(position: Int) {
            super.onBind(position)
            val article = articles[position]

            if (article.urlToImage.isNullOrBlank()) {
                newsImage.visibility = View.GONE
            } else {
                newsImage.visibility = View.VISIBLE
                newsImage.createImageRequest(article.urlToImage)
            }

            if (articles.last() == article) {
                divider.visibility = View.GONE
            } else {
                divider.visibility = View.VISIBLE
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
                    R.layout.item_top_headlines, parent, false
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

    fun setData(articles: List<DBNews>) {
        this.articles = articles
        notifyDataSetChanged()
    }

    fun appendTo(recyclerView: RecyclerView, layoutManager: LinearLayoutManager) {
        this.recyclerView = recyclerView
        this.recyclerView.layoutManager = layoutManager
        this.recyclerView.adapter = this
    }
}