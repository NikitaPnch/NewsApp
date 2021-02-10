package com.example.newsapp.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.example.newsapp.R
import com.example.newsapp.databinding.ViewEmptyBinding

class EmptyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var binding: ViewEmptyBinding

    init {
        val view = View.inflate(context, R.layout.view_empty, this)
        binding = ViewEmptyBinding.bind(view)
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.EmptyView)
        with(binding) {
            ivEmpty.setImageDrawable(attributes.getDrawable(R.styleable.EmptyView_image))
            tvEmpty.text = attributes.getString(R.styleable.EmptyView_text)
        }
        attributes.recycle()
    }
}