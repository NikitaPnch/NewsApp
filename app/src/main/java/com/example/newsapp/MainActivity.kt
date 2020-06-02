package com.example.newsapp

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.widget.DiffUtil
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newsAdapter = NewsAdapter(busEvent)
        newsAdapter.appendTo(rv_news, this)

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
