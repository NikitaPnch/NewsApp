package com.example.newsapp.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

fun launchIO(block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(Dispatchers.IO) {
    runCatching {
        block()
    }.onFailure {
        Timber.tag("Error").e(it.localizedMessage)
    }
}

fun launchDefault(block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch(Dispatchers.Default) {
        runCatching {
            block()
        }.onFailure {
            Timber.tag("Error").e(it.localizedMessage)
        }
    }

fun launchMain(block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(Dispatchers.Main) {
    runCatching {
        block()
    }.onFailure {
        Timber.tag("Error").e(it.localizedMessage)
    }
}