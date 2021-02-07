package com.example.newsapp.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.example.newsapp.R
import com.example.newsapp.extensions.liveDataNotNull
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import kotlinx.android.synthetic.main.view_search.view.*
import java.util.*
import java.util.concurrent.TimeUnit

class SearchView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_search, this)
    }

    var text: String
        get() = this.toString()
        set(value) {
            setText(value)
        }

    private fun setText(text: CharSequence) {
        with(et_search) {
            setText(text)
            setSelection(text.length)
        }
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
                action.invoke(query)
            }
    }

    fun setupFilterClicks(lifecycleOwner: LifecycleOwner, action: () -> Unit) {
        ll_select_filter
            .clicks()
            .throttleFirst(500L, TimeUnit.MILLISECONDS)
            .liveDataNotNull(lifecycleOwner) {
                action.invoke()
            }
    }

    // показать progress
    fun showProgressBar() {
        ll_select_filter.isVisible = false
        progress_search.isVisible = true
    }

    // скрыть progress
    fun hideProgressBar() {
        ll_select_filter.isVisible = true
        progress_search.isVisible = false
    }
}