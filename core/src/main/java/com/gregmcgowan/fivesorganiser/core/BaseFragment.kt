package com.gregmcgowan.fivesorganiser.core

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    fun getAppCompatActivity(): AppCompatActivity {
        return (requireActivity() as AppCompatActivity)
    }
}