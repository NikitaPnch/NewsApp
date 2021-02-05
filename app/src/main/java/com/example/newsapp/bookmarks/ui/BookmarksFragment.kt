package com.example.newsapp.bookmarks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.extensions.openUrlInCustomTabs
import com.example.newsapp.extensions.setAdapterAndCleanupOnDetachFromWindow
import com.example.newsapp.extensions.setData
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import kotlinx.android.synthetic.main.fragment_bookmarks.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class BookmarksFragment : Fragment() {

    companion object {
        fun newInstance(): BookmarksFragment {
            return BookmarksFragment()
        }
    }

    private val viewModel: BookmarksViewModel by viewModel()
    private val adapter = ListDelegationAdapter(
        bookmarkAdapterDelegate(
            onClick = {
                openUrlInCustomTabs(requireActivity(), it)
            },
            onClickBookmark = {
                viewModel.processUiEvent(UiEvent.OnBookmarkDeleteClick(it))
            }
        )
    )

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
        setupSwipeRefreshBookmarks()
    }

    private fun render(viewState: ViewState) {
        when (viewState.status) {
            STATUS.LOAD -> {
                srl_refresh_bookmarks.isRefreshing = true
            }
            STATUS.CONTENT -> {
                srl_refresh_bookmarks.isRefreshing = false
                adapter.setData(viewState.bookmarksList)
            }
            STATUS.ERROR -> {
                srl_refresh_bookmarks.isRefreshing = false
            }
            STATUS.EMPTY -> {
                rv_bookmarks.isVisible = false
                srl_refresh_bookmarks.isRefreshing = false
                empty_view_bookmark.isVisible = true
                adapter.setData(viewState.bookmarksList)
            }
        }
    }

    // установка "Потянуть для обновления" - заново запросить новости
    private fun setupSwipeRefreshBookmarks() {
        srl_refresh_bookmarks.setOnRefreshListener {
            viewModel.processUiEvent(UiEvent.OnRefreshBookmarks)
        }
    }

    // устанавливает наблюдатель закладок и их изменение в бд
    private fun setupObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer(::render))
    }

    // настраивает recycler
    private fun setupRecycler() {
        rv_bookmarks.layoutManager = LinearLayoutManager(requireContext())
        rv_bookmarks.setAdapterAndCleanupOnDetachFromWindow(adapter)
    }
}