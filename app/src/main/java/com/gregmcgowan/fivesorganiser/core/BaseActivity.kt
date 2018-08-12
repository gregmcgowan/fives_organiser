package com.gregmcgowan.fivesorganiser.core


import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import com.gregmcgowan.fivesorganiser.BuildConfig

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FragmentManager.enableDebugLogging(BuildConfig.DEBUG)
    }
}