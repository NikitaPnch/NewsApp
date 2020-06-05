package com.example.newsapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseViewModel : ViewModel() {

    val bus = PublishSubject.create<Action>()

    protected open suspend fun listen(action: Action) {}

    private suspend fun send(action: Action) {
        runCatching {
            bus.onNext(action)
            listen(action)
        }.onFailure {
            Action.Error(it).let {
                bus.onNext(it)
                listen(it)
            }
        }
    }

    suspend fun sendAwait(action: suspend () -> Action) {
        return withContext(Dispatchers.Main) {
            send(action())
        }
    }

    fun send(action: suspend () -> Action) {
        viewModelScope.launch(Dispatchers.Main) {
            sendAwait(action)
        }
    }

    inline fun <reified T : Any> listen(): Observable<T> = bus
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .ofType(T::class.java)
}