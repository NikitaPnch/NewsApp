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
import com.example.newsapp.db.views.NewsWithBookmarks
import com.example.newsapp.extensions.createImageRequest
import com.facebook.drawee.view.SimpleDraweeView
import io.reactivex.rxjava3.subjects.PublishSubject

class TopHeadlinesAdapter(private val busEvent: PublishSubject<Any>) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var articles: List<NewsWithBookmarks> = emptyList()
    private lateinit var recyclerView: RecyclerView

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val articleContainer: ConstraintLayout =
            itemView.findViewById(R.id.cl_news_item_container)
        private val articleImage: SimpleDraweeView = itemView.findViewById(R.id.sdv_image_news)
        private val articleHeader: TextView = itemView.findViewById(R.id.tv_header_news)
        private val articleDescription: TextView = itemView.findViewById(R.id.tv_news_description)
        private val bookmarkContainer: LinearLayout = itemView.findViewById(R.id.ll_bookmark)
        private val imageBookmark: ImageView = itemView.findViewById(R.id.iv_bookmark)
        private val divider: View = itemView.findViewById(R.id.view_item_divider)

        override fun onBind(position: Int) {
            super.onBind(position)
            val article = articles[position]

            article.apply {
                setupArticleImage(articleImage)
                setupArticleDivider(divider)
                setupIsBookmarked(imageBookmark)
                setupArticleHeader(articleHeader)
                setupArticleDescription(articleDescription)
                setupClicksBookmarkContainer(bookmarkContainer, imageBookmark)
                setupClicksArticleContainer(articleContainer)
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

    // установка данных
    fun setData(articles: List<NewsWithBookmarks>) {
        this.articles = articles
        notifyDataSetChanged()
    }

    // привязывает adapter к текущему recycler
    fun appendTo(recyclerView: RecyclerView, layoutManager: LinearLayoutManager) {
        this.recyclerView = recyclerView
        this.recyclerView.layoutManager = layoutManager
        this.recyclerView.adapter = this
    }

    // устанавливает изображение новости
    private fun NewsWithBookmarks.setupArticleImage(articleImage: SimpleDraweeView) {
        if (urlToImage.isNullOrBlank()) {
            articleImage.visibility = View.GONE
        } else {
            articleImage.visibility = View.VISIBLE
            articleImage.createImageRequest(urlToImage!!)
        }
    }

    // устанавливает разделитель новостей
    private fun NewsWithBookmarks.setupArticleDivider(divider: View) {
        if (articles.last() == this) {
            divider.visibility = View.INVISIBLE
        } else {
            divider.visibility = View.VISIBLE
        }
    }

    // устанавливает кнопку закладки в положение включено
    private fun NewsWithBookmarks.setupIsBookmarked(imageBookmark: ImageView) {
        if (isBookmarked) {
            imageBookmark.setImageResource(R.drawable.ic_baseline_bookmark_24)
        } else {
            imageBookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
        }
    }

    // устанавливает заголовок новости
    private fun NewsWithBookmarks.setupArticleHeader(articleHeader: TextView) {
        articleHeader.text = title
    }

    // устанавливает описание новости новости
    private fun NewsWithBookmarks.setupArticleDescription(articleDescription: TextView) {
        if (description.isNullOrBlank()) {
            articleDescription.visibility = View.GONE
        } else {
            articleDescription.visibility = View.VISIBLE
            articleDescription.text = description
        }
    }

    // устанавливает нажатие по кнопке закладки
    private fun NewsWithBookmarks.setupClicksBookmarkContainer(
        bookmarkContainer: LinearLayout,
        imageBookmark: ImageView
    ) {
        bookmarkContainer.setOnClickListener {
            if (isBookmarked) {
                imageBookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
                busEvent.onNext(Events.RemoveArticleFromBookmarks(url))
            } else {
                imageBookmark.setImageResource(R.drawable.ic_baseline_bookmark_24)
                busEvent.onNext(Events.AddArticleToBookmarks(this))
            }
        }
    }

    // устанавливает нажатие по новости
    private fun NewsWithBookmarks.setupClicksArticleContainer(articleContainer: ConstraintLayout) {
        articleContainer.setOnClickListener { busEvent.onNext(Events.ArticleClickEvent(url)) }
    }
}