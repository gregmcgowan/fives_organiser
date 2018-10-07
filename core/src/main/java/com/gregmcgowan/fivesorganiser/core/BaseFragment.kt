package com.gregmcgowan.fivesorganiser.core

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

abstract class BaseFragment : Fragment() {

    fun getAppCompatActivity(): AppCompatActivity {
        return (requireActivity() as AppCompatActivity)
    }
}