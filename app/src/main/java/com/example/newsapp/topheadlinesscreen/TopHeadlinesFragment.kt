package com.example.newsapp.topheadlinesscreen

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.extensions.openUrlInCustomTabs
import com.example.newsapp.extensions.setAdapterAndCleanupOnDetachFromWindow
import com.example.newsapp.extensions.setData
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import kotlinx.android.synthetic.main.fragment_top_headlines.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopHeadlinesFragment : Fragment(R.layout.fragment_top_headlines) {

    companion object {
        fun newInstance(): TopHeadlinesFragment {
            return TopHeadlinesFragment()
        }
    }

    private val viewModel: TopHeadlinesScreenViewModel by viewModel()
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

    private fun render(viewState: ViewState) {
        when (viewState.status) {
            STATUS.LOAD -> {
                srl_refresh_news.isRefreshing = true
            }
            STATUS.CONTENT -> {
                srl_refresh_news.isRefreshing = false
                adapter.setData(viewState.articleList)
            }
            STATUS.ERROR -> {
                srl_refresh_news.isRefreshing = false
                Toast.makeText(requireContext(), viewState.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // установка наблюдателей
    private fun setupObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer(::render))
    }

    // установка "Потянуть для обновления" - заново запросить новости
    private fun setupSwipeRefreshNews() {
        srl_refresh_news.setOnRefreshListener {
            viewModel.processUiEvent(UiEvent.OnRefreshNews)
        }
    }

    // настройка recycler
    private fun setupRecycler() {
        rv_news.layoutManager = LinearLayoutManager(requireContext())
        rv_news.setAdapterAndCleanupOnDetachFromWindow(adapter)
    }
}