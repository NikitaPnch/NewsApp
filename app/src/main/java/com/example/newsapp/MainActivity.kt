package com.example.newsapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.widget.DiffUtil
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var newsAdapter: NewsAdapter

    private val model by viewModel<MainViewModel>()

    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ConnectionLiveData(this)
            .distinctUntilChanged()
            .debounce(1000L)
            .observe(this) { isConnected ->
            isConnected?.let {
                showNetworkMessage(it)
                if (isConnected) {
                    getNews()
                }
            }
        }

        newsAdapter = NewsAdapter()
        newsAdapter.appendTo(rv_news, this)

        getNews()

        model.news.observe(this) {
            it?.let {
                val newsDiffUtilCallback = NewsDiffUtilCallback(newsAdapter.getData(), it.articles)
                val productDiffResult = DiffUtil.calculateDiff(newsDiffUtilCallback)
                newsAdapter.setData(it.articles)
                productDiffResult.dispatchUpdatesTo(newsAdapter)
                if (pb_news_loading.visibility == View.VISIBLE) {
                    pb_news_loading.visibility = View.GONE
                }
            }
        }
    }

    // получить новости
    private fun getNews() {
        val locale: String = resources.configuration.locale.country
        model.getNews(locale)
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            snackbar = Snackbar.make(
                findViewById(R.id.root_layout),
                getString(R.string.no_internet_connection),
                Snackbar.LENGTH_LONG
            )
            snackbar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackbar?.show()
        } else {
            snackbar?.dismiss()
        }
    }
}
