package com.example.newsapp.extensions

import android.content.res.Resources
import android.os.Build
import java.util.*

class LocaleResolver(private val resources: Resources) {

    // получить установленный язык на устройстве
    fun getLocaleCountry(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0).country.toLowerCase(Locale.getDefault())
        } else {
            resources.configuration.locale.country.toLowerCase(Locale.getDefault())
        }
    }

    // получить установленный язык на устройстве
    fun getLocaleLanguage(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0).language.toLowerCase(Locale.getDefault())
        } else {
            resources.configuration.locale.language.toLowerCase(Locale.getDefault())
        }
    }
}