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
import io.reactivex.rxjava3.subjects.PublishSubject

class SearchAdapter(private val busEvent: PublishSubject<Any>) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var articles: List<APINews.Article> = emptyList()
    private lateinit var recyclerView: RecyclerView

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val newsContainer: ConstraintLayout =
            itemView.findViewById(R.id.cl_search_item_container)
        private val articleImage: SimpleDraweeView = itemView.findViewById(R.id.sdv_search_image)
        private val articleHeader: TextView = itemView.findViewById(R.id.tv_search_header)
        private val articleDescription: TextView = itemView.findViewById(R.id.tv_search_description)

        override fun onBind(position: Int) {
            super.onBind(position)
            val article = articles[position]

            article.apply {
                setupArticleImage(articleImage)
                setupArticleHeader(articleHeader)
                setupArticleDescription(articleDescription)
                setupClickNewsContainer(newsContainer)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            inflater.inflate(
                R.layout.item_search, parent, false
            )
        )
    }

    override fun getItemCount(): Int = articles.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    // устанавливает данные
    fun setData(articles: List<APINews.Article>) {
        this.articles = articles
        notifyDataSetChanged()
    }

    // привязывает адаптер к переданному recycler
    fun appendTo(recyclerView: RecyclerView, layoutManager: LinearLayoutManager) {
        this.recyclerView = recyclerView
        this.recyclerView.layoutManager = layoutManager
        this.recyclerView.adapter = this
    }

    // устанавливает изображение новости
    private fun APINews.Article.setupArticleImage(articleImage: SimpleDraweeView) {
        urlToImage?.let {
            articleImage.visibility = View.VISIBLE
            articleImage.createImageRequest(urlToImage)
        } ?: let {
            articleImage.visibility = View.GONE
        }
    }

    // устанавливает заголовок новости
    private fun APINews.Article.setupArticleHeader(articleHeader: TextView) {
        articleHeader.text = title
    }

    // устанавливает описание новости
    private fun APINews.Article.setupArticleDescription(articleDescription: TextView) {
        articleDescription.text = title
    }

    // устанавливает нажатие на новость
    private fun APINews.Article.setupClickNewsContainer(newsContainer: ConstraintLayout) {
        newsContainer.setOnClickListener {
            busEvent.onNext(Events.ArticleClickEvent(url))
        }
    }
}