package com.gregmcgowan.fivesorganiser.core

import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity

abstract class BaseFragment : androidx.fragment.app.Fragment() {

    fun getAppCompatActivity(): AppCompatActivity {
        return (requireActivity() as AppCompatActivity)
    }
}