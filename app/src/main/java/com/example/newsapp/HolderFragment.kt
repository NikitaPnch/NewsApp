package com.example.newsapp

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import com.example.newsapp.base.BaseFragment
import com.example.newsapp.bookmarks.ui.BookmarksScreen
import com.example.newsapp.extensions.NotificationHelper
import com.example.newsapp.extensions.hideKeyboard
import com.example.newsapp.extensions.updateConstraints
import com.example.newsapp.searchscreen.ui.SearchScreen
import com.example.newsapp.topheadlinesscreen.TopHeadlinesScreen
import com.example.newsapp.topheadlinesscreen.di.NEWS_FEED_QUALIFIER
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.AppScreen
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_holder.*
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class HolderFragment : BaseFragment(R.layout.fragment_holder) {

    companion object {
        fun newInstance(): HolderFragment {
            return HolderFragment()
        }
    }

    private val navigator: Navigator by lazy { createNavigator() }
    private lateinit var snackBar: Snackbar
    private val router: Router by inject(named(NEWS_FEED_QUALIFIER))
    private val navigatorHolder: NavigatorHolder by inject(named(NEWS_FEED_QUALIFIER))
    private val notificationHelper: NotificationHelper by inject()

    private val topHeadlinesScreen by lazy {
        TopHeadlinesScreen.get()
    }

    private val bookmarksScreen by lazy {
        BookmarksScreen.get()
    }

    private val searchScreen by lazy {
        SearchScreen.get()
    }


    private fun createNavigator(): Navigator {
        return AppNavigator(requireActivity(), R.id.fragment_container, childFragmentManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        snackBar = Snackbar.make(
            root_layout,
            getString(R.string.no_internet_connection),
            Snackbar.LENGTH_LONG
        )

        notificationHelper.createNotificationChannel(requireContext())
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }

    override fun onAvailable() {
        super.onAvailable()
        showNetworkMessage(true)
    }

    override fun onLost() {
        super.onLost()
        showNetworkMessage(false)
    }

    // настраивает BottomNavigationView для переключения фрагментов между собой
    private fun setupBottomNavigation() {
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_today -> {
                    hideKeyboard(requireActivity())
                    setupFragment(R.string.news, topHeadlinesScreen)
                    showMainBarWithAnim()
                }
                R.id.action_bookmarks -> {
                    hideKeyboard(requireActivity())
                    setupFragment(R.string.bookmarks, bookmarksScreen)
                    showMainBarWithAnim()
                }
                R.id.action_search -> {
                    setupFragment(R.string.search, searchScreen)
                    hideMainBarWithAnim()
                }
                else -> {
                }
            }
            true
        }
        router.replaceScreen(topHeadlinesScreen)
    }

    // устанавливает выбранный фрагмент с параметрами
    private fun setupFragment(@StringRes name: Int, screen: AppScreen) {
        renameBar(name)
        router.replaceScreen(screen)
    }

    // показывает сообщение о состоянии интернет соединения
    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            snackBar.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar.show()
        } else {
            snackBar.dismiss()
        }
    }

    // скрывает main bar с анимацией
    private fun hideMainBarWithAnim() {
        if (main_bar.visibility == View.VISIBLE) {
            main_bar.animate().apply {
                translationY(-main_bar.height.toFloat())
                withEndAction {
                    main_bar.visibility = View.INVISIBLE
                }
                startDelay = 250
                duration = 250
                start()
            }
            updateConstraints(root_layout, R.id.fragment_container, R.id.main_bar, isShow = false)
        }
    }

    // показывает main bar с анимацией
    private fun showMainBarWithAnim() {
        if (main_bar.visibility == View.INVISIBLE) {
            main_bar.animate().apply {
                translationYBy(main_bar.height.toFloat())
                withStartAction {
                    main_bar.visibility = View.VISIBLE
                }
                startDelay = 250
                duration = 250
                start()
            }
            updateConstraints(root_layout, R.id.fragment_container, R.id.main_bar, isShow = true)
        }
    }

    // переименовывает main bar
    private fun renameBar(@StringRes name: Int) {
        main_bar_text.text = resources.getString(name)
    }
}