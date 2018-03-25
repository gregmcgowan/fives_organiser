package com.gregmcgowan.fivesorganiser.core

import android.app.Activity
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.FivesOrganiserApp


fun <T> MutableLiveData<T>.getNonNull(): T = this.value
        ?: throw IllegalStateException("live data cannot be null")

fun Activity.getApp(): FivesOrganiserApp = this.application as FivesOrganiserApp

fun Fragment.getApp(): FivesOrganiserApp = this.activity?.application as FivesOrganiserApp

@Suppress("UNCHECKED_CAST")
internal fun <T : View> Activity.find(
        @IdRes id: Int
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(id) }

@Suppress("UNCHECKED_CAST")
internal fun <T : View> Fragment.find(
        @IdRes id: Int
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { activity!!.findViewById<T>(id) }

@Suppress("UNCHECKED_CAST")
internal fun <T : View> ViewGroup.find(
        @IdRes id: Int
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(id) }

@Suppress("UNCHECKED_CAST")
internal fun <T : View> View.find(
        @IdRes id: Int
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(id) }

@Suppress("UNCHECKED_CAST")
internal fun <T : View> RecyclerView.ViewHolder.find(
        @IdRes id: Int
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { itemView.findViewById<T>(id) }

@Suppress("UNCHECKED_CAST")
internal fun <T : View> find(
        @IdRes id: Int,
        rootView: View
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { rootView.findViewById<T>(id) }

fun Spinner.setIfNotEqual(index : Int) {
    if(this.selectedItemPosition != index) {
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

fun TextView.setTextIfNotEqual(text: CharSequence) {
    if (this.text != text) {
        this.text = text
    }
}

fun EditText.setTextIfNotEqual(text: CharSequence) {
    if (this.text.toString() != text.toString()) {
        this.setText(text)
    }
}

fun View.setVisible(visible: Boolean) {
    if (visible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED
}

inline fun <T> LiveData<T>.observeNonNull(
        owner: LifecycleOwner,
        crossinline observer: (T) -> Unit
) {
    this.observe(owner, Observer {
        it?.let {
            observer(it)
        }
    })
}

