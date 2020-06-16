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
import com.example.newsapp.extensions.getTimestampFromString
import com.example.newsapp.extensions.observeNotNull
import com.example.newsapp.ui.adapters.BookmarksAdapter
import com.example.newsapp.viewmodel.MainActions
import com.example.newsapp.viewmodel.MainViewModel
import io.reactivex.rxjava3.kotlin.ofType
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_top_headlines.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BookmarksFragment : Fragment() {

    private val model by sharedViewModel<MainViewModel>()
    private lateinit var bookmarksAdapter: BookmarksAdapter
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
        setupObservers()
    }

    override fun onResume() {
        super.onResume()

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

        busEvent.ofType<Events.RemoveArticleFromBookmarks>()
            .autoDispose(scope())
            .subscribe {
                model.send { MainActions.RemoveArticleFromBookmarks(it.url) }
            }
    }

    private fun setupObservers() {
        model.bookmarksLiveData.observeNotNull(this) { unsortedList ->
            unsortedList.sortedByDescending { getTimestampFromString(it.publishedAt) }
                .let { sortedList ->
                    bookmarksAdapter.setData(sortedList)
                }
        }
    }

    private fun setupRecycler() {
        val layoutManager = LinearLayoutManager(this.requireActivity())
        bookmarksAdapter = BookmarksAdapter(busEvent)
        bookmarksAdapter.appendTo(rv_news, layoutManager)
    }
}