package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import autodispose2.androidx.lifecycle.scope
import autodispose2.autoDispose
import com.example.newsapp.Events
import com.example.newsapp.R
import com.example.newsapp.extensions.*
import com.example.newsapp.ui.adapters.SearchAdapter
import com.example.newsapp.viewmodel.Action
import com.example.newsapp.viewmodel.MainActions
import com.example.newsapp.viewmodel.MainViewModel
import io.reactivex.rxjava3.kotlin.ofType
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.main_bar_search
import kotlinx.android.synthetic.main.view_search.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

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
        setupListeners()
        setupClicks()
        setupTextChanges()
        requireContext().showKeyboardForEditText(et_search)
    }

    override fun onResume() {
        super.onResume()

        setupBusEvents()
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

    // установка слушателей
    private fun setupListeners() {

        // слушатель ошибки viewmodel - скрывает прогресс загрузки
        model.listen<Action.Error>().liveDataNotNull(this) {
            main_bar_search.hideProgressBar()
            this.requireContext().showToastMessage(R.string.error_message)
            Timber.tag("ERROR").e(it.errorMessage.localizedMessage)
        }
    }

    // настройка нажатий в текущем фрагменте
    private fun setupClicks() {

        // кнопка вызывает диалог с фильтрами
        main_bar_search.setupFilterClicks(this) {
            showFilterFragment()
        }
    }

    // устанавливает слушатель шины событий
    private fun setupBusEvents() {

        // событие нажатия на новость
        busEvent.ofType<Events.ArticleClickEvent>()
            .autoDispose(scope())
            .subscribe {
                openUrlInCustomTabs(this.requireActivity(), it.url)
            }
    }

    // установка слушателей изменения текста
    private fun setupTextChanges() {

        // слушатель изменения текста в поиске
        main_bar_search.setupSearchChanges(this) {
            model.send { MainActions.SetQuery(it) }
            model.send { MainActions.SearchNews(getLocaleLanguage(resources)) }
        }
    }

    // показывает диалог с фильтрами
    private fun showFilterFragment() {
        filterDialog.show(parentFragmentManager, "filter_dialog")
    }

    // установка наблюдателей
    private fun setupObservers() {

        // слушает изменения результатов поиска и обновляет список adapter
        model.searchLiveData.observeNotNull(this) {
            empty_view_search.changeViewVisibility(it.isEmpty())
            it.let { sortedList ->
                searchAdapter.setData(sortedList)
            }
        }

        // слушает состояние загрузки поиска и показывает preloader
        model.isLoadingSearch.observeNotNull(this) {
            if (it) {
                main_bar_search.showProgressBar()
            } else {
                main_bar_search.hideProgressBar()
            }
        }

        // слушает ошибки viewmodel и показывает сообщение с ошибкой
        model.listen<Action.Error>().liveDataNotNull(this) {
            model.isLoadingSearch.value = false
            this.requireContext().showToastMessage(R.string.error_message)
            Timber.tag("ERROR").e(it.errorMessage.localizedMessage)
        }
    }
}