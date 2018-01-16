package com.gregmcgowan.fivesorganiser.core.ui

import android.arch.lifecycle.MutableLiveData

class NonNullMutableLiveData<T>(initialValue: T) : MutableLiveData<T>() {

    init {
        value = initialValue
    }

    fun getNonNullValue(): T {
        value?.let {
            return it
        }
        throw IllegalStateException("value cannot be null")
    }

}