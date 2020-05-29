package com.example.newsapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {

    val model by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_news.layoutManager = LinearLayoutManager(this)

        val locale: String = resources.configuration.locale.country

        model.getNews(locale)

        model.news.observe(this) {
            it?.let {
                rv_news.adapter = NewsAdapter(it.articles)
                if (pb_news_loading.visibility == View.VISIBLE) {
                    pb_news_loading.visibility = View.GONE
                }
            }
        }
    }
}
