package com.example.newsapp.extensions

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.example.newsapp.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

// вытаскивает timestamp из строки с датой
fun getTimestampFromString(dateStr: String): Long {
    val newString = dateStr.replace("T", " ").replace("Z", "")
    val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
    val date: Date = formatter.parse(newString) as Date
    return date.time
}

// получить установленный язык на устройстве
fun getLocaleCountry(resources: Resources): String {
    return resources.configuration.locale.country
}

// скрыть клавиатуру
fun hideKeyboard(activity: Activity) {
    val imm: InputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view: View? = activity.currentFocus
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

// приводит дату к формату который нужно передать в апи запросе
fun toQueryDate(year: Int, month: Int, dayOfMonth: Int): String {
    return "$year-${month + 1}-${dayOfMonth}"
}

// открывает ссылку внутри браузера в приложении
fun openUrlInCustomTabs(context: Activity, url: String) {
    val builder = CustomTabsIntent.Builder()
    builder.addDefaultShareMenuItem()
    builder.setToolbarColor(
        ContextCompat.getColor(
            context,
            R.color.colorWhite
        )
    )
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(context, Uri.parse(url))
}

// показывает toast с любым сообщением
fun Context.showToastMessage(message: Int) {
    Toast.makeText(
        this,
        resources.getString(message),
        Toast.LENGTH_SHORT
    ).show()
}