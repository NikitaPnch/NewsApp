package com.example.newsapp.ui

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.Events
import com.example.newsapp.R
import com.example.newsapp.extensions.*
import com.example.newsapp.viewmodel.MainViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDispose
import io.reactivex.rxkotlin.ofType
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var newsAdapter: NewsAdapter
    private val model by viewModel<MainViewModel>()
    private val busEvent = PublishSubject.create<Any>()
    private var snackBar: Snackbar? = null
    private var isConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecycler()
        setupSwipeRefreshNews()

        model.newsListLiveDatabase.observe(this) {
            it?.sortedByDescending { getTimestampFromString(it.publishedAt) }?.let { articles ->
                newsAdapter.setData(articles)
            }
        }

        model.isLoading.observeNotNull(this) { isLoading ->
            srl_refresh_news.isRefreshing = isLoading
        }

        ConnectionLiveData(this)
            .distinctUntilChanged()
            .debounce(1000L)
            .observe(this) { isConnected ->
                isConnected?.let {
                    this.isConnected = it
                    showNetworkMessage(it)
                    if (isConnected) {
                        getNews()
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()

        busEvent.ofType<Events.NewsClickEvent>()
            .autoDispose(scope())
            .subscribe {
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, Uri.parse(it.url))
            }
    }

    private fun setupSwipeRefreshNews() {
        srl_refresh_news.setOnRefreshListener {
            getNews()
        }
    }

    private fun setupRecycler() {
        val layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter(busEvent)
        newsAdapter.appendTo(rv_news, layoutManager)
    }

    // получить новости
    private fun getNews() {
        val locale: String = resources.configuration.locale.country
        model.getNews(locale)
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            snackBar = Snackbar.make(
                findViewById(R.id.root_layout),
                getString(R.string.no_internet_connection),
                Snackbar.LENGTH_LONG
            )
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.show()
        } else {
            snackBar?.dismiss()
        }
    }
}
