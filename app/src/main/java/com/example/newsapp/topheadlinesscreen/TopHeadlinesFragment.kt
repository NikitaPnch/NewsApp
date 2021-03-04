package com.example.newsapp.topheadlinesscreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentTopHeadlinesBinding
import com.example.newsapp.extensions.openUrlInCustomTabs
import com.example.newsapp.extensions.setAdapterAndCleanupOnDetachFromWindow
import com.example.newsapp.extensions.setData
import com.example.newsapp.extensions.showToastMessage
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TopHeadlinesFragment : Fragment(R.layout.fragment_top_headlines) {

    companion object {
        fun newInstance(): TopHeadlinesFragment {
            return TopHeadlinesFragment()
        }
    }

    private val binding by viewBinding(FragmentTopHeadlinesBinding::bind)

    private val viewModel: TopHeadlinesScreenViewModel by sharedViewModel()
    private val adapter = ListDelegationAdapter(
        articleAdapterDelegate(
            onClickNews = {
                openUrlInCustomTabs(requireActivity(), it)
            },
            onClickBookmark = {
                viewModel.processUiEvent(UiEvent.OnBookmarkClick(it))
            }
        )
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        setupObservers()
        setupSwipeRefreshNews()
    }

    override fun onResume() {
        super.onResume()
        viewModel.processUiEvent(UiEvent.GetCurrentNews)
    }

    private fun render(viewState: ViewState) {
        with(binding) {
            when (viewState.status) {
                STATUS.LOAD -> {
                    srlRefreshNews.isRefreshing = true
                }
                STATUS.CONTENT -> {
                    srlRefreshNews.isRefreshing = false
                    adapter.setData(viewState.articleList)
                }
                STATUS.ERROR -> {
                    srlRefreshNews.isRefreshing = false
                    requireContext().showToastMessage(R.string.error_message)
                }
                STATUS.CURRENT_CONTENT -> {
                    adapter.setData(viewState.articleList)
                }
            }
        }
    }

    // установка наблюдателей
    private fun setupObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer(::render))
    }

    // установка "Потянуть для обновления" - заново запросить новости
    private fun setupSwipeRefreshNews() {
        binding.srlRefreshNews.setOnRefreshListener {
            viewModel.processUiEvent(UiEvent.OnRefreshNews)
        }
    }

    // настройка recycler
    private fun setupRecycler() {
        with(binding) {
            rvNews.layoutManager = LinearLayoutManager(requireContext())
            rvNews.setAdapterAndCleanupOnDetachFromWindow(adapter)
        }
    }
}