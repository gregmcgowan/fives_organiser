package com.gregmcgowan.fivesorganiser.core

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED
}

fun View.setVisibleOrGone(visible: Boolean) {
    if (visible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

inline fun <T> LiveData<T>.observeNonNull(
        owner: LifecycleOwner,
        crossinline observer: (T) -> Unit
) {
    this.observe(owner, { value ->
        value?.let {
            observer(it)
        }
    })
}

