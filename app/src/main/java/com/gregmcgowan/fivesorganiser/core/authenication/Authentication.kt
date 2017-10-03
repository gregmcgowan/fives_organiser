package com.gregmcgowan.fivesorganiser.core.authenication

import io.reactivex.Completable


interface Authentication {

    fun isInitialised(): Boolean
    fun initialise(): Completable
    fun getUserId(): String?
}