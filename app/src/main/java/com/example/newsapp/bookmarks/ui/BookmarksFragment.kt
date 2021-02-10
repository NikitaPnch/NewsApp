package com.example.newsapp.bookmarks.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentBookmarksBinding
import com.example.newsapp.extensions.openUrlInCustomTabs
import com.example.newsapp.extensions.setAdapterAndCleanupOnDetachFromWindow
import com.example.newsapp.extensions.setData
import com.example.newsapp.extensions.showToastMessage
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class BookmarksFragment : Fragment(R.layout.fragment_bookmarks) {

    companion object {
        fun newInstance(): BookmarksFragment {
            return BookmarksFragment()
        }
    }

    private val binding by viewBinding(FragmentBookmarksBinding::bind)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        setupObservers()
        setupSwipeRefreshBookmarks()
    }

    override fun onResume() {
        super.onResume()
        viewModel.processUiEvent(UiEvent.OnRefreshBookmarks)
    }

    private fun render(viewState: ViewState) {
        with(binding) {
            when (viewState.status) {
                STATUS.LOAD -> {
                    srlRefreshBookmarks.isRefreshing = true
                }
                STATUS.CONTENT -> {
                    rvBookmarks.isVisible = true
                    emptyViewBookmark.isVisible = false
                    srlRefreshBookmarks.isRefreshing = false
                    adapter.setData(viewState.bookmarksList)
                }
                STATUS.ERROR -> {
                    srlRefreshBookmarks.isRefreshing = false
                    requireContext().showToastMessage(R.string.error_message)
                }
                STATUS.EMPTY -> {
                    rvBookmarks.isVisible = false
                    srlRefreshBookmarks.isRefreshing = false
                    emptyViewBookmark.isVisible = true
                }
            }
        }
    }

    // установка "Потянуть для обновления" - заново запросить новости
    private fun setupSwipeRefreshBookmarks() {
        binding.srlRefreshBookmarks.setOnRefreshListener {
            viewModel.processUiEvent(UiEvent.OnRefreshBookmarks)
        }
    }

    // устанавливает наблюдатель закладок и их изменение в бд
    private fun setupObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer(::render))
    }

    // настраивает recycler
    private fun setupRecycler() {
        with(binding) {
            rvBookmarks.layoutManager = LinearLayoutManager(requireContext())
            rvBookmarks.setAdapterAndCleanupOnDetachFromWindow(adapter)
        }
    }
}