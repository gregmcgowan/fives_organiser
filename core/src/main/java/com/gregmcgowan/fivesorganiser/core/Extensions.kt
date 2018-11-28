package com.gregmcgowan.fivesorganiser.core

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView

fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED
}

fun Spinner.setIfNotEqual(index: Int) {
    if (this.selectedItemPosition != index) {
        this.setSelection(index)
    }
}

fun <T : Any> ArrayAdapter<T>.getItems(): List<T> {
    if (this.count == 0) {
        return emptyList()
    }
    return (0 until this.count).map { getItem(it) }
}

fun <T : Any> ArrayAdapter<T>.updateIfChanged(newValues: List<T>) {
    val existingItems = this.getItems()
    if (existingItems != newValues) {
        this.clear()
        this.addAll(newValues)
        this.notifyDataSetChanged()
    }
}

fun <T : TextView> T.setTextIfNotEqual(text: CharSequence) {
    if (this.text.toString() != text.toString()) {
        this.text = text
    }
}

fun <T : TextView> T.setTextIfValidRes(textResId: Int) {
    if (textResId != -1) {
        this.setText(textResId)
    }
}

fun View.setVisibleOrGone(visible: Boolean) {
    if (visible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

fun <T> LiveData<T>.requireValue(): T = this.value
        ?: throw IllegalStateException("live data cannot have null value")

fun <T : Any> MutableLiveData<T>.setIfDifferent(newValue: T) {
    if (newValue != this.value) {
        this.value = newValue
    }
}

inline fun <T> LiveData<T>.observeNonNull(
        owner: LifecycleOwner,
        crossinline observer: (T) -> Unit
) {
    this.observe(owner, Observer { value ->
        value?.let {
            observer(it)
        }
    })
}

