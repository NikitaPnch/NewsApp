package com.example.newsapp.searchscreen.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.extensions.*
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.view_search.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class SearchFragment : Fragment(R.layout.fragment_search) {

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    private val model: SearchScreenViewModel by sharedViewModel()
    private val adapter = ListDelegationAdapter(
        searchAdapterDelegate {
            openUrlInCustomTabs(requireActivity(), it)
        }
    )
    private val filterDialog by lazy { FilterBottomSheetFragment() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        setupObservers()
        setupClicks()
        setupTextChanges()
        et_search.requestFocus()
        showKeyboard(requireActivity(), et_search)
    }

    private fun setupRecycler() {
        rv_news_search.layoutManager = LinearLayoutManager(requireContext())
        rv_news_search.setAdapterAndCleanupOnDetachFromWindow(adapter)
    }

    // настройка нажатий в текущем фрагменте
    private fun setupClicks() {

        // кнопка вызывает диалог с фильтрами
        main_bar_search_view.setupFilterClicks(this) {
            showFilterFragment()
        }
    }

    // установка слушателей изменения текста
    private fun setupTextChanges() {

        // слушатель изменения текста в поиске
        main_bar_search_view.setupSearchChanges(this) {
            model.processUiEvent(UiEvent.OnEditTextChanged(it))
        }
    }

    // показывает диалог с фильтрами
    private fun showFilterFragment() {
        filterDialog.show(parentFragmentManager, "filter_dialog")
    }

    private fun render(viewState: ViewState) {
        when (viewState.status) {
            STATUS.LOAD -> {
                main_bar_search_view.showProgressBar()
            }
            STATUS.CONTENT -> {
                with(main_bar_search_view) {
                    setText(viewState.query)
                    hideProgressBar()
                }
                empty_view_search.changeViewVisibility(viewState.searchList.isEmpty())
                adapter.setData(viewState.searchList)
            }
            STATUS.ERROR -> {
                main_bar_search_view.hideProgressBar()
                this.requireContext().showToastMessage(R.string.error_message)
                Timber.tag("ERROR").e(viewState.errorMessage)
            }
        }
    }

    // установка наблюдателей
    private fun setupObservers() {
        model.viewState.observe(viewLifecycleOwner, Observer(::render))
    }
}