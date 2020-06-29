package com.example.newsapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseViewModel : ViewModel() {

    val bus: PublishSubject<Action> = PublishSubject.create()

    private suspend fun send(action: Action) {
        runCatching {
            bus.onNext(action)
            listen(action)
        }.onFailure { t ->
            Action.Error(t).let {
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

    protected open suspend fun listen(action: Action) {}

    inline fun <reified T : Any> listen(): Observable<T> = bus
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .ofType(T::class.java)
}