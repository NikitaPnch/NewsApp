package com.example.newsapp.extensions

import android.content.res.Resources
import java.util.*

class LocaleResolver(private val resources: Resources) {

    // получить установленный язык на устройстве
    fun getLocaleCountry(): String {
        return resources.configuration.locale.country.toLowerCase(Locale.getDefault())
    }

    // получить установленный язык на устройстве
    fun getLocaleLanguage(): String {
        return resources.configuration.locale.language.toLowerCase(Locale.getDefault())
    }
}