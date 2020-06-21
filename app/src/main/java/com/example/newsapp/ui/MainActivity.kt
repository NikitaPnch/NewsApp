package com.example.newsapp.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.distinctUntilChanged
import com.example.newsapp.R
import com.example.newsapp.extensions.*
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
    private val notificationHelper by lazy { NotificationHelper() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationHelper.createNotificationChannel(this)
        setupNetworkConnectionLiveData()
        setupBottomNavigation()
        setupAlarmManager()
    }

    // устанавливает слушатель интернет соединения
    private fun setupNetworkConnectionLiveData() {
        ConnectionLiveData(this)
            .distinctUntilChanged()
            .debounce()
            .observe(this) { isConnected ->
                isConnected?.let {
                    showNetworkMessage(it)
                    if (isConnected) {
                        getNews()
                    }
                }
            }
    }

    // настраивает BottomNavigationView для переключения фрагментов между собой
    private fun setupBottomNavigation() {
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_today -> {
                    renameBar(R.string.news)
                    replaceFragment(topHeadlinesFragment)
                    showMainBarWithAnim()
                }
                R.id.action_bookmarks -> {
                    renameBar(R.string.bookmarks)
                    replaceFragment(bookmarksFragment)
                    showMainBarWithAnim()
                }
                R.id.action_search -> {
                    replaceFragment(searchFragment)
                    hideMainBarWithAnim()
                }
                else -> replaceFragment(topHeadlinesFragment)
            }
            true
        }

        bottom_navigation.selectedItemId = R.id.action_today
    }

    // заменяет фрагменты
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
        }.commit()
    }

    // отправляет запрос на получение новостей
    private fun getNews() {
        model.send { MainActions.GetNews(getLocaleCountry(resources)) }
    }

    // показывает сообщение о состоянии интернет соединения
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
    private fun renameBar(name: Int) {
        main_bar_text.text = resources.getString(name)
    }

    // включает alarm manager с выполнением задачи каждый час которую будет обрабатывать Receiver.kt
    private fun setupAlarmManager() {
        val intent = createIntent()
        val pendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR, pendingIntent
        )
    }
}
