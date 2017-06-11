package com.gregmcgowan.fivesorganiser

import android.app.Activity
import android.support.annotation.IdRes
import android.view.View
import android.view.ViewGroup


fun Activity.getApp() : FivesOrganiserApp = this.application as FivesOrganiserApp

@Suppress("UNCHECKED_CAST")
internal fun <T : View> Activity.find(@IdRes id: Int) : Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { findViewById(id) as T }

@Suppress("UNCHECKED_CAST")
internal fun <T : View> ViewGroup.find(@IdRes id: Int): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { findViewById(id) as T }