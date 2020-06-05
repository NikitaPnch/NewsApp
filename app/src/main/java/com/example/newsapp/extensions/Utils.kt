package com.example.newsapp.extensions

import android.content.res.Resources
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