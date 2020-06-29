package com.example.newsapp.ui.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import com.example.newsapp.R
import com.example.newsapp.extensions.liveDataNotNull
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import kotlinx.android.synthetic.main.view_search.view.*
import java.util.*
import java.util.concurrent.TimeUnit

class SearchView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_search, this)
    }

    fun setupSearchChanges(lifecycleOwner: LifecycleOwner, action: (query: String) -> Unit) {
        et_search.textChanges()
            .map { query ->
                query.toString().toLowerCase(Locale.getDefault()).trim()
            }
            .debounce(500L, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .filter { query ->
                query.isNotBlank()
            }
            .liveDataNotNull(lifecycleOwner) { query ->
                action(query)
            }
    }

    fun setupFilterClicks(lifecycleOwner: LifecycleOwner, action: () -> Unit) {
        ll_select_filter
            .clicks()
            .throttleFirst(500L, TimeUnit.MILLISECONDS)
            .liveDataNotNull(lifecycleOwner) {
                action()
            }
    }

    // показать progress
    fun showProgressBar() {
        ll_select_filter.visibility = View.GONE
        progress_search.visibility = View.VISIBLE
    }

    // скрыть progress
    fun hideProgressBar() {
        ll_select_filter.visibility = View.VISIBLE
        progress_search.visibility = View.GONE
    }
}