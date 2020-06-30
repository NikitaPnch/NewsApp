package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import autodispose2.androidx.lifecycle.scope
import autodispose2.autoDispose
import com.example.newsapp.Events
import com.example.newsapp.R
import com.example.newsapp.extensions.*
import com.example.newsapp.ui.adapters.BookmarksAdapter
import com.example.newsapp.viewmodel.MainActions
import com.example.newsapp.viewmodel.MainViewModel
import io.reactivex.rxjava3.kotlin.ofType
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_bookmarks.*
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
        return inflater.inflate(R.layout.fragment_bookmarks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        setupObservers()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()

        setupBusEvents()
    }

    // установка слушателей
    private fun setupListeners() {

        // слушает событие проскроллить к первой записи
        model.listen<MainActions.ScrollToTop>().liveDataNotNull(this) {
            if (rv_bookmarks.size > 0) {
                rv_bookmarks.smoothScrollToPosition(0)
            }
        }
    }

    // устанавливает обработку событий шины
    private fun setupBusEvents() {

        // нажатие по новости
        busEvent.ofType<Events.ArticleClickEvent>()
            .autoDispose(scope())
            .subscribe {
                openUrlInCustomTabs(this.requireActivity(), it.url)
            }

        // удаление новости из закладок
        busEvent.ofType<Events.RemoveArticleFromBookmarks>()
            .autoDispose(scope())
            .subscribe {
                model.send { MainActions.RemoveArticleFromBookmarks(it.url) }
            }
    }

    // устанавливает наблюдатель закладок и их изменение в бд
    private fun setupObservers() {
        model.bookmarksLiveData.observeNotNull(this) { unsortedList ->
            empty_view_bookmark.changeViewVisibility(unsortedList.isEmpty())
            unsortedList.sortedByDescending { getTimestampFromString(it.publishedAt) }
                .let { sortedList ->
                    bookmarksAdapter.setData(sortedList)
                }
        }
    }

    // настраивает recycler
    private fun setupRecycler() {
        val layoutManager = LinearLayoutManager(this.requireActivity())
        bookmarksAdapter = BookmarksAdapter(busEvent)
        bookmarksAdapter.appendTo(rv_bookmarks, layoutManager)
    }
}