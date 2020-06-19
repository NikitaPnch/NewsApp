package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import autodispose2.androidx.lifecycle.scope
import autodispose2.autoDispose
import com.example.newsapp.Events
import com.example.newsapp.R
import com.example.newsapp.extensions.*
import com.example.newsapp.ui.adapters.TopHeadlinesAdapter
import com.example.newsapp.viewmodel.Action
import com.example.newsapp.viewmodel.MainActions
import com.example.newsapp.viewmodel.MainViewModel
import io.reactivex.rxjava3.kotlin.ofType
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_top_headlines.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class TopHeadlinesFragment : Fragment() {

    private val model by sharedViewModel<MainViewModel>()
    private lateinit var newsAdapter: TopHeadlinesAdapter
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
        setupSwipeRefreshNews()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()

        setupBusEvents()
    }

    // устанавливает обработку событий шины
    private fun setupBusEvents() {

        // событие нажатия на новость
        busEvent.ofType<Events.ArticleClickEvent>()
            .autoDispose(scope())
            .subscribe {
                openUrlInCustomTabs(this.requireActivity(), it.url)
            }

        // событие добавления новости в закладки
        busEvent.ofType<Events.AddArticleToBookmarks>()
            .autoDispose(scope())
            .subscribe {
                model.send { MainActions.AddArticleToBookmarks(it.dbNews) }
            }

        // событие удаления новости из закладок
        busEvent.ofType<Events.RemoveArticleFromBookmarks>()
            .autoDispose(scope())
            .subscribe {
                model.send { MainActions.RemoveArticleFromBookmarks(it.url) }
            }
    }

    // установка наблюдателей
    private fun setupObservers() {

        // следит за изменением таблицы в бд и обновляет список recycler
        model.topHeadlinesLiveData.observeNotNull(this) { unsortedList ->
            unsortedList.sortedByDescending { getTimestampFromString(it.publishedAt) }
                .let { sortedList ->
                    newsAdapter.setData(sortedList)
                }
        }

        // управляет показом preloader подгрузки новостей
        model.isLoading.observeNotNull(this) { isLoading ->
            srl_refresh_news.isRefreshing = isLoading
        }

        // слушает события ошибки запроса viewmodel, при ошибке отключает preloader
        model.listen<Action.Error>().liveDataNotNull(this) {
            model.isLoading.value = false
            srl_refresh_news.isRefreshing = false
            this.requireContext().showToastMessage(R.string.error_message)
            Timber.tag("ERROR").e(it.errorMessage.localizedMessage)
        }
    }

    // установка "Потянуть для обновления" - заново запросить новости
    private fun setupSwipeRefreshNews() {
        srl_refresh_news.setOnRefreshListener {
            getNews()
        }
    }

    // настройка recycler
    private fun setupRecycler() {
        val layoutManager = LinearLayoutManager(this.requireActivity())
        newsAdapter = TopHeadlinesAdapter(busEvent)
        newsAdapter.appendTo(rv_news, layoutManager)
    }

    // получить новости
    private fun getNews() {
        model.send { MainActions.GetNews(getLocaleCountry(resources)) }
    }
}