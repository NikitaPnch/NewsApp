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
import androidx.recyclerview.widget.RecyclerView
import autodispose2.androidx.lifecycle.scope
import autodispose2.autoDispose
import com.example.newsapp.Events
import com.example.newsapp.R
import com.example.newsapp.extensions.hideKeyboard
import com.example.newsapp.extensions.liveDataNotNull
import com.example.newsapp.extensions.observeNotNull
import com.example.newsapp.ui.adapters.SearchAdapter
import com.example.newsapp.viewmodel.Action
import com.example.newsapp.viewmodel.MainActions
import com.example.newsapp.viewmodel.MainViewModel
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.kotlin.ofType
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class SearchFragment : Fragment() {

    private val model by sharedViewModel<MainViewModel>()
    private lateinit var searchAdapter: SearchAdapter
    private val busEvent = PublishSubject.create<Any>()
    private val filterDialog by lazy { FilterBottomSheetFragment() }

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

        model.listen<Action.Error>().liveDataNotNull(this) {
            hideProgressBar()
        }

        ll_select_filter
            .clicks()
            .throttleFirst(500L, TimeUnit.MILLISECONDS)
            .liveDataNotNull(this) {
                showFilterFragment()
            }
    }

    override fun onResume() {
        super.onResume()

        et_search.textChanges()
            .map { query ->
                query.toString().toLowerCase(Locale.getDefault()).trim()
            }
            .debounce(500L, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .filter { query ->
                query.isNotBlank()
            }
            .autoDispose(scope())
            .subscribe { query ->
                model.setQuery(query)
                model.send { MainActions.SearchNews() }
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

    private fun setupRecycler() {
        val layoutManager = LinearLayoutManager(this.requireActivity())
        searchAdapter = SearchAdapter(busEvent)
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

    private fun showFilterFragment() {
        filterDialog.show(parentFragmentManager, "filter_dialog")
    }

    private fun setupObservers() {

        model.searchLiveData.observeNotNull(this) {
            it.let { sortedList ->
                searchAdapter.setData(sortedList)
            }
        }

        model.isLoadingSearch.observeNotNull(this) {
            if (it) {
                showProgressBar()
            } else {
                hideProgressBar()
            }
        }

        model.listen<Action.Error>().liveDataNotNull(this) {
            model.isLoading.value = false
            Timber.tag("ERROR").e(it.errorMessage.localizedMessage)
        }
    }

    private fun showProgressBar() {
        ll_select_filter.visibility = View.GONE
        progress_search.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        ll_select_filter.visibility = View.VISIBLE
        progress_search.visibility = View.GONE
    }
}