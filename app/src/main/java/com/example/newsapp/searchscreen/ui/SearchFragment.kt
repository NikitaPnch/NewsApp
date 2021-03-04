package com.example.newsapp.searchscreen.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentSearchBinding
import com.example.newsapp.extensions.*
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class SearchFragment : Fragment(R.layout.fragment_search) {

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    private val binding by viewBinding(FragmentSearchBinding::bind)

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
        binding.mainBarSearchView.requestFocusText()
        showKeyboard(requireActivity())
    }

    private fun setupRecycler() {
        with(binding) {
            rvNewsSearch.layoutManager = LinearLayoutManager(requireContext())
            rvNewsSearch.setAdapterAndCleanupOnDetachFromWindow(adapter)
        }
    }

    // настройка нажатий в текущем фрагменте
    private fun setupClicks() {

        // кнопка вызывает диалог с фильтрами
        binding.mainBarSearchView.setupFilterClicks(viewLifecycleOwner) {
            showFilterFragment()
        }
    }

    // установка слушателей изменения текста
    private fun setupTextChanges() {

        // слушатель изменения текста в поиске
        binding.mainBarSearchView.setupSearchChanges(viewLifecycleOwner) {
            model.processUiEvent(UiEvent.OnEditTextChanged(it))
        }
    }

    // показывает диалог с фильтрами
    private fun showFilterFragment() {
        filterDialog.show(parentFragmentManager, "filter_dialog")
    }

    private fun render(viewState: ViewState) {
        with(binding) {
            if (mainBarSearchView.text.isEmpty()) {
                mainBarSearchView.text = viewState.query
            }
            when (viewState.status) {
                STATUS.LOAD -> {
                    mainBarSearchView.showProgressBar()
                }
                STATUS.CONTENT -> {
                    mainBarSearchView.hideProgressBar()
                    emptyViewSearch.isVisible = viewState.searchList.isEmpty()
                    adapter.setData(viewState.searchList)
                }
                STATUS.ERROR -> {
                    mainBarSearchView.hideProgressBar()
                    requireContext().showToastMessage(R.string.error_message)
                    Timber.tag("ERROR").e(viewState.errorMessage)
                }
            }
        }
    }

    // установка наблюдателей
    private fun setupObservers() {
        model.viewState.observe(viewLifecycleOwner, Observer(::render))
    }
}