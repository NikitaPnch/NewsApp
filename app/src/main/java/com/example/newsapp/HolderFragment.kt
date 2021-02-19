package com.example.newsapp

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.newsapp.base.BaseFragment
import com.example.newsapp.bookmarks.ui.BookmarksFragment
import com.example.newsapp.databinding.FragmentHolderBinding
import com.example.newsapp.extensions.hideKeyboard
import com.example.newsapp.extensions.updateConstraints
import com.example.newsapp.extensions.viewBinding
import com.example.newsapp.searchscreen.ui.SearchFragment
import com.example.newsapp.topheadlinesscreen.TopHeadlinesFragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class HolderFragment : BaseFragment(R.layout.fragment_holder) {

    companion object {
        fun newInstance(): HolderFragment {
            return HolderFragment()
        }
    }

    private lateinit var snackBar: Snackbar
    private val binding by viewBinding(FragmentHolderBinding::bind)

    private val topHeadlinesFragment by lazy { TopHeadlinesFragment.newInstance() }
    private val bookmarksFragment by lazy { BookmarksFragment.newInstance() }
    private val searchFragment by lazy { SearchFragment.newInstance() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        snackBar = Snackbar.make(
            binding.rootLayout,
            getString(R.string.no_internet_connection),
            Snackbar.LENGTH_LONG
        )

        setupKeyboardVisibleEvent()
        setupBottomNavigation()
    }

    private fun setupKeyboardVisibleEvent() {
        setEventListener(
            requireActivity(),
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    binding.bottomNavigation.isVisible = !isOpen
                }
            })
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
        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_today -> {
                    replaceFragment(R.string.news, topHeadlinesFragment)
                    showMainBarWithAnim()
                }
                R.id.action_bookmarks -> {
                    replaceFragment(R.string.bookmarks, bookmarksFragment)
                    showMainBarWithAnim()
                }
                R.id.action_search -> {
                    replaceFragment(R.string.search, searchFragment)
                    hideMainBarWithAnim()
                }
                else -> {
                }
            }
            true
        }
        replaceFragment(R.string.news, topHeadlinesFragment)
    }

    // заменяет фрагменты
    private fun replaceFragment(@StringRes name: Int, fragment: Fragment) {
        hideKeyboard(requireActivity())
        renameBar(name)
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment)
        }.commit()
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
        with(binding) {
            if (mainBar.visibility == View.VISIBLE) {
                mainBar.animate().apply {
                    translationY(-mainBar.height.toFloat())
                    withEndAction {
                        mainBar.visibility = View.INVISIBLE
                    }
                    startDelay = 250
                    duration = 250
                    start()
                }
                updateConstraints(rootLayout, R.id.fragmentContainer, R.id.mainBar, isShow = false)
            }
        }
    }

    // показывает main bar с анимацией
    private fun showMainBarWithAnim() {
        with(binding) {
            if (mainBar.visibility == View.INVISIBLE) {
                mainBar.animate().apply {
                    translationYBy(mainBar.height.toFloat())
                    withStartAction {
                        mainBar.visibility = View.VISIBLE
                    }
                    startDelay = 250
                    duration = 250
                    start()
                }
                updateConstraints(rootLayout, R.id.fragmentContainer, R.id.mainBar, isShow = true)
            }
        }
    }

    // переименовывает main bar
    private fun renameBar(@StringRes name: Int) {
        binding.mainBarText.text = resources.getString(name)
    }
}