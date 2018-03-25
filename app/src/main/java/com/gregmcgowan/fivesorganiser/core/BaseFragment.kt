package com.gregmcgowan.fivesorganiser.core

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.gregmcgowan.fivesorganiser.core.di.AppComponent

abstract class BaseFragment : Fragment() {

    val appComponent: AppComponent get() = getApp().appComponent

    fun getAppCompatActivity(): AppCompatActivity {
        return (requireActivity() as AppCompatActivity)
    }
}