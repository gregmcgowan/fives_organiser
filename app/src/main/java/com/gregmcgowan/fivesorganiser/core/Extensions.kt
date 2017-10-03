package com.gregmcgowan.fivesorganiser.core

import android.app.Activity
import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.gregmcgowan.fivesorganiser.FivesOrganiserApp


fun Activity.getApp(): FivesOrganiserApp = this.application as FivesOrganiserApp

@Suppress("UNCHECKED_CAST")
internal fun <T : View> Activity.find(@IdRes id: Int): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(id) }

@Suppress("UNCHECKED_CAST")
internal fun <T : View> ViewGroup.find(@IdRes id: Int): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(id) }

@Suppress("UNCHECKED_CAST")
internal fun <T : View> View.find(@IdRes id: Int): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(id) }

@Suppress("UNCHECKED_CAST")
internal fun <T : View> RecyclerView.ViewHolder.find(@IdRes id: Int): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { itemView.findViewById<T>(id) }

@Suppress("UNCHECKED_CAST")
internal fun <T : View> find(@IdRes id: Int, rootView: View): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { rootView.findViewById<T>(id) }

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

fun View.setVisible(visible : Boolean) {
    if(visible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}
