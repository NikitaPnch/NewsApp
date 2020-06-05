package com.example.newsapp.ui

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.Events
import com.example.newsapp.R
import com.example.newsapp.extensions.*
import com.example.newsapp.viewmodel.Action
import com.example.newsapp.viewmodel.MainViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDispose
import io.reactivex.rxkotlin.ofType
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var newsAdapter: NewsAdapter
    private val model by viewModel<MainViewModel>()
    private val busEvent = PublishSubject.create<Any>()
    private var snackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecycler()
        setupSwipeRefreshNews()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()

        busEvent.ofType<Events.NewsClickEvent>()
            .autoDispose(scope())
            .subscribe {
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorWhite))
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, Uri.parse(it.url))
            }
    }

    private fun setupObservers() {
        model.newsListLiveData.observeNotNull(this) { unsortedList ->
            unsortedList.sortedByDescending { getTimestampFromString(it.publishedAt) }
                .let { sortedList ->
                    newsAdapter.setData(sortedList)
                }
        }

        model.isLoading.observeNotNull(this) { isLoading ->
            srl_refresh_news.isRefreshing = isLoading
        }

        model.listen<Action.Error>().liveDataNotNull(this) {
            model.isLoading.value = false
            Timber.tag("ERROR").e(it.errorMessage.localizedMessage)
        }

        ConnectionLiveData(this)
            .debounce(1000L)
            .observe(this) { isConnected ->
                isConnected?.let {
                    showNetworkMessage(it)
                    if (isConnected) {
                        getNews()
                    }
                }
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
        model.send { MainActions.GetNews(getLocale(resources)) }
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
