package com.gregmcgowan.fivesorganiser.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FragmentManager.enableDebugLogging(BuildConfig.DEBUG)
    }
}