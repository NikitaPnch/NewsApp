package com.example.newsapp.ui.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.example.newsapp.R
import kotlinx.android.synthetic.main.view_empty.view.*

class EmptyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_empty, this)
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.EmptyView)
        iv_empty.setImageDrawable(attributes.getDrawable(R.styleable.EmptyView_image))
        tv_empty.text = attributes.getString(R.styleable.EmptyView_text)
        attributes.recycle()
    }
}