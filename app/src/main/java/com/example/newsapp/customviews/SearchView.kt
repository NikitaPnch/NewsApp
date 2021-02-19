package com.example.newsapp.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.example.newsapp.R
import com.example.newsapp.databinding.ViewSearchBinding
import com.example.newsapp.extensions.liveDataNotNull
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import java.util.*
import java.util.concurrent.TimeUnit

class SearchView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: ViewSearchBinding

    init {
        val view = View.inflate(context, R.layout.view_search, this)
        binding = ViewSearchBinding.bind(view)
    }

    var text: String
        get() = this.toString()
        set(value) {
            setText(value)
        }

    private fun setText(text: CharSequence) {
        with(binding.etSearch) {
            setText(text)
            setSelection(text.length)
        }
    }

    fun requestFocusText() {
        binding.etSearch.requestFocus()
    }

    fun setupSearchChanges(lifecycleOwner: LifecycleOwner, action: (query: String) -> Unit) {
        binding.etSearch.textChanges()
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
        binding.llSelectFilter
            .clicks()
            .throttleFirst(500L, TimeUnit.MILLISECONDS)
            .liveDataNotNull(lifecycleOwner) {
                action.invoke()
            }
    }

    // показать progress
    fun showProgressBar() {
        with(binding) {
            llSelectFilter.isVisible = false
            progressSearch.isVisible = true
        }
    }

    // скрыть progress
    fun hideProgressBar() {
        with(binding) {
            llSelectFilter.isVisible = true
            progressSearch.isVisible = false
        }
    }
}