package com.example.newsapp

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveDataReactiveStreams
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable

fun <T> Observable<T>.liveData(strategy: BackpressureStrategy = BackpressureStrategy.BUFFER) =
    LiveDataReactiveStreams.fromPublisher<T>(this.toFlowable(strategy))

fun <T> Observable<T>.liveData(lifecycleOwner: LifecycleOwner, observer: (T?) -> Unit) =
    liveData().observe(lifecycleOwner) { observer(it) }