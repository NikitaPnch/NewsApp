package com.example.newsapp.extensions

import android.app.Activity
import android.content.res.Resources
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


fun getTimestampFromString(dateStr: String): Long {
    val newString = dateStr.replace("T", " ").replace("Z", "")
    val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
    val date: Date = formatter.parse(newString) as Date
    return date.time
}

fun getLocale(resources: Resources): String {
    return resources.configuration.locale.country
}

fun hideKeyboard(activity: Activity) {
    val imm: InputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view: View? = activity.currentFocus
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}