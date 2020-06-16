package com.example.newsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.distinctUntilChanged
import com.example.newsapp.R
import com.example.newsapp.extensions.ConnectionLiveData
import com.example.newsapp.extensions.debounce
import com.example.newsapp.extensions.getLocale
import com.example.newsapp.extensions.observe
import com.example.newsapp.ui.fragments.BookmarksFragment
import com.example.newsapp.ui.fragments.SearchFragment
import com.example.newsapp.ui.fragments.TopHeadlinesFragment
import com.example.newsapp.viewmodel.MainActions
import com.example.newsapp.viewmodel.MainViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val model by viewModel<MainViewModel>()
    private var snackBar: Snackbar? = null
    private val topHeadlinesFragment by lazy { TopHeadlinesFragment() }
    private val searchFragment by lazy { SearchFragment() }
    private val bookmarksFragment by lazy { BookmarksFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(topHeadlinesFragment)

        ConnectionLiveData(this)
            .distinctUntilChanged()
            .debounce(1000L)
            .observe(this) { isConnected ->
                isConnected?.let {
                    showNetworkMessage(it)
                    if (isConnected) {
                        getNews()
                    }
                }
            }

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_today -> replaceFragment(topHeadlinesFragment)
                R.id.action_bookmarks -> replaceFragment(bookmarksFragment)
                R.id.action_search -> replaceFragment(searchFragment)
                else -> replaceFragment(topHeadlinesFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
        }.commit()
    }

    // получить новости
    private fun getNews() {
        model.send { MainActions.GetNews(getLocale(resources)) }
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            snackBar = Snackbar.make(
                findViewById(R.id.root_layout),
                getString(R.string.no_internet_connection),
                Snackbar.LENGTH_LONG
            )
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.show()
        } else {
            snackBar?.dismiss()
        }
    }
}
