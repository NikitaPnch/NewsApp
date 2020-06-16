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
        private val newsContent: TextView = itemView.findViewById(R.id.tv_news_content)
        private val bookmarkContainer: LinearLayout = itemView.findViewById(R.id.ll_bookmark)
        private val imageBookmark: ImageView = itemView.findViewById(R.id.iv_bookmark)
        private val divider: View = itemView.findViewById(R.id.view_item_divider)

        override fun onBind(position: Int) {
            super.onBind(position)
            val article = articles[position]

            if (article.urlToImage.isNullOrBlank()) {
                newsImage.visibility = View.GONE
            } else {
                newsImage.visibility = View.VISIBLE
                newsImage.createImageRequest(article.urlToImage!!)
            }

            if (articles.last() == article) {
                divider.visibility = View.GONE
            } else {
                divider.visibility = View.VISIBLE
            }

            imageBookmark.setImageResource(R.drawable.ic_baseline_bookmark_24)

            newsHeader.text = article.title

            if (article.description.isNullOrBlank()) {
                newsContent.visibility = View.GONE
            } else {
                newsContent.visibility = View.VISIBLE
                newsContent.text = article.description
            }

            bookmarkContainer.setOnClickListener {
                imageBookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
                busEvent.onNext(Events.RemoveArticleFromBookmarks(article.url))
            }

            newsContainer.setOnClickListener {
                busEvent.onNext(
                    Events.NewsClickEvent(
                        article.url
                    )
                )
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

    fun setData(articles: List<DBBookmark>) {
        this.articles = articles
        notifyDataSetChanged()
    }

    fun appendTo(recyclerView: RecyclerView, layoutManager: LinearLayoutManager) {
        this.recyclerView = recyclerView
        this.recyclerView.layoutManager = layoutManager
        this.recyclerView.adapter = this
    }
}