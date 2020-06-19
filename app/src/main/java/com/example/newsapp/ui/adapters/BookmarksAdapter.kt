package com.example.newsapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.Events
import com.example.newsapp.R
import com.example.newsapp.db.entities.DBBookmark
import com.example.newsapp.extensions.createImageRequest
import com.facebook.drawee.view.SimpleDraweeView
import io.reactivex.rxjava3.subjects.PublishSubject

class BookmarksAdapter(private val busEvent: PublishSubject<Any>) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var articles: List<DBBookmark> = emptyList()
    private lateinit var recyclerView: RecyclerView

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val newsContainer: ConstraintLayout =
            itemView.findViewById(R.id.cl_news_item_container)
        private val newsImage: SimpleDraweeView = itemView.findViewById(R.id.sdv_image_news)
        private val newsHeader: TextView = itemView.findViewById(R.id.tv_header_news)
        private val newsDescription: TextView = itemView.findViewById(R.id.tv_news_description)
        private val bookmarkContainer: LinearLayout = itemView.findViewById(R.id.ll_bookmark)
        private val imageBookmark: ImageView = itemView.findViewById(R.id.iv_bookmark)
        private val divider: View = itemView.findViewById(R.id.view_item_divider)

        override fun onBind(position: Int) {
            super.onBind(position)
            val article = articles[position]

            setupBookmark(imageBookmark)

            article.apply {
                setupArticleImage(newsImage)
                setupArticleDivider(divider)
                setupArticleHeader(newsHeader)
                setupArticleDescription(newsDescription)
                setupClicksBookmarkContainer(bookmarkContainer)
                setupClicksNewsContainer(newsContainer)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            inflater.inflate(
                R.layout.item_top_headlines, parent, false
            )
        )
    }

    override fun getItemCount(): Int = articles.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    // устанавливает данные
    fun setData(articles: List<DBBookmark>) {
        this.articles = articles
        notifyDataSetChanged()
    }

    // привязывает адаптер к переданному recyclerView
    fun appendTo(recyclerView: RecyclerView, layoutManager: LinearLayoutManager) {
        this.recyclerView = recyclerView
        this.recyclerView.layoutManager = layoutManager
        this.recyclerView.adapter = this
    }

    // устанавливает изображение новости
    private fun DBBookmark.setupArticleImage(newsImage: SimpleDraweeView) {
        if (urlToImage.isNullOrBlank()) {
            newsImage.visibility = View.GONE
        } else {
            newsImage.visibility = View.VISIBLE
            newsImage.createImageRequest(urlToImage!!)
        }
    }

    // устанавливает разделитель новостей
    private fun DBBookmark.setupArticleDivider(divider: View) {
        if (articles.last() == this) {
            divider.visibility = View.INVISIBLE
        } else {
            divider.visibility = View.VISIBLE
        }
    }

    // устанавливает кнопку закладки в положение включено
    private fun setupBookmark(imageBookmark: ImageView) {
        imageBookmark.setImageResource(R.drawable.ic_baseline_bookmark_24)
    }

    // устанавливает заголовок новости
    private fun DBBookmark.setupArticleHeader(newsHeader: TextView) {
        newsHeader.text = title
    }

    // устанавливает описание новости новости
    private fun DBBookmark.setupArticleDescription(newsDescription: TextView) {
        if (description.isNullOrBlank()) {
            newsDescription.visibility = View.GONE
        } else {
            newsDescription.visibility = View.VISIBLE
            newsDescription.text = description
        }
    }

    // устанавливает нажатие по кнопке закладки
    private fun DBBookmark.setupClicksBookmarkContainer(
        bookmarkContainer: LinearLayout
    ) {
        bookmarkContainer.setOnClickListener {
            busEvent.onNext(Events.RemoveArticleFromBookmarks(url))
        }
    }

    // устанавливает нажатие по новости
    private fun DBBookmark.setupClicksNewsContainer(newsContainer: ConstraintLayout) {
        newsContainer.setOnClickListener { busEvent.onNext(Events.ArticleClickEvent(url)) }
    }
}