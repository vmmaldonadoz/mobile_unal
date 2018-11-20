package com.vmmaldonadoz.challenges.utils

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


fun <T> Observable<T>.subscribeOnIO(): Observable<T> {
    return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.subscribeOnComputation(): Observable<T> {
    return this.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
}


fun <T> Flowable<T>.subscribeOnIO(): Flowable<T> {
    return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Flowable<T>.subscribeOnComputation(): Flowable<T> {
    return this.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
}


fun Completable.subscribeOnIO(): Completable {
    return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

fun Completable.subscribeOnComputation(): Completable {
    return this.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
}
