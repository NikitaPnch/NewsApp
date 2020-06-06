package com.example.newsapp.ui.fragments.search

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.Events
import com.example.newsapp.R
import com.example.newsapp.extensions.hideKeyboard
import com.example.newsapp.extensions.liveDataNotNull
import com.example.newsapp.extensions.observeNotNull
import com.example.newsapp.ui.MainActions
import com.example.newsapp.viewmodel.Action
import com.example.newsapp.viewmodel.MainViewModel
import com.jakewharton.rxbinding3.widget.textChanges
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDispose
import io.reactivex.rxkotlin.ofType
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class SearchFragment : Fragment() {

    private val model by sharedViewModel<MainViewModel>()
    private lateinit var searchAdapter: SearchAdapter
    private val busEvent = PublishSubject.create<Any>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()

        et_search.textChanges()
            .map { query ->
                query.toString().toLowerCase(Locale.getDefault()).trim()
            }
            .debounce(500L, TimeUnit.MILLISECONDS)
            .distinct()
            .filter { query ->
                query.isNotBlank()
            }
            .autoDispose(scope())
            .subscribe { query ->
                getNews(query)
            }

        busEvent.ofType<Events.NewsClickEvent>()
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

    private fun getNews(query: String) {
        model.send { MainActions.SearchNews(query) }
    }

    private fun setupRecycler() {
        val layoutManager = LinearLayoutManager(this.requireActivity())
        searchAdapter =
            SearchAdapter(busEvent)
        searchAdapter.appendTo(rv_news_search, layoutManager)

        rv_news_search.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    hideKeyboard(requireActivity())
                }
            }
        })
    }

    private fun setupObservers() {

        model.searchLiveData.observeNotNull(this) {
            it.let { sortedList ->
                searchAdapter.setData(sortedList)
            }
        }

        model.isLoadingSearch.observeNotNull(this) {
            if (it) {
                iv_select_filter.visibility = View.GONE
                progress_search.visibility = View.VISIBLE
            } else {
                iv_select_filter.visibility = View.VISIBLE
                progress_search.visibility = View.GONE
            }
        }

        model.listen<Action.Error>().liveDataNotNull(this) {
            model.isLoading.value = false
            Timber.tag("ERROR").e(it.errorMessage.localizedMessage)
        }
    }
}