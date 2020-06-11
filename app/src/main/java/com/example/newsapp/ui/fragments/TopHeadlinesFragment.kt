package com.example.newsapp.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import autodispose2.androidx.lifecycle.scope
import autodispose2.autoDispose
import com.example.newsapp.Events
import com.example.newsapp.R
import com.example.newsapp.extensions.getLocale
import com.example.newsapp.extensions.getTimestampFromString
import com.example.newsapp.extensions.liveDataNotNull
import com.example.newsapp.extensions.observeNotNull
import com.example.newsapp.ui.adapters.TopHeadlinesAdapter
import com.example.newsapp.viewmodel.Action
import com.example.newsapp.viewmodel.MainActions
import com.example.newsapp.viewmodel.MainViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_top_headlines.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class TopHeadlinesFragment : Fragment() {

    private val model by sharedViewModel<MainViewModel>()
    private lateinit var newsAdapter: TopHeadlinesAdapter
    private val busEvent = PublishSubject.create<Any>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_top_headlines, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        setupSwipeRefreshNews()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()

        busEvent.ofType(Events.NewsClickEvent::class.java)
            .autoDispose(scope())
            .subscribe {
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(
                    ContextCompat.getColor(
                        this.requireActivity(),
                        R.color.colorWhite
                    )
                )
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this.requireActivity(), Uri.parse(it.url))
            }
    }

    private fun setupObservers() {
        model.topHeadlinesLiveData.observeNotNull(this) { unsortedList ->
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
    }

    private fun setupSwipeRefreshNews() {
        srl_refresh_news.setOnRefreshListener {
            getNews()
        }
    }

    private fun setupRecycler() {
        val layoutManager = LinearLayoutManager(this.requireActivity())
        newsAdapter =
            TopHeadlinesAdapter(busEvent)
        newsAdapter.appendTo(rv_news, layoutManager)
    }

    // получить новости
    private fun getNews() {
        model.send { MainActions.GetNews(getLocale(resources)) }
    }
}